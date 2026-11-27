package com.yandex.div.compose.views.gallery.scroll

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.layout.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivGallery
import kotlin.math.abs
import kotlin.math.sign

// Like Foundation's animateScrollToItem, an animation jumps over more distant items instead of measuring them all.
private const val MAX_ANIMATED_ITEMS = 100

// One scroll this long measures all the items up to the content end in a single layout pass.
private const val CONTENT_END_SCROLL_STEP = (1 shl 29).toFloat()

internal data class GalleryScrollDestination(
    val index: Int,
    val toEnd: Boolean = false,
    val offset: Int = 0,
    val overflow: DivActionScrollBy.Overflow = DivActionScrollBy.Overflow.CLAMP,
)

/**
 * Gallery layout driven by scroll actions, the default item and paging snaps. The factories below adapt
 * a lazy list and a lazy staggered grid to it; the scrolling itself is shared.
 */
internal class GalleryScrollLayout(
    val scrollState: ScrollableState,
    private val currentLayoutInfo: () -> GalleryLayoutInfo,
    /** Reaches the items a lazy layout has not laid out yet; null when every item is laid out. */
    private val lazyScrollScope: ((ScrollScope) -> LazyLayoutScrollScope)? = null,
    /** Whether an index jump reassigns the lanes, which rearranges the items around the target. */
    private val isStaggered: Boolean = false,
) {
    val layoutInfo: GalleryLayoutInfo
        get() = currentLayoutInfo()

    suspend fun scrollToItem(
        destination: GalleryScrollDestination,
        animated: Boolean,
        alignment: DivGallery.ContentAlignment,
        duration: Int? = null,
    ) {
        scrollState.scroll {
            val lazyScope = lazyScrollScope?.invoke(this)
            if (isStaggered && destination.toEnd) measureToContentEnd(animated)
            val index = destination.index
            if (animated) {
                if (layoutInfo.item(index) == null) lazyScope?.skipDistantItems(index)
                animateScroll(duration) { distanceTo(destination, alignment, lazyScope) }
            }
            // Lands exactly: an animation follows size estimates of the items that are not laid out.
            lazyScope?.layOutItem(index)
            scrollBy(distanceTo(destination, alignment, lazyScope))
        }
    }

    // A staggered grid looks the same at any position only while its lanes are assigned from the first item
    // on, so a nearby item is reached by scrolling over the items between, a distant one by a jump.
    private fun LazyLayoutScrollScope.layOutItem(index: Int) {
        if (isStaggered && abs(index - firstVisibleItemIndex) <= MAX_ANIMATED_ITEMS) {
            val viewportSize = layoutInfo.viewportSize.toFloat()
            val step = if (layoutInfo.items.any { it.index > index }) -viewportSize else viewportSize
            while (layoutInfo.item(index) == null) {
                if (scrollBy(step) == 0f) break
            }
        }
        if (layoutInfo.item(index) == null) snapToItem(index)
    }

    // An index jump reassigns the lanes and can drop a long earlier item that holds the content end, so
    // the end is reached by scrolling. An animation then covers the last viewport.
    private fun ScrollScope.measureToContentEnd(animated: Boolean) {
        var travelled = 0f
        do {
            val step = scrollBy(CONTENT_END_SCROLL_STEP)
            travelled += step
        } while (step > 0f)
        if (animated) scrollBy(-minOf(layoutInfo.viewportSize.toFloat(), travelled))
    }

    private fun LazyLayoutScrollScope.skipDistantItems(index: Int) {
        val indexDistance = index - firstVisibleItemIndex
        if (abs(indexDistance) > MAX_ANIMATED_ITEMS) {
            snapToItem(index - indexDistance.sign * MAX_ANIMATED_ITEMS)
        }
    }

    // Without a duration this is Compose's default scroll animation, the spring of animateScrollBy.
    private suspend fun ScrollScope.animateScroll(duration: Int?, distance: () -> Float) {
        val spec = duration?.let { tween(it) }
            ?: spring(visibilityThreshold = 1f / abs(distance()).coerceAtLeast(1f))
        var previousFraction = 0f
        animate(0f, 1f, animationSpec = spec) { fraction, _ ->
            if (previousFraction < 1f) {
                // Re-estimates the sizes of unseen items without restarting the animation when the target appears.
                scrollBy(distance() * (fraction - previousFraction) / (1f - previousFraction))
            }
            previousFraction = fraction
        }
    }

    // Distance to the destination, bounded by the reachable edge so a boundary item is reached at the deadline.
    private fun distanceTo(
        destination: GalleryScrollDestination,
        alignment: DivGallery.ContentAlignment,
        lazyScope: LazyLayoutScrollScope?,
    ): Float {
        val layout = layoutInfo
        val item = layout.item(destination.index)
        val distance = when {
            item == null -> lazyScope?.calculateDistanceTo(destination.index) ?: 0
            destination.toEnd -> layout.distanceToEnd ?: layout.snapOffset(item, alignment)
            else -> layout.snapOffset(item, alignment)
        }
        val bounded = layout.coerceToContent(distance).toFloat()
        return if (destination.offset == 0) {
            bounded
        } else {
            scrollState.galleryOffsetDistance(destination.offset, destination.overflow, bounded)
        }
    }
}

internal fun LazyListState.galleryScrollLayout(): GalleryScrollLayout = GalleryScrollLayout(
    scrollState = this,
    currentLayoutInfo = { layoutInfo.toGalleryLayoutInfo() },
    lazyScrollScope = { LazyLayoutScrollScope(this, it) },
)

internal fun LazyStaggeredGridState.galleryScrollLayout(): GalleryScrollLayout = GalleryScrollLayout(
    scrollState = this,
    currentLayoutInfo = { toGalleryLayoutInfo() },
    lazyScrollScope = { LazyLayoutScrollScope(this, it) },
    isStaggered = true,
)
