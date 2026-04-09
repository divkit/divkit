package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.imageLoader
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div.compose.views.image.buildImageRequest
import com.yandex.div.compose.views.image.resolveTransformations
import com.yandex.div.compose.views.image.toContentScale
import com.yandex.div2.DivImageBackground
import kotlin.math.roundToInt

@Composable
internal fun Modifier.imageBackground(data: DivImageBackground): Modifier {
    val imageUrl = data.imageUrl.observedValue()
    val imageAlpha = data.alpha.observedValue().toFloat()
    val scale = data.scale.observedValue()
    val alignHorizontal = data.contentAlignmentHorizontal.observedValue()
    val alignVertical = data.contentAlignmentVertical.observedValue()

    val context = divContext
    val density = LocalDensity.current.density
    val filterTransformations = data.filters.resolveTransformations(context, density)

    val model = buildImageRequest(context, imageUrl, scale, filterTransformations)
    val contentScale = scale.toContentScale(density)
    val alignment = toAlignment(alignHorizontal, alignVertical)

    val painter = rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
    )

    return drawBehind {
        val srcSize = painter.intrinsicSize
        if (!srcSize.isSpecified || srcSize == Size.Zero) return@drawBehind

        clipRect {
            val scaleFactor = contentScale.computeScaleFactor(srcSize, size)
            val scaledWidth = srcSize.width * scaleFactor.scaleX
            val scaledHeight = srcSize.height * scaleFactor.scaleY

            val alignedOffset = alignment.align(
                IntSize(scaledWidth.roundToInt(), scaledHeight.roundToInt()),
                IntSize(size.width.roundToInt(), size.height.roundToInt()),
                layoutDirection
            )

            translate(alignedOffset.x.toFloat(), alignedOffset.y.toFloat()) {
                with(painter) {
                    draw(size = Size(scaledWidth, scaledHeight), alpha = imageAlpha)
                }
            }
        }
    }
}
