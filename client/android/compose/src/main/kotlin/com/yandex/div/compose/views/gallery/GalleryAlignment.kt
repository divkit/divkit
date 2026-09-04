package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.ui.Alignment
import com.yandex.div.compose.utils.scroll.CrossAxisAlignment
import com.yandex.div.compose.utils.scroll.desiredSnapOffset
import com.yandex.div2.DivGallery

internal fun DivGallery.ContentAlignment.toVerticalAlignment(): Alignment.Vertical =
    toCrossAxisAlignment().toVerticalAlignment()

internal fun DivGallery.ContentAlignment.toHorizontalAlignment(): Alignment.Horizontal =
    toCrossAxisAlignment().toHorizontalAlignment()

internal fun DivGallery.ContentAlignment.toCrossAxisAlignment(): CrossAxisAlignment =
    when (this) {
        DivGallery.ContentAlignment.START -> CrossAxisAlignment.START
        DivGallery.ContentAlignment.CENTER -> CrossAxisAlignment.CENTER
        DivGallery.ContentAlignment.END -> CrossAxisAlignment.END
    }

internal fun DivGallery.ContentAlignment.toSnapPosition(): SnapPosition =
    GallerySnapPosition(this)

internal fun DivGallery.ContentAlignment.calculateDesiredScrollOffset(
    viewportSizePx: Int,
    itemSizePx: Int,
    startPaddingPx: Int,
    endPaddingPx: Int,
): Int {
    return desiredSnapOffset(
        snapPosition = when (this) {
            DivGallery.ContentAlignment.START -> SnapPosition.Start
            DivGallery.ContentAlignment.CENTER -> SnapPosition.Center
            DivGallery.ContentAlignment.END -> SnapPosition.End
        },
        viewportSizePx = viewportSizePx,
        itemSizePx = itemSizePx,
        startPaddingPx = startPaddingPx,
        endPaddingPx = endPaddingPx,
    ) - startPaddingPx
}

private data class GallerySnapPosition(
    val alignment: DivGallery.ContentAlignment,
) : SnapPosition {
    override fun position(
        layoutSize: Int,
        itemSize: Int,
        beforeContentPadding: Int,
        afterContentPadding: Int,
        itemIndex: Int,
        itemCount: Int,
    ): Int = alignment.calculateDesiredScrollOffset(
        viewportSizePx = layoutSize,
        itemSizePx = itemSize,
        startPaddingPx = beforeContentPadding,
        endPaddingPx = afterContentPadding,
    )
}

internal fun DivGallery.ScrollMode.defaultScrollContentAlignment(): DivGallery.ContentAlignment =
    when (this) {
        DivGallery.ScrollMode.DEFAULT -> DivGallery.ContentAlignment.START
        DivGallery.ScrollMode.PAGING -> DivGallery.ContentAlignment.CENTER
    }

internal data class GalleryScrollAlignmentKey(
    val alignment: DivGallery.ContentAlignment,
    val isHorizontal: Boolean,
    val startPaddingPx: Int,
    val endPaddingPx: Int,
)
