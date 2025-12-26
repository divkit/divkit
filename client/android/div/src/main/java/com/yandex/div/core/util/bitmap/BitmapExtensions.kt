package com.yandex.div.core.util.bitmap

import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.view.View
import androidx.core.graphics.scale
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.availableHeight
import com.yandex.div.core.view2.divs.availableWidth
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.view2.divs.wrapsContent
import com.yandex.div2.DivImageScale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal fun Bitmap.applyScaleAndFilters(
    divView: Div2View,
    target: View,
    scale: DivImageScale,
    nonEmptyFilters: List<BitmapFilter>,
    actionAfterFilters: (Bitmap) -> Unit
) {
    density = DisplayMetrics.DENSITY_DEFAULT
    if (scale == DivImageScale.NO_SCALE || (target as? DivImageView)?.wrapsContent() == true) {
        val density = target.resources.displayMetrics.density
        val result = scale((width * density).roundToInt(), (height * density).roundToInt(), filter = false)
            .applyFilters(divView, nonEmptyFilters, target)
        actionAfterFilters(result)
        return
    }

    target.doOnActualLayout {
        val result = scale(scale, target)
            .applyFilters(divView, nonEmptyFilters, target)
        actionAfterFilters(result)
    }
}

private fun Bitmap.scale(scale: DivImageScale, target: View): Bitmap {
    val availableWidth = target.availableWidth.toFloat()
    val availableHeight = target.availableHeight.toFloat()

    val scaleX = when (scale) {
        DivImageScale.NO_SCALE -> return this
        DivImageScale.FIT -> min(availableWidth / width, availableHeight / height)
        DivImageScale.FILL -> max(availableWidth / width, availableHeight / height)
        DivImageScale.STRETCH -> availableWidth / width
    }
    val scaleY = when (scale) {
        DivImageScale.STRETCH -> availableHeight / height
        else -> scaleX
    }
    return scale((scaleX * width).toInt(), (scaleY * height).toInt(), false)
}

private fun Bitmap.applyFilters(
    divView: Div2View,
    filters: List<BitmapFilter>,
    target: View,
): Bitmap {
    val bitmapEffectHelper = divView.div2Component.bitmapEffectHelper
    var result = this
    for (filter in filters) {
        when (filter) {
            is BitmapFilter.Blur -> {
                val radius = filter.radius.dpToPx(target.resources.displayMetrics)
                result = bitmapEffectHelper.blurBitmap(result, radius.toFloat())
            }

            is BitmapFilter.RtlMirror -> if (target.isLayoutRtl()) {
                result = bitmapEffectHelper.mirrorBitmap(result)
            }
        }
    }
    return result
}
