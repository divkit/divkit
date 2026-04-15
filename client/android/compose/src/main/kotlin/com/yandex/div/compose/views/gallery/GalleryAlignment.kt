package com.yandex.div.compose.views.gallery

import androidx.compose.ui.Alignment
import com.yandex.div.compose.utils.scroll.CrossAxisAlignment
import com.yandex.div2.DivGallery

internal fun DivGallery.CrossContentAlignment.toVerticalAlignment(): Alignment.Vertical =
    toCrossAxisAlignment().toVerticalAlignment()

internal fun DivGallery.CrossContentAlignment.toHorizontalAlignment(): Alignment.Horizontal =
    toCrossAxisAlignment().toHorizontalAlignment()

internal fun DivGallery.CrossContentAlignment.toCrossAxisAlignment(): CrossAxisAlignment =
    when (this) {
        DivGallery.CrossContentAlignment.START -> CrossAxisAlignment.START
        DivGallery.CrossContentAlignment.CENTER -> CrossAxisAlignment.CENTER
        DivGallery.CrossContentAlignment.END -> CrossAxisAlignment.END
    }
