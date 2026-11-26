package com.yandex.div.compose.views.pager

import androidx.compose.runtime.Composable
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.toEasing
import com.yandex.div2.DivPageTransformation

@Composable
internal fun observePageTransformation(data: DivPageTransformation?): PageTransformation? = when (data) {
    null -> null
    is DivPageTransformation.Slide -> with(data.value) {
        PageTransformation(
            nextPageAlpha.observedFloatValue(),
            previousPageAlpha.observedFloatValue(),
            nextPageScale.observedFloatValue(),
            previousPageScale.observedFloatValue(),
            interpolator.observedValue().toEasing(),
        )
    }
    is DivPageTransformation.Overlap -> with(data.value) {
        PageTransformation(
            nextPageAlpha.observedFloatValue(),
            previousPageAlpha.observedFloatValue(),
            nextPageScale.observedFloatValue(),
            previousPageScale.observedFloatValue(),
            interpolator.observedValue().toEasing(),
            overlap = true,
            reversedStackingOrder = reversedStackingOrder.observedValue(),
        )
    }
}
