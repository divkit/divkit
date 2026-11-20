package com.yandex.div.compose.views.transition

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.requireLayoutCoordinates
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.views.divVisibility
import com.yandex.div2.DivBase
import com.yandex.div2.DivVisibility

internal data class VisibilityChangeTransition(
    val visibility: DivVisibility,
    val appearance: StateChangeTransitionModifier? = null,
    val isExiting: Boolean = false,
)

/**
 * Adds a running visibility transition to the state change one. A state snapshot draws the element
 * through its own layer, so the transition alpha is applied there, not for the second time here.
 */
internal fun StateChangeTransitionModifier.withVisibilityTransition(
    transition: VisibilityChangeTransition?,
    blocksPointerInput: Boolean,
): StateChangeTransitionModifier {
    val appearance = transition?.appearance ?: return this
    return copy(
        modifier = modifier
            // Exiting content is still drawn, but it is hidden for input: an open select menu
            // closes, and neither the element nor its descendants can be focused or reopened.
            .applyIf(transition.isExiting) { divVisibility(DivVisibility.INVISIBLE) }
            .applyIf(transition.isExiting && appearance.appearanceAlpha == 0f) {
                then(RetainUnplacedContent)
            }
            .then(appearance.modifier)
            .applyIf(transition.isExiting && blocksPointerInput) { then(BlockPointerInput) },
        appearanceAlpha = if (snapshotId == null) appearance.appearanceAlpha else appearanceAlpha,
    )
}

// Keep the exit's layout and animation lifetime without intercepting taps behind invisible content.
private val RetainUnplacedContent = Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {}
}

private val BlockPointerInput = Modifier.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
        }
    }
}

@Composable
internal fun rememberVisibilityChangeTransition(
    data: DivBase,
    visibility: DivVisibility,
    hasLayoutWrapper: Boolean = false,
): VisibilityChangeTransition {
    val component = LocalComponent.current
    val retention = component.visibilityTransitionStorage.get(data, visibility)
    val history = remember {
        VisibilityChangeHistory(
            if (visibility == DivVisibility.VISIBLE && retention.isObserved) {
                retention.renderedVisibility
            } else {
                visibility
            }
        )
    }
    DisposableEffect(retention) {
        retention.attach(visibility)
        onDispose { retention.detach() }
    }
    if (!LocalStateChangeTransitionContext.current.contentVisible ||
        !component.animationConfiguration.isEnabledAsState()
    ) {
        SideEffect {
            history.visibility = visibility
            history.visualState = null
            retention.renderedVisibility = visibility
        }
        return VisibilityChangeTransition(visibility)
    }

    val visible = visibility == DivVisibility.VISIBLE
    val result = key(visible) {
        VisibilityChangeAppearance(data, visibility, history, hasLayoutWrapper)
    }
    SideEffect {
        history.visibility = visibility
        retention.renderedVisibility = result.visibility
    }
    return result
}

private class VisibilityChangeHistory(var visibility: DivVisibility) {
    var visualState: StateChangeVisualState? = null
}

