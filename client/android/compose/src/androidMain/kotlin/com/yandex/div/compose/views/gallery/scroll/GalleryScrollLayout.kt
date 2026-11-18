package com.yandex.div.compose.views.gallery.scroll

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.layout.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.ui.unit.Density
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivGallery
import kotlin.math.abs
import kotlin.math.sign

private const val MAX_ANIMATED_ITEMS = 100
private const val MAX_NEARBY_STEPS = 2
private const val MAX_END_SCROLL_STEPS = 4
// Leaves room for item offsets in the layout's Int arithmetic.
private const val MAX_SCROLL_DISTANCE = 1 shl 29

internal data class GalleryScrollDestination(
    val index: Int,
    val toEnd: Boolean = false,
    val offset: Int = 0,
    val overflow: DivActionScrollBy.Overflow = DivActionScrollBy.Overflow.CLAMP,
)

/**
 * Gallery layout driven by scroll actions. The factories below adapt a lazy list, a lazy staggered grid
 * and measured lanes to it; the scrolling itself is shared.
 */
internal class GalleryScrollLayout(
    val scrollState: ScrollableState,
    private val density: Density,
    /** Whether an index jump discards the lane history of the preceding items. */
    private val isStaggered: Boolean,
    /** Items an animation skips with a jump instead of measuring them all. */
    private val teleportItems: () -> Int,
    private val currentLayoutInfo: () -> GalleryLayoutInfo,
    private val jumpToItem: suspend (index: Int) -> Unit,
    private val scroll: suspend (block: suspend LazyLayoutScrollScope.() -> Unit) -> Unit,
) {
    val layoutInfo: GalleryLayoutInfo
        get() = currentLayoutInfo()

    suspend fun scrollToItem(
        destination: GalleryScrollDestination,
        animated: Boolean,
        alignment: DivGallery.ContentAlignment,
        duration: Int?,
    ) {
        if (destination.toEnd && isStaggered) {
            scrollToContentEnd(animated, duration)
            return
        }
        val index = destination.index
        if (animated) {
            scroll {
                animateGalleryScroll(index, duration) { remainingDistance(destination, alignment) }
            }
        } else if (isStaggered && layoutInfo.item(index) == null) {
            scrollNearby(index)
        }
        // Lands exactly: animations follow size estimates and nearby scrolls may stop short of the item.
        if (layoutInfo.item(index) == null) jumpToItem(index)
        scroll {
            val distance = remainingDistance(destination, alignment)
            if (distance != 0f) scrollBy(distance)
        }
    }

    private suspend fun LazyLayoutScrollScope.animateGalleryScroll(
        index: Int,
        duration: Int?,
        distanceToTarget: LazyLayoutScrollScope.() -> Float,
    ) {
        val indexDistance = index - firstVisibleItemIndex
        val skippedItems = teleportItems()
        if (layoutInfo.item(index) == null && abs(indexDistance) > skippedItems) {
            // Like Foundation's animateScrollToItem, skip distant items instead of measuring them all.
            snapToItem(index - indexDistance.sign * skippedItems)
        }
        val millis = duration ?: defaultScrollDuration(distanceToTarget(), density)
        var previousFraction = 0f
        animate(0f, 1f, animationSpec = tween(durationMillis = millis)) { fraction, _ ->
            if (previousFraction < 1f) {
                // Re-estimate unseen item sizes without restarting the duration when the target appears.
                scrollBy(distanceToTarget() * (fraction - previousFraction) / (1f - previousFraction))
            }
            previousFraction = fraction
        }
    }

    // Distance to the destination, bounded by the reachable edge so a boundary item is reached at the deadline.
    private fun LazyLayoutScrollScope.remainingDistance(
        destination: GalleryScrollDestination,
        alignment: DivGallery.ContentAlignment,
    ): Float {
        val layout = layoutInfo
        val item = layout.item(destination.index)
        val distance = when {
            item == null -> calculateDistanceTo(destination.index)
            destination.toEnd -> layout.distanceToEnd() ?: calculateDistanceTo(destination.index)
            else -> layout.snapOffset(item, alignment)
        }
        val bounded = when {
            distance < 0 -> layout.distanceToStart()?.let { maxOf(distance, it) } ?: distance
            distance > 0 -> layout.distanceToEnd()?.let { minOf(distance, it) } ?: distance
            else -> distance
        }.toFloat()
        return if (destination.offset == 0) {
            bounded
        } else {
            scrollState.galleryOffsetDistance(destination.offset, destination.overflow, bounded)
        }
    }

    // Keeps the lane history of nearby targets without traversing every intermediate viewport.
    private suspend fun scrollNearby(index: Int) {
        val backwards = layoutInfo.items.any { it.index > index }
        scroll {
            var steps = 0
            while (steps < MAX_NEARBY_STEPS && layoutInfo.item(index) == null) {
                val viewportSize = layoutInfo.viewportSize
                val distance = if (backwards) -viewportSize else viewportSize
                if (scrollBy(distance.toFloat()) == 0f) break
                steps++
            }
        }
    }

    // An index jump can discard the lane of a long earlier item, so the end is reached by scrolling.
    // One oversized scroll measures the unseen items in a single layout pass.
    private suspend fun scrollToContentEnd(animated: Boolean, duration: Int?) {
        val viewportSize = layoutInfo.viewportSize
        if (viewportSize <= 0) return

        var consumed = 0f
        scroll {
            var steps = 0
            while (steps < MAX_END_SCROLL_STEPS && scrollState.canScrollForward) {
                val step = scrollBy(MAX_SCROLL_DISTANCE.toFloat())
                if (step <= 0f) break
                consumed += step
                steps++
            }
        }
        if (animated && consumed > 0f) {
            val rewind = scrollState.scrollBy(-minOf(viewportSize.toFloat(), consumed))
            scrollState.scrollByDistance(-rewind, animated = true, duration, density)
        }
    }
}

internal fun LazyListState.galleryScrollLayout(density: Density): GalleryScrollLayout = GalleryScrollLayout(
    scrollState = this,
    density = density,
    isStaggered = false,
    teleportItems = { MAX_ANIMATED_ITEMS },
    currentLayoutInfo = { layoutInfo.toGalleryLayoutInfo() },
    jumpToItem = { index -> scrollToItem(index) },
    scroll = { block -> scroll { LazyLayoutScrollScope(this@galleryScrollLayout, this).block() } },
)

internal fun LazyStaggeredGridState.galleryScrollLayout(density: Density): GalleryScrollLayout = GalleryScrollLayout(
    scrollState = this,
    density = density,
    isStaggered = true,
    // Foundation's animateScrollToItem skips a hundred items per lane.
    teleportItems = { MAX_ANIMATED_ITEMS * (layoutInfo.visibleItemsInfo.maxOfOrNull { it.lane + 1 } ?: 1) },
    currentLayoutInfo = { layoutInfo.toGalleryLayoutInfo() },
    jumpToItem = { index -> scrollToItem(index) },
    scroll = { block -> scroll { LazyLayoutScrollScope(this@galleryScrollLayout, this).block() } },
)
