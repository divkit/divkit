package com.yandex.div.compose.views.transition

import androidx.compose.animation.EnterExitState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivBase
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivVisibility.VISIBLE

internal data class StateChangeTransitionModifier(
    val modifier: Modifier = Modifier,
    val appearanceAlpha: Float? = null,
    val capturesOverlay: Boolean = false,
    val releasesOverlayCapture: Boolean = false,
    val snapshotModifier: Modifier = Modifier,
    val snapshotNode: StateChangeSnapshotNode? = null,
    val boundsSizeModifier: Modifier = Modifier,
)

private data class StateChangeGeometry(
    val modifier: Modifier,
    val slideGeometry: State<SlideGeometry?>,
)

internal data class StateChangeAnimation(
    val appearance: AnimatedAppearance,
    val visualState: StateChangeVisualState,
    val slideGeometry: SlideGeometry?,
    val exitLayout: StateChangeLayoutState?,
    val isExiting: Boolean,
)

@Composable
internal fun Modifier.applyStateChangeTransition(
    data: DivBase,
    visibility: DivVisibility,
    capturesScene: Boolean = false,
    alphaOverride: Float? = null,
): StateChangeTransitionModifier {
    val transitionContext = LocalStateChangeTransitionContext.current
    val scope = transitionContext.scope ?: return StateChangeTransitionModifier(this)
    val id = data.id ?: return StateChangeTransitionModifier(this)
    val ancestorOverlayCaptured = transitionContext.overlayCaptured
    val releasesOverlayCapture = !transitionContext.contentActive && ancestorOverlayCaptured && id in scope.commonIds
    val snapshot = rememberStateChangeSnapshot(data, visibility, scope, id, capturesScene, alphaOverride)
    if (data.transitionTriggers?.contains(DivTransitionTrigger.STATE_CHANGE) == false) {
        val modifier = if (scope.scene.lookaheadScope == null) this else {
            withStateChangeGeometry(scope, id, snapshot?.node).modifier
        }
        return StateChangeTransitionModifier(
            modifier = modifier,
            appearanceAlpha = 0f.takeIf { releasesOverlayCapture },
            releasesOverlayCapture = releasesOverlayCapture,
        ).withSnapshot(snapshot, StateChangeVisualState(snapshot?.baseAlpha ?: 0f))
    }

    val visible = transitionContext.contentActive && transitionContext.contentVisible && visibility == VISIBLE
    val bounds = stateChangeBoundsModifiers(scope, id, snapshot?.boundsSpecs, visible)
    val geometry = then(bounds?.content ?: Modifier).withStateChangeGeometry(scope, id, snapshot?.node)
    val isExiting = id in scope.exitingIds
    val baseAlpha = alphaOverride
        ?: if (visibility == DivVisibility.VISIBLE) data.alpha.observedFloatValue() else 0f
    val cachedLayer = rememberCachedTransitionLayer(snapshot, ancestorOverlayCaptured, data.transitionOut)
    val transition = data.selectStateChangeTransition(id in scope.enteringIds, isExiting)
    val appearance = if (transition == null) {
        SideEffect { scope.transitionState.updateCurrent(id, StateChangeVisualState(baseAlpha)) }
        StateChangeTransitionModifier(
            modifier = geometry.modifier.cacheStateChangeContent(cachedLayer),
            appearanceAlpha = 0f.takeIf { releasesOverlayCapture },
            releasesOverlayCapture = releasesOverlayCapture,
        ).withSnapshot(snapshot, StateChangeVisualState(baseAlpha))
    } else {
        val animation = resolveStateChangeAnimation(
            transition = transition,
            data = data,
            visibility = visibility,
            scope = scope,
            id = id,
            slideGeometry = geometry.slideGeometry.value,
            isExiting = isExiting,
        )
        if (snapshot == null) {
            geometry.modifier.applyStateChangeAnimation(scope, id, animation, cachedLayer)
        } else {
            StateChangeTransitionModifier(geometry.modifier).withSnapshot(
                snapshot,
                animation.visualState.copy(alpha = animation.resolveAppearanceAlpha(false)),
            )
        }
    }
    // Div sizing sits between the two bounds modifiers, so the size one is applied apart from the content one.
    return appearance.copy(boundsSizeModifier = bounds?.size ?: Modifier)
}

