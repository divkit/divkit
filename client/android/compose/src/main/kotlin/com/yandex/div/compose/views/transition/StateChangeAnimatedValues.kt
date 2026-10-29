package com.yandex.div.compose.views.transition

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.lerp
import com.yandex.div2.DivSlideTransition

internal data class AnimatedAppearance(
    val alphas: List<AnimatedFloat> = emptyList(),
    val scales: List<AnimatedScale> = emptyList(),
    val slides: List<AnimatedSlide> = emptyList(),
    val orderingDurationMillis: Long = appearanceOrderingDuration(alphas, scales, slides),
) {
    private val isFinished: Boolean
        get() = alphas.all { it.timing.isFinished || !it.timing.hasAnimator } &&
            scales.all { it.timing.isFinished || !it.timing.hasAnimator } &&
            slides.all { it.timing.isFinished || !it.timing.hasAnimator }

    fun combineWith(other: AnimatedAppearance): AnimatedAppearance {
        return AnimatedAppearance(
            alphas = alphas + other.alphas,
            scales = scales + other.scales,
            slides = slides + other.slides,
            orderingDurationMillis = SET_ORDERING_DURATION_MILLIS,
        )
    }

    fun resolveVisualState(
        sceneSize: IntSize,
        bounds: ElementBounds,
        baseState: StateChangeVisualState,
        isExiting: Boolean,
    ): StateChangeVisualState {
        val scale = scales.dominantAnimation { it.timing }
        val slide = slides.dominantAnimation { it.timing }
        val pivot = scales.resolvePivot(baseState)
        val translation = slide?.resolveTranslation(sceneSize, bounds, isExiting) ?: Offset(
            baseState.translationX,
            baseState.translationY,
        )
        return StateChangeVisualState(
            // AnimatedVisibility removes outgoing content after the last animation frame.
            alpha = if (isExiting && isFinished) {
                0f
            } else {
                alphas.dominantAnimation { it.timing }?.value ?: baseState.alpha
            },
            scale = scale?.value ?: baseState.scale,
            pivotX = pivot.x,
            pivotY = pivot.y,
            translationX = translation.x,
            translationY = translation.y,
        )
    }

    fun dominantSlide(): AnimatedSlide? = slides.dominantAnimation { it.timing }
}

internal data class AnimatedFloat(
    val value: Float,
    val timing: AnimatedTiming,
    val orderingDurationMillis: Long,
)

internal data class AnimatedScale(
    val value: Float,
    val pivotX: Float,
    val pivotY: Float,
    val timing: AnimatedTiming,
    val orderingDurationMillis: Long,
)

internal data class AnimatedSlide(
    val progress: Float,
    val edge: DivSlideTransition.Edge,
    val distance: Float?,
    val exitTranslationX: Float,
    val exitTranslationY: Float,
    val timing: AnimatedTiming = AnimatedTiming.NotStarted,
    val orderingDurationMillis: Long = 0L,
) {
    fun resolveTranslation(
        sceneSize: IntSize,
        bounds: ElementBounds,
        isExiting: Boolean,
    ): Offset {
        val target = targetTranslation(sceneSize, bounds, isExiting)
        if (!isExiting) return target * progress
        return Offset(
            x = lerp(exitTranslationX, target.x, progress),
            y = lerp(exitTranslationY, target.y, progress),
        )
    }

    fun hasMovement(geometry: SlideGeometry?, isExiting: Boolean): Boolean {
        if (distance == null && geometry == null) return true
        val target = targetTranslation(
            sceneSize = geometry?.sceneSize ?: IntSize.Zero,
            bounds = geometry?.bounds ?: ElementBounds.Zero,
            isExiting = isExiting,
        )
        val start = if (isExiting) Offset(exitTranslationX, exitTranslationY) else target
        val end = if (isExiting) target else Offset.Zero
        return start.x != end.x || start.y != end.y
    }

    private fun targetTranslation(
        sceneSize: IntSize,
        bounds: ElementBounds,
        isExiting: Boolean,
    ): Offset {
        val baseX = if (isExiting) exitTranslationX else 0f
        val baseY = if (isExiting) exitTranslationY else 0f
        return when (edge) {
            DivSlideTransition.Edge.LEFT -> Offset(baseX - (distance ?: bounds.right), baseY)
            DivSlideTransition.Edge.TOP -> Offset(baseX, baseY - (distance ?: bounds.bottom))
            DivSlideTransition.Edge.RIGHT -> Offset(
                baseX + (distance ?: sceneSize.width - bounds.left),
                baseY,
            )
            DivSlideTransition.Edge.BOTTOM -> Offset(
                baseX,
                baseY + (distance ?: sceneSize.height - bounds.top),
            )
        }
    }
}

internal data class AnimatedTiming(
    val phase: Float,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val hasAnimator: Boolean,
) {
    val isRunning: Boolean get() = phase > 0f && phase < 1f
    val isFinished: Boolean get() = phase >= 1f
    val hasStarted: Boolean get() = hasAnimator && (startTimeMillis == 0L || phase > 0f)

    companion object {
        val NotStarted = AnimatedTiming(
            phase = 0f,
            startTimeMillis = 0L,
            endTimeMillis = 0L,
            hasAnimator = true,
        )
    }
}

private fun appearanceOrderingDuration(
    alphas: List<AnimatedFloat>,
    scales: List<AnimatedScale>,
    slides: List<AnimatedSlide>,
): Long {
    return alphas.firstOrNull()?.orderingDurationMillis
        ?: scales.firstOrNull()?.orderingDurationMillis
        ?: slides.firstOrNull()?.orderingDurationMillis
        ?: SET_ORDERING_DURATION_MILLIS
}

private fun <T> List<T>.dominantAnimation(timing: (T) -> AnimatedTiming): T? {
    lastOrNull { timing(it).hasAnimator && timing(it).isRunning }?.let { return it }
    return lastOrNull { timing(it).hasAnimator && !timing(it).isFinished }
}

private fun List<AnimatedScale>.resolvePivot(baseState: StateChangeVisualState): Offset {
    var pivot = Offset(baseState.pivotX, baseState.pivotY)
    var lastEventTime = Long.MIN_VALUE
    var lastEventOrder = -1
    forEachIndexed { index, scale ->
        if (!scale.timing.hasAnimator || scale.hasCenterPivot) return@forEachIndexed
        val startOrder = index * 2
        if (scale.timing.hasStarted && isLaterEvent(
                scale.timing.startTimeMillis,
                startOrder,
                lastEventTime,
                lastEventOrder,
            )
        ) {
            pivot = Offset(scale.pivotX, scale.pivotY)
            lastEventTime = scale.timing.startTimeMillis
            lastEventOrder = startOrder
        }
        val endOrder = startOrder + 1
        if (scale.timing.isFinished && isLaterEvent(
                scale.timing.endTimeMillis,
                endOrder,
                lastEventTime,
                lastEventOrder,
            )
        ) {
            pivot = Offset(PIVOT_CENTER, PIVOT_CENTER)
            lastEventTime = scale.timing.endTimeMillis
            lastEventOrder = endOrder
        }
    }
    return pivot
}

private val AnimatedScale.hasCenterPivot: Boolean
    get() = pivotX == PIVOT_CENTER && pivotY == PIVOT_CENTER

private fun isLaterEvent(time: Long, order: Int, previousTime: Long, previousOrder: Int): Boolean {
    return time > previousTime || time == previousTime && order > previousOrder
}

private const val SET_ORDERING_DURATION_MILLIS = -1L
private const val PIVOT_CENTER = 0.5f
