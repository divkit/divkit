package com.yandex.div.compose.views.image

import android.content.Context
import androidx.compose.runtime.Composable
import coil3.transform.Transformation
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.reportWarning
import com.yandex.div2.DivFilter
import com.yandex.div2.DivFilterRtlMirror

@Composable
internal fun List<DivFilter>?.resolveTransformations(
    context: Context,
    density: Float,
): List<Transformation> {
    if (this == null) {
        return emptyList()
    }

    return mapNotNull { filter ->
        when (filter) {
            is DivFilter.Blur -> {
                val radiusDp = filter.value.radius.observedIntValue()
                if (radiusDp > 0f) {
                    val transformation = BlurTransformation(context, radiusDp, density)
                    if (radiusDp > transformation.maxRadiusDp) {
                        reportWarning(
                            "The maximum supported blur radius is 25. Values exceeding this limit will be automatically downscaled"
                        )
                    }
                    return@mapNotNull transformation
                }
                return@mapNotNull null
            }

            is DivFilter.RtlMirror -> {
                reportError("Filter not supported: ${DivFilterRtlMirror.TYPE}")
                return@mapNotNull null
            }
        }
    }
}
