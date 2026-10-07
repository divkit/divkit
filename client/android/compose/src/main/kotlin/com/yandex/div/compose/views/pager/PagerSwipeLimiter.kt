package com.yandex.div.compose.views.pager

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Easing
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
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
import kotlinx.coroutines.yield

private const val BASE_DENSITY_DPI = 160f
private const val MILLISECONDS_PER_INCH = 100f
private const val MAX_FLING_SCROLL_DURATION_MS = 100
private const val DECELERATION_TIME_FACTOR = 0.3356f
private const val DEFAULT_SCROLL_DURATION_MS = 300
private const val MAX_SCROLL_DURATION_MS = 2000

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
 * Scrolls without a touch, such as page keys and mouse wheels, are not limited.
 */
internal class PagerSwipeLimiter(
    private val listState: LazyListState,
    private val snapPosition: SnapPosition,
    private val neighbourSize: (Int) -> Int?,
    private val layoutDirection: LayoutDirection,
    private val onSnapOffsetCalculated: (Float) -> Unit,
) : NestedScrollConnection {
    private val delegate = SnapLayoutInfoProvider(listState, snapPosition)
    private var touchActive = false
    /** The page where the current drag started. */
    private var origin: Int? = null
    /** Pointer movement past the adjacent pages, which the pointer has to return before the pages move back. */
    private var dragOverflow = 0f
    private val isHorizontal: Boolean get() = listState.layoutInfo.orientation == Orientation.Horizontal
    private val direction: Float get() = if (isHorizontal && layoutDirection == LayoutDirection.Rtl) 1f else -1f

    val viewportSizePx: Int
        get() = listState.layoutInfo.let { if (isHorizontal) it.viewportSize.width else it.viewportSize.height }

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
        val delta = available.mainAxis() * direction
        if (delta == 0f) return Offset.Zero
        if (origin == null) origin = pageClosestToSnapPosition()
        val anchors = dragAnchors() ?: return Offset.Zero

        // ViewPager2 pages span the viewport, so dragging by the viewport size turns one page.
        val pixelsPerPage = viewportSizePx.coerceAtLeast(1).toFloat()
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
        return axisOffset((delta - allowed + consumedInput).coerceIn(deltaRange) * direction)
    }

    /**
     * Returns the scroll distance to the settling target, at most one page away from where the drag started.
     * The target is reported before settling to keep ViewPager2's selection order for interrupted flings.
     */
    fun calculateSnapOffset(velocity: Float): Float {
        val anchors = dragAnchors()
        endDrag()
        val lower = anchors?.previous ?: Float.NEGATIVE_INFINITY
        val upper = anchors?.next ?: Float.POSITIVE_INFINITY
        val offset = delegate.calculateSnapOffset(velocity).coerceIn(lower, upper)
        onSnapOffsetCalculated(offset)
        return offset
    }

    private fun endDrag() {
        origin = null
        dragOverflow = 0f
    }

    fun remainingPageFraction(offset: Float): Float {
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val target = items.minByOrNull { abs(info.snapOffset(it, snapPosition) - offset) } ?: return 0f
        val adjacentIndex = target.index + if (offset < 0f) 1 else -1
        val adjacent = items.firstOrNull { it.index == adjacentIndex }
        val stride = if (adjacent != null) {
            abs(info.snapOffset(target, snapPosition) - info.snapOffset(adjacent, snapPosition))
        } else {
            (target.size + info.mainAxisItemSpacing).toFloat()
        }
        return abs(offset) / stride.coerceAtLeast(1f)
    }

    private fun pageClosestToSnapPosition(): Int? {
        val info = listState.layoutInfo
        return info.itemsWithNeighbours(neighbourSize).minByOrNull { abs(info.snapOffset(it, snapPosition)) }?.index
    }

    /** Snap offsets around the drag origin in the current layout, which LazyList updates with every scroll. */
    private fun dragAnchors(): DragAnchors? {
        val page = origin ?: return null
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val originItem = items.firstOrNull { it.index == page } ?: return null
        fun snapOffsetOf(index: Int) =
            items.firstOrNull { it.index == index }?.let { info.snapOffset(it, snapPosition) }

        return DragAnchors(
            origin = info.snapOffset(originItem, snapPosition),
            previous = snapOffsetOf(page - 1),
            next = snapOffsetOf(page + 1),
            pageStride = (originItem.size + info.mainAxisItemSpacing).toFloat().coerceAtLeast(1f),
        )
    }

    private fun Offset.mainAxis(): Float = if (isHorizontal) x else y

    private fun axisOffset(value: Float): Offset = if (isHorizontal) Offset(value, 0f) else Offset(0f, value)
}

/** Snap offsets of the drag origin and its neighbours; an unknown neighbour leaves its side unbounded. */
private class DragAnchors(
    val origin: Float,
    val previous: Float?,
    val next: Float?,
    pageStride: Float,
) {
    private val previousStride = previous?.let { (origin - it).coerceAtLeast(1f) } ?: pageStride
    private val nextStride = next?.let { (it - origin).coerceAtLeast(1f) } ?: pageStride

    /** Drag progress in pages: -1 at the previous page, 0 at the origin, 1 at the next page. */
    val progress: Float get() = toPages(-origin)

    fun toPages(pixels: Float): Float = pixels / if (pixels < 0f) previousStride else nextStride

    fun toPixels(pages: Float): Float = pages * if (pages < 0f) previousStride else nextStride
}

/** Uses the paging touch slop that ViewPager2 waits for before it starts a page drag. */
@Composable
internal fun rememberPagerViewConfiguration(configuration: ViewConfiguration): ViewConfiguration {
    val context = LocalContext.current
    return remember(context, configuration) {
        val pagingTouchSlop = android.view.ViewConfiguration.get(context).scaledPagingTouchSlop.toFloat()
        object : ViewConfiguration by configuration {
            override val touchSlop = pagingTouchSlop
        }
    }
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
    override suspend fun ScrollScope.performFling(
        initialVelocity: Float,
        onRemainingDistanceUpdated: (Float) -> Unit,
    ): Float {
        val isFling = abs(initialVelocity) > minimumFlingVelocity
        val targetVelocity = if (isFling) initialVelocity.sign * Float.MAX_VALUE else 0f
        val offset = limiter.calculateSnapOffset(targetVelocity)
        val fraction = limiter.remainingPageFraction(offset)
        val duration = if (isFling) {
            // PagerSnapHelper times the decorated viewport slot, regardless of the child width.
            val distance = fraction * limiter.viewportSizePx
            val scrollTime = ceil(distance * MILLISECONDS_PER_INCH / densityDpi).toInt()
            ceil(scrollTime.coerceAtMost(MAX_FLING_SCROLL_DURATION_MS) / DECELERATION_TIME_FACTOR).toInt()
        } else {
            // RecyclerView's idle snap uses its default smoothScrollBy duration and interpolator.
            ((fraction + 1f) * DEFAULT_SCROLL_DURATION_MS).toInt().coerceAtMost(MAX_SCROLL_DURATION_MS)
        }
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
