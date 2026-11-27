package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalViewConfiguration
import com.yandex.div.compose.views.gallery.scroll.GalleryScrollLayout
import com.yandex.div2.DivGallery
import kotlin.math.abs
import kotlin.math.sign

@Composable
internal fun rememberGalleryFlingBehavior(
    properties: GalleryProperties,
    scrollLayout: GalleryScrollLayout,
): FlingBehavior = when (properties.scrollMode) {
    DivGallery.ScrollMode.DEFAULT -> ScrollableDefaults.flingBehavior()
    DivGallery.ScrollMode.PAGING -> {
        val alignment = properties.scrollContentAlignment
        val minimumFlingVelocity = LocalViewConfiguration.current.minimumFlingVelocity
        val provider = remember(scrollLayout, alignment, minimumFlingVelocity) {
            GallerySnapLayoutInfoProvider(scrollLayout, alignment, minimumFlingVelocity)
        }
        rememberSnapFlingBehavior(provider)
    }
}

/**
 * Lets a fling decay and snaps to the item nearest in its direction, like Foundation's provider for lazy
 * lists. Works on any gallery layout and keeps the snap positions within the content edges.
 */
private class GallerySnapLayoutInfoProvider(
    private val scrollLayout: GalleryScrollLayout,
    private val alignment: DivGallery.ContentAlignment,
    private val minimumFlingVelocity: Float,
) : SnapLayoutInfoProvider {

    // Paging scrolls page by page: however fast, a fling decays over one viewport at most and then snaps
    // to the next item in its direction, which may lie farther when the spacing exceeds the viewport.
    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
        val layout = scrollLayout.layoutInfo
        val averageItemSize = if (layout.items.isEmpty()) 0 else layout.items.sumOf { it.size } / layout.items.size
        val distance = minOf(abs(decayOffset), layout.viewportSize.toFloat()) - averageItemSize
        return distance.coerceAtLeast(0f) * decayOffset.sign
    }

    override fun calculateSnapOffset(velocity: Float): Float {
        val layout = scrollLayout.layoutInfo
        val offsets = layout.items.map { layout.coerceToContent(layout.snapOffset(it, alignment)) }
        val target = when {
            velocity > minimumFlingVelocity -> offsets.filter { it >= 0 }.minOrNull()
            velocity < -minimumFlingVelocity -> offsets.filter { it <= 0 }.maxOrNull()
            else -> null
        }
        return (target ?: offsets.minByOrNull { abs(it) } ?: 0).toFloat()
    }
}
