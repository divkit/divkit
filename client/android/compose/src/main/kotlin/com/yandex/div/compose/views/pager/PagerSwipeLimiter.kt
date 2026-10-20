package com.yandex.div.compose.views.pager

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

private const val BASE_DENSITY_DPI = 160f
private const val MILLISECONDS_PER_INCH = 100f
private const val MILLISECONDS_PER_SECOND = 1000f
private const val MAX_FLING_SCROLL_DURATION_MS = 100
private const val DECELERATION_TIME_FACTOR = 0.3356f
private const val DEFAULT_SCROLL_DURATION_MS = 300
private const val MAX_SCROLL_DURATION_MS = 2000

/** The stock fling behaviours keep their animations off the system animation scale; so does the pager. */
private object UnscaledMotionDuration : MotionDurationScale {
    override val scaleFactor = 1f
}

private val DecelerateEasing = Easing { fraction ->
    val remaining = 1f - fraction
    1f - remaining * remaining
}
private val QuinticEasing = Easing { fraction ->
    val shifted = fraction - 1f
    shifted * shifted * shifted * shifted * shifted + 1f
}

/** Applies [limiter] to the touch drags of the pager list. */
internal fun Modifier.pagerSwipe(limiter: PagerSwipeLimiter): Modifier =
    trackTouches(limiter).nestedScroll(limiter)

private fun Modifier.trackTouches(limiter: PagerSwipeLimiter): Modifier = pointerInput(limiter) {
    coroutineScope {
        var touchEnd: Job? = null
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            touchEnd?.cancel()
            limiter.onTouchStarted()
            try {
                do {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                } while (event.changes.any { it.pressed })
            } finally {
                touchEnd = launch {
                    // Draggable processes pointer deltas through a coroutine queue.
                    yield()
                    limiter.onTouchEnded()
                }
            }
        }
    }
}

/**
 * Limits a touch drag and its settling animation to the pages adjacent to its starting page.
 *
 * The pages start moving [pagingTouchSlopExtra] after the list claims the gesture, which is how a pager that
 * claims at the default touch slop still waits for ViewPager2's paging touch slop (see [PagerGestureSlop]);
 * a pager whose list waits for the paging slop itself passes 0 here. With [multiPageScroll] the drag stays
 * limited while the settling target follows the gesture (see [calculateMultiPageSnap]). Scrolls without a
 * touch, such as page keys and mouse wheels, are not limited.
 */
internal class PagerSwipeLimiter(
    private val listState: LazyListState,
    snapPosition: SnapPosition,
    neighbourSize: (Int) -> Int?,
    private val layoutDirection: LayoutDirection,
    private val pagingTouchSlopExtra: Float,
    val multiPageScroll: () -> Boolean,
    private val onTargetPageCalculated: (Int) -> Unit,
) : NestedScrollConnection {
    val snapTargets = PagerSnapTargets(listState, snapPosition, neighbourSize)
    private var touchActive = false
    /** The page where the current drag started. */
    private var origin: Int? = null
    /** Pointer movement inside the paging touch slop, before the pages start moving. */
    private var pagingSlopOffset = 0f
    private var pastPagingSlop = false
    /** Pointer movement past the adjacent pages, which the pointer has to return before the pages move back. */
    private var dragOverflow = 0f
    /** Whether the last pointer movement of the current drag went towards the next page. */
    private var dragForward: Boolean? = null
    private val isHorizontal: Boolean get() = listState.layoutInfo.orientation == Orientation.Horizontal
    private val direction: Float get() = if (isHorizontal && layoutDirection == LayoutDirection.Rtl) 1f else -1f

    fun onTouchStarted() {
        touchActive = true
        endDrag()
    }

    fun onTouchEnded() {
        // A drag may still have queued deltas; its fling callback runs after they are consumed.
        if (origin == null) touchActive = false
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        touchActive = false
        return Velocity.Zero
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // Nested children scroll this list through raw deltas without opening its scroll session.
        if (!touchActive || source != NestedScrollSource.UserInput || !listState.isScrollInProgress) return Offset.Zero
        val eventDelta = available.mainAxis() * direction
        if (eventDelta == 0f) return Offset.Zero
        dragForward = eventDelta > 0f
        var delta = eventDelta
        if (!pastPagingSlop) {
            pagingSlopOffset += eventDelta
            if (abs(pagingSlopOffset) < pagingTouchSlopExtra) return axisOffset(available.mainAxis())
            pastPagingSlop = true
            // The pages start where the pointer leaves the paging touch slop, as RecyclerView does with its own.
            delta = pagingSlopOffset - pagingSlopOffset.sign * pagingTouchSlopExtra
        }
        // The distance the paging touch slop swallowed is consumed too: the list must not scroll it either.
        val swallowed = eventDelta - delta
        if (origin == null) origin = snapTargets.closestPage()
        // Without pages to drag nothing else can consume the event either; the slop distance stays consumed.
        val anchors = origin?.let(snapTargets::dragAnchors) ?: return axisOffset(swallowed * direction)

        // ViewPager2 pages span the viewport, so dragging by the viewport size turns one page.
        val pixelsPerPage = snapTargets.viewportSizePx.coerceAtLeast(1).toFloat()
        val position = anchors.progress * pixelsPerPage
        val requested = delta + dragOverflow
        val deltaRange = min(delta, 0f)..max(delta, 0f)
        // The pointer turns at most one page from the origin and never moves pages against its own direction.
        val allowed = requested.coerceIn(-pixelsPerPage - position, pixelsPerPage - position).coerceIn(deltaRange)
        dragOverflow = requested - allowed
        // This callback runs inside LazyList's drag scroll session. Consume in page coordinates
        // and return the consumed pointer distance, leaving edge overflow to nested parents.
        val scroll = anchors.origin + anchors.toPixels((position + allowed) / pixelsPerPage)
        val consumed = listState.dispatchRawDelta(scroll)
        val consumedInput = anchors.toPages(consumed - anchors.origin) * pixelsPerPage - position
        val eventRange = min(eventDelta, 0f)..max(eventDelta, 0f)
        return axisOffset((swallowed + delta - allowed + consumedInput).coerceIn(eventRange) * direction)
    }

    /**
     * Returns the scroll distance to the settling target, at most one page away from where the drag started.
     * The target is reported before settling to keep ViewPager2's selection order for interrupted flings.
     */
    fun calculateSnapOffset(velocity: Float): Float {
        val anchors = origin?.let(snapTargets::dragAnchors)
        endDrag()
        val target = snapTargets.adjacentPage(velocity, anchors)
        target.page?.let(onTargetPageCalculated)
        return target.offset
    }

    /** Returns the settling target of a multi-page gesture, reported before settling like [calculateSnapOffset]. */
    fun calculateMultiPageSnap(flingDistance: Float): SettlingTarget {
        val anchors = origin?.let(snapTargets::dragAnchors)
        // A release below the fling threshold carries no usable velocity; its direction is the pointer's.
        val forward = if (flingDistance != 0f) flingDistance > 0f else dragForward
        endDrag()
        val target = snapTargets.multiPageTarget(flingDistance, forward, anchors)
        target.page?.let(onTargetPageCalculated)
        return target
    }

    private fun endDrag() {
        origin = null
        dragOverflow = 0f
        dragForward = null
        pagingSlopOffset = 0f
        pastPagingSlop = false
    }

    private fun Offset.mainAxis(): Float = if (isHorizontal) x else y

    private fun axisOffset(value: Float): Offset = if (isHorizontal) Offset(value, 0f) else Offset(0f, value)
}

