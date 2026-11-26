package com.yandex.div.compose.views.transition

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import com.yandex.div2.DivAppearanceTransition
import kotlinx.coroutines.launch

internal class StateChangeSnapshotNode(
    val id: String,
    val parent: StateChangeSnapshotNode?,
    private val cache: StateChangeSnapshotCache,
    private val store: StateChangeLayerStore,
) : RememberObserver {
    val layer = store.createLayer()
    var coordinates: LayoutCoordinates? = null
    var layout: StateChangeLayoutState? = null
    var visualState = StateChangeVisualState(1f)

    override fun onRemembered() = Unit

    override fun onForgotten() {
        coordinates = null
        cache.detach(this)
    }

    override fun onAbandoned() = releaseLayer()

    fun releaseLayer() = store.releaseLayer(layer)
}

internal data class StateChangeSnapshot(
    val node: StateChangeSnapshotNode,
    val transition: DivAppearanceTransition?,
    val baseAlpha: Float,
    val capturesScene: Boolean = false,
    val visible: Boolean = true,
    val boundsSpecs: List<FiniteAnimationSpec<Float>>? = null,
) {
    val id: String get() = node.id
    val layer: GraphicsLayer get() = node.layer
    var captureVersion = 0
    var frozen by mutableStateOf<StateChangeFrozenSnapshot?>(null)
}

internal data class StateChangeFrozenSnapshot(
    val layer: GraphicsLayer,
    val offset: IntOffset,
)

internal class StateChangeSnapshotCache {
    var layerStore: StateChangeLayerStore? = null
    private val snapshots = mutableMapOf<StateChangeSnapshotNode, StateChangeSnapshot>()
    private val layouts = mutableMapOf<String, StateChangeLayoutState>()
    private var retainingExit = false
    private val detachedNodes = mutableSetOf<StateChangeSnapshotNode>()
    var pendingIds by mutableStateOf(emptySet<String>())
        private set
    var exiting by mutableStateOf(emptyList<StateChangeSnapshot>())
        private set

    fun update(snapshot: StateChangeSnapshot) {
        snapshots[snapshot.node] = snapshot
    }

    // onForgotten precedes the state switch's SideEffect. Retain the last drawn frame until that
    // commit has had a chance to capture it, then release items removed from a still-active scene.
    fun detach(node: StateChangeSnapshotNode) {
        if (retainingExit) return
        detachedNodes += node
        if (detachedNodes.size == 1) layerStore?.coroutineScope?.launch { releaseDetachedNodes() }
    }

    private fun releaseDetachedNodes() {
        if (!retainingExit) detachedNodes.forEach { node ->
            snapshots.remove(node)
            if (layouts[node.id] === node.layout) layouts.remove(node.id)
            node.releaseLayer()
        }
        detachedNodes.clear()
    }

    fun capture(
        exitingIds: Set<String>,
        commonIds: Set<String>,
        runtimeMatches: Set<String>? = null,
        pendingIds: Set<String> = emptySet(),
    ) {
        retainingExit = true
        this.pendingIds = pendingIds
        val matchingNodes = snapshots.values.asSequence()
            .filter { it.visible && it.id in commonIds }
            .groupBy { it.id }.values
            .mapNotNullTo(mutableSetOf()) { it.singleOrNull()?.node }
        val affectedParents = matchingNodes.asSequence()
            .filter { runtimeMatches != null && it.id in runtimeMatches }
            .flatMap { it.ancestors() }
            .toSet()
        val keepFrozen = if (runtimeMatches == null) emptySet() else exiting.mapTo(mutableSetOf()) { it.node }
        exiting = snapshots.values.filter { snapshot ->
            snapshot.visible && snapshot.id in exitingIds && snapshot.transition != null &&
                snapshot.node.ancestors()
                    .takeWhile { it !in matchingNodes }
                    .none { it.id in exitingIds && snapshots[it]?.transition != null }
        }
        matchingNodes.forEach { it.layer.alpha = 0f }
        exiting.filterNot { it.id in pendingIds }.forEach { snapshot ->
            if (snapshot.captureVersion == 0 || snapshot.node !in keepFrozen || snapshot.node in affectedParents) {
                layerStore?.freeze(snapshot, snapshot.node.layout)
            }
        }
    }

    private fun StateChangeSnapshotNode.ancestors() = generateSequence(parent) { it.parent }

    fun remove(ids: Set<String>) {
        exiting.filter { it.id in ids }.forEach { layerStore?.release(it) }
        exiting = exiting.filterNot { it.id in ids }
    }

    fun updateLayout(id: String, value: StateChangeLayoutState) {
        layouts[id] = value
    }

    fun updateLayouts(scene: StateChangeTransitionScene) {
        val sceneCoordinates = scene.coordinates ?: return
        snapshots.keys.forEach { node ->
            val coordinates = node.coordinates
            if (coordinates != null && coordinates.isAttached) {
                val layout = StateChangeLayoutState(
                    sceneCoordinates.size,
                    ElementBounds(sceneCoordinates.localPositionOf(coordinates, Offset.Zero), coordinates.size),
                    coordinates.positionInRoot(),
                )
                node.layout = layout
                layouts[node.id] = layout
            }
        }
    }

    fun captureBounds(
        ids: Set<String>,
        scene: LayoutCoordinates? = null,
    ): Map<String, StateChangeBoundsStart> = buildMap {
        val root = scene?.takeIf { it.isAttached }?.findRootCoordinates()
        val sources = snapshots.values.asSequence().filter { it.visible && it.id in ids && it.node !in detachedNodes }
        for (source in sources) {
            val layout = source.node.layout ?: continue
            val bounds = layout.bounds
            // The scene-local rectangle includes the offset of a gallery item. Ignore only scrolling above the scene.
            val position = if (root != null) {
                root.localPositionOf(scene, Offset(bounds.left, bounds.top), includeMotionFrameOfReference = false)
            } else layout.positionInRoot
            put(source.id, StateChangeBoundsStart(bounds, position, source.boundsSpecs))
        }
    }

    fun layout(id: String): StateChangeLayoutState? = layouts[id]

    fun snapshot(id: String): StateChangeSnapshot? = snapshots.values.singleOrNull { it.id == id }
}

@Composable
internal fun StateChangeSnapshots(scope: StateChangeTransitionScope) {
    for (snapshot in scope.transitionState.snapshots.exiting) {
        val layout = snapshot.node.layout ?: continue
        key(snapshot.node) {
            val initial = snapshot.node.visualState
            val appearance = requireNotNull(snapshot.transition).animatedAppearance(
                scope = scope.exitScope,
                transitionState = EnterExitState.PostExit,
                baseAlpha = snapshot.baseAlpha,
                exitValues = initial,
                slideGeometry = layout.slideGeometry,
                label = snapshot.id,
            )
            val visualState = appearance.resolveVisualState(
                sceneSize = layout.sceneSize,
                bounds = layout.bounds,
                baseState = initial,
                isExiting = true,
            )
            if (snapshot.id !in scope.transitionState.snapshots.pendingIds) {
                RegisterOverlayLayer(
                    scope,
                    snapshot.id,
                    StateChangeAnimation(appearance, visualState, layout.slideGeometry, layout, true),
                    snapshot.frozen?.layer ?: snapshot.layer,
                    snapshot.frozen?.offset ?: IntOffset.Zero,
                    owner = snapshot.node,
                )
            }
        }
    }
}