@Composable
private fun VisibilityChangeAppearance(
    data: DivBase,
    visibility: DivVisibility,
    history: VisibilityChangeHistory,
    hasLayoutWrapper: Boolean,
): VisibilityChangeTransition {
    val isExiting = visibility != DivVisibility.VISIBLE
    val appearance = if (isExiting) data.transitionOut else data.transitionIn
    val shouldAnimate = remember {
        (history.visibility == DivVisibility.VISIBLE) != !isExiting && appearance != null
    }
    if (!shouldAnimate || appearance == null) {
        SideEffect { history.visualState = null }
        return VisibilityChangeTransition(visibility)
    }

    val transitionState = remember {
        MutableTransitionState(if (isExiting) EnterExitState.Visible else EnterExitState.PreEnter)
            .apply {
                targetState = if (isExiting) EnterExitState.PostExit else EnterExitState.Visible
            }
    }
    val transition = rememberTransition(transitionState, label = "Div visibility")
    if (transitionState.isIdle) {
        SideEffect { history.visualState = null }
        return VisibilityChangeTransition(visibility)
    }
    val exitValues = remember { history.visualState.takeIf { isExiting } }
    val baseAlpha = data.alpha.observedFloatValue()
    val animation = appearance.animatedAppearance(
        transition = transition,
        transitionState = if (isExiting) EnterExitState.PostExit else EnterExitState.PreEnter,
        baseAlpha = baseAlpha,
        exitValues = exitValues,
        slideGeometry = null,
        label = data.id.orEmpty(),
    )
    val visualState = animation.resolveVisualState(
        sceneSize = IntSize.Zero,
        bounds = ElementBounds.Zero,
        baseState = exitValues ?: StateChangeVisualState(baseAlpha),
        isExiting = isExiting,
        retainFinishedValues = isExiting,
    )
    return VisibilityChangeTransition(
        DivVisibility.VISIBLE,
        StateChangeTransitionModifier(
            visibilityAnimationModifier(animation, visualState, isExiting, history, hasLayoutWrapper),
            appearanceAlpha = visualState.alpha,
        ),
        isExiting = isExiting,
    )
}

@Composable
private fun visibilityAnimationModifier(
    animation: AnimatedAppearance,
    visualState: StateChangeVisualState,
    isExiting: Boolean,
    history: VisibilityChangeHistory,
    hasLayoutWrapper: Boolean,
): Modifier {
    if (animation.scales.isEmpty() && animation.slides.isEmpty() && !visualState.hasTransform) {
        SideEffect { history.visualState = visualState }
        return Modifier
    }
    val geometry = remember { mutableStateOf<SlideGeometry?>(null) }
    SideEffect {
        val translation = animation.resolveTranslation(
            visualState, geometry.value, isExiting, retainFinishedValues = isExiting,
        )
        history.visualState = visualState.copy(
            translationX = translation.x,
            translationY = translation.y,
        )
    }
    return Modifier
        .applyIf(animation.slides.isNotEmpty()) {
            then(SlideGeometryElement(geometry, hasLayoutWrapper))
        }
        .graphicsLayer {
            val translation = animation.resolveTranslation(
                visualState, geometry.value, isExiting, retainFinishedValues = isExiting,
            )
            scaleX = visualState.scale
            scaleY = visualState.scale
            translationX = translation.x
            translationY = translation.y
            transformOrigin = TransformOrigin(visualState.pivotX, visualState.pivotY)
        }
}

/**
 * Marks the element as the one its scrollable children slide from: they are laid out in service
 * layouts, whose bounds differ from the bounds of the element.
 */
internal fun Modifier.slideTransitionParent(): Modifier = then(SlideParentElement)

private data object SlideParentElement : ModifierNodeElement<SlideParentNode>() {
    override fun create() = SlideParentNode()

    override fun update(node: SlideParentNode) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "slideTransitionParent"
    }
}

private class SlideParentNode : Modifier.Node(), TraversableNode {
    override val traverseKey = SlideParentElement
}

private data class SlideGeometryElement(
    val geometry: MutableState<SlideGeometry?>,
    val hasLayoutWrapper: Boolean,
) : ModifierNodeElement<SlideGeometryNode>() {
    override fun create() = SlideGeometryNode(geometry, hasLayoutWrapper)

    override fun update(node: SlideGeometryNode) {
        node.geometry = geometry
        node.hasLayoutWrapper = hasLayoutWrapper
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "slideGeometry"
    }
}

private class SlideGeometryNode(
    var geometry: MutableState<SlideGeometry?>,
    var hasLayoutWrapper: Boolean,
) : Modifier.Node(), GlobalPositionAwareModifierNode {
    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val markedParent = if (hasLayoutWrapper) findNearestAncestor(SlideParentElement) else null
        val parent = markedParent?.requireLayoutCoordinates()
            ?: coordinates.parentLayoutCoordinates
            ?: return
        geometry.value = SlideGeometry(
            parent.size,
            ElementBounds(parent.localPositionOf(coordinates, Offset.Zero), coordinates.size),
        )
    }
}
