package com.yandex.div.compose.views.transition

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.approachLayout
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.round
import androidx.compose.ui.util.lerp
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.children
import com.yandex.div.compose.utils.reportWarning
import com.yandex.div.compose.utils.toEasing
import com.yandex.div2.Div
import com.yandex.div2.DivChangeTransition
import com.yandex.div2.DivState
import kotlin.math.roundToInt

@Composable
internal fun WithChangeBoundsScope(enabled: Boolean, content: @Composable (LookaheadScope?) -> Unit) {
    if (enabled) {
        LookaheadScope { content(this) }
    } else {
        content(null)
    }
}

@Composable
internal fun DivState.hasChangeBounds(transitionsAvailable: Boolean): Boolean = remember(transitionsAvailable, states) {
    transitionsAvailable && states.any { it.div?.containsChangeBounds() == true }
}

private fun Div.containsChangeBounds(): Boolean {
    val data = value()
    if (data.transitionChange != null) return true
    val builder = when (this) {
        is Div.Container -> value.itemBuilder
        is Div.Gallery -> value.itemBuilder
        is Div.Pager -> value.itemBuilder
        is Div.State -> return false
        is Div.Custom, is Div.GifImage, is Div.Grid, is Div.Image, is Div.Indicator, is Div.Input,
        is Div.Select, is Div.Separator, is Div.Slider, is Div.Switch, is Div.Tabs, is Div.Text, is Div.Video -> null
    }
    return builder?.prototypes.orEmpty().any { it.div.containsChangeBounds() } ||
        children().any { it.containsChangeBounds() }
}

@Composable
internal fun DivChangeTransition.observedBoundsSpecs(): List<FiniteAnimationSpec<Float>> = when (this) {
    is DivChangeTransition.Bounds -> listOf(boundsTiming(
        value.duration.observedIntValue().coerceAtLeast(0),
        value.startDelay.observedIntValue().coerceAtLeast(0),
        value.interpolator.observedValue().toEasing(),
    ))
    is DivChangeTransition.Set -> value.items.flatMap { it.observedBoundsSpecs() }.distinct()
}

// A zero duration has no curve, so such timings in a change-set differ only by their delays.
internal fun boundsTiming(durationMillis: Int, delayMillis: Int, easing: Easing): FiniteAnimationSpec<Float> =
    if (durationMillis == 0) snap(delayMillis) else tween(durationMillis, delayMillis, easing)

internal class StateChangeBoundsModifiers(val size: Modifier, val content: Modifier)

@Composable
internal fun stateChangeBoundsModifiers(
    scope: StateChangeTransitionScope,
    id: String,
    targetSpecs: List<FiniteAnimationSpec<Float>>?,
    visible: Boolean,
): StateChangeBoundsModifiers? {
    if (!visible) return null
    val lookahead = scope.scene.lookaheadScope ?: return null
    val enter = scope.enterScope.transition
    if (enter.currentState == EnterExitState.Visible && enter.targetState == EnterExitState.Visible) return null
    val source = remember(scope.transitionState, id) { scope.transitionState.boundsMatching?.source(id) } ?: return null
    if (!source.targetIsUnique) return null
    val specs = targetSpecs ?: source.specs ?: return null
    val spec = specs.singleOrNull()
    if (spec == null) {
        reportWarning("Conflicting change_bounds timings for element '$id'")
        return null
    }
    val progress by enter.animateFloat(
        transitionSpec = { spec },
        label = "Div state bounds $id",
    ) { if (it == EnterExitState.PreEnter) 0f else 1f }

    fun fraction() = source.fraction(progress)

    val start = source.bounds
    // Div sizing sits between the two modifiers. The parent and the drawn content need the same animated size.
    val size = Modifier.approachLayout(
        isMeasurementApproachInProgress = { fraction() != 1f },
    ) { measurable, constraints ->
        val child = measurable.measure(constraints)
        val width = constraints.constrainWidth(lerp(start.width.roundToInt(), lookaheadSize.width, fraction()))
        val height = constraints.constrainHeight(lerp(start.height.roundToInt(), lookaheadSize.height, fraction()))
        layout(width, height) { child.place(0, 0) }
    }
    val content = Modifier.approachLayout(
        isMeasurementApproachInProgress = { fraction() != 1f },
        isPlacementApproachInProgress = { enter.currentState != enter.targetState },
    ) { measurable, constraints ->
        val target = lookaheadSize
        val progress = fraction()
        val width = lerp(start.width.roundToInt(), target.width, progress)
        val height = lerp(start.height.roundToInt(), target.height, progress)
        val child = measurable.measure(Constraints.fitPrioritizingWidth(width, width, height, height))
        layout(constraints.constrainWidth(target.width), constraints.constrainHeight(target.height)) {
            val current = coordinates
            val scene = scope.scene.coordinates
            if (current == null || scene == null || !scene.isAttached) {
                child.place(0, 0)
            } else {
                val end = with(lookahead) { scene.localLookaheadPositionOf(current) }
                val position = lerp(source.positionIn(scene), end, progress)
                child.place(current.localPositionOf(scene, position).round())
            }
        }
    }
    return StateChangeBoundsModifiers(size, content)
}

