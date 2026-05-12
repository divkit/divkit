package com.yandex.div.compose.views.gallery

import androidx.compose.ui.Alignment
import com.yandex.div.compose.utils.scroll.CrossAxisAlignment
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
