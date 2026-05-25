package com.yandex.div.compose.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalDensity
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivImageScale

@Composable
internal fun Expression<DivImageScale>.observedContentScale(): ContentScale {
    return when (observedValue()) {
        DivImageScale.FILL -> ContentScale.Crop
        DivImageScale.FIT -> ContentScale.Fit
        DivImageScale.NO_SCALE -> FixedScale(LocalDensity.current.density)
        DivImageScale.STRETCH -> ContentScale.FillBounds
    }
}