@Composable
internal fun rememberPagerFlingBehavior(limiter: PagerSwipeLimiter): TargetedFlingBehavior {
    val densityDpi = LocalDensity.current.density * BASE_DENSITY_DPI
    val minimumFlingVelocity = LocalViewConfiguration.current.minimumFlingVelocity
    val decay = rememberSplineBasedDecay<Float>()
    return remember(limiter, decay, densityDpi, minimumFlingVelocity) {
        PagerFlingBehavior(limiter, decay, densityDpi, minimumFlingVelocity)
    }
}

private class PagerFlingBehavior(
    private val limiter: PagerSwipeLimiter,
    private val decay: DecayAnimationSpec<Float>,
    private val densityDpi: Float,
    private val minimumFlingVelocity: Float,
) : TargetedFlingBehavior {
    private val snapTargets = limiter.snapTargets

    override suspend fun ScrollScope.performFling(
        initialVelocity: Float,
        onRemainingDistanceUpdated: (Float) -> Unit,
    ): Float {
        val isFling = abs(initialVelocity) > minimumFlingVelocity
        if (limiter.multiPageScroll()) {
            return performMultiPageFling(initialVelocity, isFling, onRemainingDistanceUpdated)
        }
        val targetVelocity = if (isFling) initialVelocity.sign * Float.MAX_VALUE else 0f
        val offset = limiter.calculateSnapOffset(targetVelocity)
        val duration = if (isFling) {
            // PagerSnapHelper times the decorated viewport slot, regardless of the child width.
            val distance = snapTargets.remainingPageFraction(offset) * snapTargets.viewportSizePx
            val scrollTime = ceil(distance * MILLISECONDS_PER_INCH / densityDpi).toInt()
            ceil(scrollTime.coerceAtMost(MAX_FLING_SCROLL_DURATION_MS) / DECELERATION_TIME_FACTOR).toInt()
        } else {
            idleSnapDuration(offset)
        }
        return animateSnap(initialVelocity, offset, duration, isFling, onRemainingDistanceUpdated)
    }

    /**
     * Mirrors `PagerMultiPageScrollHelper`: a fling that turns pages is one decelerating scroll timed by its
     * velocity; a fling that stays on its page uses the snap helper's deceleration time; a slow release uses
     * RecyclerView's default scroll.
     *
     * The settle ends on the page the gesture reported to `selected_actions`, unless a touch interrupts it or the
     * list ends before that page reaches its snap position. ViewPager2 pages span the viewport, so the View
     * renderer reaches a far target by an exact distance; pages of different sizes make that distance an
     * estimate here, which [animateToPage] corrects on the way and [alignPage] finishes.
     */
    private suspend fun ScrollScope.performMultiPageFling(
        initialVelocity: Float,
        isFling: Boolean,
        onRemainingDistanceUpdated: (Float) -> Unit,
    ): Float {
        val flingDistance = if (isFling) decay.calculateTargetValue(0f, initialVelocity) else 0f
        val snap = limiter.calculateMultiPageSnap(flingDistance)
        val page = snap.page ?: return initialVelocity
        val remainingVelocity = if (snap.turnsPages) {
            val duration =
                ceil(2f * abs(snap.offset) * MILLISECONDS_PER_SECOND / abs(initialVelocity)).toInt().coerceAtLeast(1)
            animateToPage(page, duration, onRemainingDistanceUpdated)
            0f
        } else {
            val duration = if (isFling) {
                val scrollTime = ceil(abs(snap.offset) * MILLISECONDS_PER_INCH / densityDpi).toInt()
                ceil(scrollTime / DECELERATION_TIME_FACTOR).toInt()
            } else {
                idleSnapDuration(snap.offset)
            }
            animateSnap(initialVelocity, snap.offset, duration, isFling, onRemainingDistanceUpdated)
        }
        alignPage(page, onRemainingDistanceUpdated)
        return remainingVelocity
    }

    /**
     * Scrolls to [page] along a decelerating curve over [durationMillis], measuring the remaining distance again
     * on every frame: the pages laid out on the way replace the extrapolated distance with their real sizes, so
     * the curve bends towards the page instead of stopping where the first estimate pointed.
     *
     * Like the stock snap fling, the curve runs at the normal animation speed whatever the system scale is. It
     * stops as soon as the list reaches its end in the direction of the page, which a page that cannot reach its
     * snap position runs into, and returns no velocity: `PagerMultiPageScrollHelper` scrolls with
     * `RecyclerView.smoothScrollBy` without nested scrolling, so in View a scrolling ancestor never takes the
     * rest. Only the frame that reaches the end passes its overflow on, as any scroll of this list does.
     */
    private suspend fun ScrollScope.animateToPage(
        page: Int,
        durationMillis: Int,
        onRemainingDistanceUpdated: (Float) -> Unit,
    ) = withContext(UnscaledMotionDuration) {
        var previousProgress = 0f
        AnimationState(initialValue = 0f).animateTo(1f, tween(durationMillis, easing = DecelerateEasing)) {
            val remaining = snapTargets.snapOffsetOfPage(page)
            if (!snapTargets.canScrollBy(remaining)) {
                cancelAnimation()
                return@animateTo
            }
            // The share of the remaining distance this frame covers on the curve.
            val share = if (value < 1f) (value - previousProgress) / (1f - previousProgress) else 1f
            val consumed = scrollBy(remaining * share)
            onRemainingDistanceUpdated(remaining - consumed)
            previousProgress = value
        }
    }

    /**
     * Aligns [page] exactly, as RecyclerView's snap helper aligns its target once the scroll is idle. A page the
     * scroll did not bring into the layout is laid out first, because only its own layout gives its position, and
     * the alignment repeats while it gets closer: a page measured anew on the way corrects the previous offset.
     */
    private suspend fun ScrollScope.alignPage(page: Int, onRemainingDistanceUpdated: (Float) -> Unit) {
        if (!snapTargets.isPositionKnown(page)) snapTargets.layOutPage(this, page)
        var offset = snapTargets.snapOffsetOfPage(page)
        while (abs(offset) >= 1f && snapTargets.canScrollBy(offset)) {
            animateSnap(0f, offset, idleSnapDuration(offset), isFling = false, onRemainingDistanceUpdated)
            val next = snapTargets.snapOffsetOfPage(page)
            if (abs(next) > abs(offset) - 1f) return
            offset = next
        }
    }

    /** RecyclerView's idle snap uses its default smoothScrollBy duration and interpolator. */
    private fun idleSnapDuration(offset: Float): Int {
        val fraction = snapTargets.remainingPageFraction(offset)
        return ((fraction + 1f) * DEFAULT_SCROLL_DURATION_MS).toInt().coerceAtMost(MAX_SCROLL_DURATION_MS)
    }

    private suspend fun ScrollScope.animateSnap(
        initialVelocity: Float,
        offset: Float,
        duration: Int,
        isFling: Boolean,
        onRemainingDistanceUpdated: (Float) -> Unit,
    ): Float {
        val target = object : SnapLayoutInfoProvider {
            override fun calculateApproachOffset(velocity: Float, decayOffset: Float) = 0f
            override fun calculateSnapOffset(velocity: Float) = offset
        }
        val fling = snapFlingBehavior(
            snapLayoutInfoProvider = target,
            decayAnimationSpec = decay,
            snapAnimationSpec = tween(
                durationMillis = if (offset == 0f) 0 else duration,
                easing = if (isFling) DecelerateEasing else QuinticEasing,
            ),
        )
        return with(fling) { performFling(initialVelocity, onRemainingDistanceUpdated) }
    }
}
