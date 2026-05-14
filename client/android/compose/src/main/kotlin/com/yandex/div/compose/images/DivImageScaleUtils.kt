package com.yandex.div.compose.images

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import com.yandex.div2.DivImageScale

internal fun DivImageScale.toContentScale(density: Float): ContentScale {
    return when (this) {
        DivImageScale.FILL -> ContentScale.Crop
        DivImageScale.FIT -> ContentScale.Fit
        DivImageScale.NO_SCALE -> FixedScale(density)
        DivImageScale.STRETCH -> ContentScale.FillBounds
    }
}
