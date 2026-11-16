package com.yandex.div.compose.images.transformations

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import coil3.transform.Transformation
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.utils.isRtlLayout
import com.yandex.div2.DivFilter

@Composable
internal fun List<DivFilter>?.observedTransformations(): List<Transformation> {
    if (this.isNullOrEmpty()) {
        return emptyList()
    }

    val factory = LocalComponent.current.transformationFactory
    val density = LocalDensity.current.density

    return mapNotNull { filter ->
        when (filter) {
            is DivFilter.Blur -> {
                val radiusDp = filter.value.radius.observedIntValue()
                if (radiusDp > 0f) factory.createBlur(radiusDp, density) else null
            }

            is DivFilter.RtlMirror ->
                if (isRtlLayout) factory.createRtlMirror() else null
        }
    }
}
