package com.yandex.div.compose.views.transition

import androidx.compose.animation.EnterExitState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.GraphicsContext
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.unit.IntOffset
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div2.DivAppearanceTransition
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

@Composable
internal fun BindStateChangeSnapshots(cache: StateChangeSnapshotCache) {
    val graphicsContext = LocalGraphicsContext.current
    val coroutineScope = rememberCoroutineScope()
    val reporter = LocalComponent.current.reporter
    cache.layerStore = remember(graphicsContext, coroutineScope, reporter) {
        StateChangeLayerStore(graphicsContext, coroutineScope, reporter)
    }
}

internal class StateChangeLayerStore(
    private val context: GraphicsContext,
    private val coroutineScope: CoroutineScope,
    private val reporter: DivReporter,
) : RememberObserver {
    private val layers = mutableMapOf<String, GraphicsLayer>()
    private val frozenLayers = mutableSetOf<GraphicsLayer>()
    private val freezeJobs = mutableMapOf<String, Job>()
    private var released = false

    fun layer(id: String): GraphicsLayer = layers.getOrPut(id, context::createGraphicsLayer)

    fun freeze(snapshot: StateChangeSnapshot, layout: StateChangeLayoutState?) {
        val version = ++snapshot.captureVersion
        val previousJob = freezeJobs[snapshot.id]
        val size = snapshot.layer.size
        if (size.width == 0 || size.height == 0) return

        val captureArea = snapshot.captureArea(size, layout)
        val frozenLayer = snapshot.frozen?.layer ?: context.createGraphicsLayer().also {
            frozenLayers += it
        }
        snapshot.frozen = StateChangeFrozenSnapshot(frozenLayer, captureArea.offset)
        freezeJobs[snapshot.id] = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            previousJob?.cancelAndJoin()
            val captureLayer = context.createGraphicsLayer()
            try {
                val image = snapshot.freezeImage(captureLayer, frozenLayer, captureArea)
                if (released || snapshot.captureVersion != version) return@launch
                frozenLayer.record(image)
            } catch (error: CancellationException) {
                throw error
            } catch (error: IllegalStateException) {
                reporter.reportError(error)
            } finally {
                context.releaseGraphicsLayer(captureLayer)
            }
        }
    }

    fun release(snapshot: StateChangeSnapshot) {
        ++snapshot.captureVersion
        freezeJobs.remove(snapshot.id)?.cancel()
        releaseFrozen(snapshot)
        snapshot.frozen = null
    }

    private fun releaseFrozen(snapshot: StateChangeSnapshot) {
        snapshot.frozen?.layer?.let(::releaseFrozen)
    }

    private fun releaseFrozen(layer: GraphicsLayer) {
        if (frozenLayers.remove(layer)) context.releaseGraphicsLayer(layer)
    }

    override fun onRemembered() = Unit

    override fun onForgotten() = release()

    override fun onAbandoned() = release()

    private fun release() {
        released = true
        freezeJobs.values.forEach(Job::cancel)
        layers.values.forEach(context::releaseGraphicsLayer)
        frozenLayers.forEach(context::releaseGraphicsLayer)
        layers.clear()
        frozenLayers.clear()
        freezeJobs.clear()
    }
}

internal data class StateChangeSnapshot(
    val id: String,
    val parentId: String?,
    val layer: GraphicsLayer,
    val transition: DivAppearanceTransition?,
    val baseAlpha: Float,
    val capturesScene: Boolean = false,
) {
    var captureVersion = 0
    var frozen by mutableStateOf<StateChangeFrozenSnapshot?>(null)
}

internal data class StateChangeFrozenSnapshot(
    val layer: GraphicsLayer,
    val offset: IntOffset,
)

internal class StateChangeSnapshotCache {
    var layerStore: StateChangeLayerStore? = null
    private val snapshots = mutableMapOf<String, StateChangeSnapshot>()
    private val coordinates = mutableMapOf<String, LayoutCoordinates>()
    private val layouts = mutableMapOf<String, StateChangeLayoutState>()
    var exiting by mutableStateOf(emptyList<StateChangeSnapshot>())
        private set

    fun update(snapshot: StateChangeSnapshot) {
        snapshots[snapshot.id] = snapshot
    }

    fun capture(exitingIds: Set<String>, commonIds: Set<String>) {
        exiting = snapshots.values.filter { snapshot ->
            snapshot.id in exitingIds && snapshot.transition != null &&
                generateSequence(snapshot.parentId) { snapshots[it]?.parentId }
                    .takeWhile { it !in commonIds }
                    .none { it in exitingIds && snapshots[it]?.transition != null }
        }
        commonIds.forEach { snapshots[it]?.layer?.alpha = 0f }
        exiting.forEach { snapshot ->
            layerStore?.freeze(snapshot, layouts[snapshot.id])
        }
    }

    fun remove(ids: Set<String>) {
        exiting.filter { it.id in ids }.forEach { layerStore?.release(it) }
        exiting = exiting.filterNot { it.id in ids }
    }

    fun updateCoordinates(id: String, value: LayoutCoordinates) {
        coordinates[id] = value
    }

    fun updateLayout(id: String, value: StateChangeLayoutState) {
        layouts[id] = value
    }

    fun updateLayouts(scene: StateChangeTransitionScene) {
        val sceneCoordinates = scene.coordinates ?: return
        coordinates.forEach { (id, coordinates) ->
            if (coordinates.isAttached) {
                layouts[id] = StateChangeLayoutState(
                    sceneCoordinates.size,
                    ElementBounds(sceneCoordinates.localPositionOf(coordinates, Offset.Zero), coordinates.size),
                    coordinates.positionInRoot(),
                )
            }
        }
    }

    fun layout(id: String): StateChangeLayoutState? = layouts[id]
}

@Composable
internal fun StateChangeSnapshots(scope: StateChangeTransitionScope) {
    for (snapshot in scope.transitionState.snapshots.exiting) {
        val layout = scope.transitionState.getExitLayout(snapshot.id) ?: continue
        key(snapshot.id) {
            val initial = scope.transitionState.getExitValues(snapshot.id)
                ?: StateChangeVisualState(snapshot.baseAlpha)
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
            RegisterOverlayLayer(
                scope,
                snapshot.id,
                StateChangeAnimation(appearance, visualState, layout.slideGeometry, layout, true),
                snapshot.frozen?.layer ?: snapshot.layer,
                snapshot.frozen?.offset ?: IntOffset.Zero,
            )
        }
    }
}