@Composable
private fun rememberCachedTransitionLayer(
    snapshot: StateChangeSnapshot?,
    ancestorOverlayCaptured: Boolean,
    transitionOut: DivAppearanceTransition?,
): GraphicsLayer? = if (snapshot == null && !ancestorOverlayCaptured && transitionOut != null) {
    rememberGraphicsLayer()
} else {
    null
}

private fun DivBase.selectStateChangeTransition(
    isEntering: Boolean,
    isExiting: Boolean,
): DivAppearanceTransition? = when {
    isExiting -> transitionOut
    isEntering -> transitionIn
    else -> null
}

@Composable
private fun StateChangeTransitionModifier.withSnapshot(
    snapshot: StateChangeSnapshot?,
    visualState: StateChangeVisualState,
): StateChangeTransitionModifier {
    if (snapshot == null) return this
    SideEffect { snapshot.node.visualState = visualState }
    return copy(
        appearanceAlpha = 1f,
        snapshotModifier = Modifier.cacheStateChangeContent(snapshot.layer, visualState),
        snapshotNode = snapshot.node,
    )
}

@Composable
private fun rememberStateChangeSnapshot(
    data: DivBase,
    visibility: DivVisibility,
    scope: StateChangeTransitionScope,
    id: String,
    capturesScene: Boolean,
    alphaOverride: Float?,
): StateChangeSnapshot? {
    val parent = LocalStateChangeTransitionContext.current.snapshotParent
    val cache = scope.transitionState.snapshots
    val store = cache.layerStore ?: return null
    val node = remember(store, id, parent) { StateChangeSnapshotNode(id, parent, cache, store) }
    val enabled = data.transitionTriggers?.contains(DivTransitionTrigger.STATE_CHANGE) != false
    val snapshot = StateChangeSnapshot(
        node = node,
        transition = if (enabled) data.transitionOut?.frozenAppearance() else null,
        boundsSpecs = if (enabled && scope.scene.lookaheadScope != null) {
            data.transitionChange?.observedBoundsSpecs()
        } else null,
        baseAlpha = alphaOverride
            ?: if (visibility == DivVisibility.VISIBLE) data.alpha.observedFloatValue() else 0f,
        capturesScene = capturesScene,
        visible = visibility == VISIBLE && LocalStateChangeTransitionContext.current.contentVisible,
    )
    SideEffect { cache.update(snapshot) }
    return snapshot
}

@Composable
private fun Modifier.withStateChangeGeometry(
    scope: StateChangeTransitionScope,
    id: String,
    node: StateChangeSnapshotNode?,
): StateChangeGeometry {
    val contentActive = LocalStateChangeTransitionContext.current.contentActive
    val slideGeometry = remember(scope.scene, id) { mutableStateOf<SlideGeometry?>(null) }
    val geometryModifier = Modifier.onGloballyPositioned { coordinates ->
        node?.coordinates = coordinates
        val sceneCoordinates = scope.scene.coordinates ?: return@onGloballyPositioned
        val position = sceneCoordinates.localPositionOf(coordinates, Offset.Zero)
        val layout = StateChangeLayoutState(
            sceneSize = sceneCoordinates.size,
            bounds = ElementBounds(position, coordinates.size),
            positionInRoot = coordinates.positionInRoot(),
        )
        node?.layout = layout
        scope.transitionState.snapshots.updateLayout(id, layout)
        slideGeometry.value = layout.slideGeometry
        if (contentActive) {
            scope.transitionState.updateCurrentLayout(id, layout)
        }
    }
    return StateChangeGeometry(then(geometryModifier), slideGeometry)
}