/**
 * Bounds of the outgoing state, offered to the incoming one as the starts of its bounds animations.
 *
 * A declared ID can be offscreen in a gallery or repeated by a template, so the sources are matched
 * against the first placed incoming tree. An element revealed after that is a new arrival.
 */
internal class StateChangeBoundsMatching(
    private val sources: Map<String, StateChangeBoundsStart>,
    private val onFinished: (Set<String>) -> Unit = {},
) {
    var isFinished = false
        private set
    private var deferrals = 0
    val isDeferred: Boolean get() = deferrals > 0

    fun source(id: String): StateChangeBoundsStart? = if (isFinished) null else sources[id]

    fun onTargetCountChanged(id: String, count: Int) {
        if (!isFinished) sources[id]?.targetIsUnique = count == 1
    }

    fun defer() {
        deferrals++
    }

    fun resume() {
        deferrals--
    }

    fun finish(matchedIds: Set<String>) {
        sources.forEach { (id, source) -> source.targetIsUnique = id in matchedIds }
        isFinished = true
        onFinished(matchedIds)
    }
}

/**
 * Content that composes its children a frame later, after it is measured, like the pages of a pager.
 *
 * Until [childrenPlaced] reports on a placement that those children are placed, it keeps bounds
 * matching open and the enter transition of the incoming state running, since bounds animations
 * run on that transition.
 */
@Composable
internal fun Modifier.deferBoundsMatching(childrenPlaced: () -> Boolean): Modifier {
    val context = LocalStateChangeTransitionContext.current
    val scope = context.scope?.takeIf { context.contentActive && context.contentVisible }
    val transitionState = scope?.transitionState
    if (transitionState?.boundsMatching?.isFinished != false) return this
    val deferral = remember(transitionState) { BoundsMatchingDeferral(transitionState) }
    scope.enterScope.transition.animateFloat(
        transitionSpec = { if (deferral.isActive) tween(Int.MAX_VALUE) else snap() },
        label = "Deferred bounds matching",
    ) { if (it == EnterExitState.Visible && deferral.isActive) 1f else 0f }
    return onGloballyPositioned { if (childrenPlaced()) deferral.resume(finish = true) }
}

private class BoundsMatchingDeferral(private val state: StateChangeTransitionState) : RememberObserver {
    var isActive by mutableStateOf(true)
        private set
    private var deferring = false

    override fun onRemembered() {
        state.boundsMatching?.defer()
        deferring = true
    }

    // Content removed before its children were placed no longer holds the matching.
    override fun onForgotten() = resume(finish = false)

    override fun onAbandoned() = Unit

    fun resume(finish: Boolean) {
        isActive = false
        if (!deferring) return
        deferring = false
        state.boundsMatching?.resume()
        if (finish) state.finishBoundsMatching()
    }
}

internal class StateChangeBoundsStart(
    val bounds: ElementBounds,
    private val positionInRoot: Offset,
    val specs: List<FiniteAnimationSpec<Float>>?,
) {
    var targetIsUnique by mutableStateOf(true)

    fun fraction(progress: Float): Float = if (targetIsUnique) progress else 1f
    private var startInScene: Offset? = null

    // Convert once after the new scene is placed; scrolling and layout motion then carry the element with its scene.
    fun positionIn(scene: LayoutCoordinates): Offset = startInScene ?: scene.localPositionOf(
        scene.findRootCoordinates(), positionInRoot, includeMotionFrameOfReference = false,
    ).also { startInScene = it }
}
