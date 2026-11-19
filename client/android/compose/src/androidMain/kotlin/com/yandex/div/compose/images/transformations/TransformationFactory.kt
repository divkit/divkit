package com.yandex.div.compose.images.transformations

import android.content.Context
import coil3.transform.Transformation
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
@Inject
internal class TransformationFactory(
    private val context: Context,
) {
    fun createBlur(radiusDp: Int, density: Float): Transformation {
        return BlurTransformation(context, radiusDp, density)
    }

    fun createRtlMirror(): Transformation = RtlMirrorTransformation
}
