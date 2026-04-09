package com.yandex.div.compose.views.image

import android.content.Context
import androidx.compose.runtime.Composable
import coil3.transform.Transformation
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div.compose.utils.reporter
import com.yandex.div2.DivFilter

@Composable
internal fun List<DivFilter>?.resolveTransformations(
    context: Context,
    density: Float,
): List<Transformation> {
    if (this == null) return emptyList()
    val transformations = mutableListOf<Transformation>()
    for (filter in this) {
        when (filter) {
            is DivFilter.Blur -> {
                val radiusDp = filter.value.radius.observedIntValue()
                if (radiusDp > 0f) {
                    val transformation = BlurTransformation(context, radiusDp, density)
                    transformations += transformation

                    if (radiusDp > transformation.maxRadiusDp) {
                        reporter.reportWarning(
                            "The maximum supported blur radius is 25. Values exceeding this limit will be automatically downscaled"
                        )
                    }
                }
            }
            is DivFilter.RtlMirror -> reporter.reportError("Not implemented")
        }
    }
    return transformations
}