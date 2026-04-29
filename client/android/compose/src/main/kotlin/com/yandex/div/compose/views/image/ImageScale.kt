package com.yandex.div.compose.views.image

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import com.yandex.div2.DivImageScale

internal fun DivImageScale.toContentScale(density: Float): ContentScale {
    return when (this) {
        DivImageScale.FILL -> ContentScale.Crop
        DivImageScale.FIT -> ContentScale.Fit
        DivImageScale.NO_SCALE -> NoScaleContentScale(density)
        DivImageScale.STRETCH -> ContentScale.FillBounds
    }
}

private class NoScaleContentScale(private val density: Float) : ContentScale {
    override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
        return ScaleFactor(density, density)
    }
}