@Composable
private fun resolveStateChangeAnimation(
    transition: DivAppearanceTransition,
    data: DivBase,
    visibility: DivVisibility,
    scope: StateChangeTransitionScope,
    id: String,
    slideGeometry: SlideGeometry?,
    isExiting: Boolean,
): StateChangeAnimation {
    val baseAlpha = if (visibility == DivVisibility.VISIBLE) {
        data.alpha.observedFloatValue()
    } else {
        0f
    }
    val exitValues = scope.transitionState.getExitValues(id).takeIf { isExiting }
    val exitLayout = scope.transitionState.getExitLayout(id).takeIf { isExiting }
    val resolvedSlideGeometry = exitLayout?.slideGeometry ?: slideGeometry
    val animation = transition.animatedAppearance(
        scope = if (isExiting) scope.exitScope else scope.enterScope,
        transitionState = if (isExiting) EnterExitState.PostExit else EnterExitState.PreEnter,
        baseAlpha = baseAlpha,
        exitValues = exitValues,
        slideGeometry = resolvedSlideGeometry,
        label = id,
    )
    val visualState = animation.resolveVisualState(
        sceneSize = resolvedSlideGeometry?.sceneSize ?: scope.scene.size,
        bounds = resolvedSlideGeometry?.bounds ?: ElementBounds.Zero,
        baseState = exitValues ?: StateChangeVisualState(alpha = baseAlpha),
        isExiting = isExiting,
    )
    SideEffect {
        scope.transitionState.updateCurrent(id, visualState)
    }
    return StateChangeAnimation(
        appearance = animation,
        visualState = visualState,
        slideGeometry = resolvedSlideGeometry,
        exitLayout = exitLayout,
        isExiting = isExiting,
    )
}

@Composable
private fun Modifier.applyStateChangeAnimation(
    scope: StateChangeTransitionScope,
    id: String,
    animation: StateChangeAnimation,
    cachedLayer: GraphicsLayer?,
): StateChangeTransitionModifier {
    val transitionModifier = createTransitionModifier(
        animation = animation.appearance,
        visualState = animation.visualState,
        slideGeometry = animation.slideGeometry,
        isExiting = animation.isExiting,
    )
    val capturesOverlay = animation.isExiting && cachedLayer != null && animation.exitLayout != null
    val overlayLayer = cachedLayer.takeIf { capturesOverlay }
    RegisterOverlayLayer(scope, id, animation, overlayLayer)
    val cacheModifier = Modifier.cacheStateChangeContent(cachedLayer)
    return StateChangeTransitionModifier(
        modifier = then(cacheModifier)
            .applyIf(!capturesOverlay) { then(transitionModifier) },
        appearanceAlpha = animation.resolveAppearanceAlpha(capturesOverlay),
        capturesOverlay = capturesOverlay,
    )
}

internal fun StateChangeAnimation.resolveAppearanceAlpha(capturesOverlay: Boolean): Float {
    if (capturesOverlay) return 1f

    val slide = appearance.dominantSlide()
    return if (slide != null && slide.distance == null && slideGeometry == null) {
        0f
    } else {
        visualState.alpha
    }
}

private fun createTransitionModifier(
    animation: AnimatedAppearance,
    visualState: StateChangeVisualState,
    slideGeometry: SlideGeometry?,
    isExiting: Boolean,
): Modifier {
    return Modifier.applyIf(animation.hasTransform || visualState.hasTransform) {
        graphicsLayer {
            val translation = animation.resolveTranslation(
                visualState = visualState,
                slideGeometry = slideGeometry,
                isExiting = isExiting,
            )
            scaleX = visualState.scale
            scaleY = visualState.scale
            translationX = translation.x
            translationY = translation.y
            transformOrigin = TransformOrigin(visualState.pivotX, visualState.pivotY)
        }
    }
}

internal fun AnimatedAppearance.resolveTranslation(
    visualState: StateChangeVisualState,
    slideGeometry: SlideGeometry?,
    isExiting: Boolean,
    retainFinishedValues: Boolean = false,
): Offset {
    val slide = dominantSlide(retainFinishedValues)
    return if (slide == null) {
        Offset(visualState.translationX, visualState.translationY)
    } else {
        slide.resolveTranslation(
            sceneSize = slideGeometry?.sceneSize ?: IntSize.Zero,
            bounds = slideGeometry?.bounds ?: ElementBounds.Zero,
            isExiting = isExiting,
        )
    }
}

private val AnimatedAppearance.hasTransform: Boolean
    get() = scales.isNotEmpty() || slides.isNotEmpty()

internal val StateChangeVisualState.hasTransform: Boolean
    get() = scale != 1f || translationX != 0f || translationY != 0f

