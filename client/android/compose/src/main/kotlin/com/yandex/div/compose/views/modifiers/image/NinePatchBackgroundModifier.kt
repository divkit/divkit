package com.yandex.div.compose.views.modifiers.image

import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.core.graphics.Insets
import coil3.BitmapImage
import coil3.compose.AsyncImagePainter
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.images.rememberNetworkRestoringImagePainter
import com.yandex.div.compose.utils.reportError
import com.yandex.div.core.images.toNinePatch
import com.yandex.div2.DivAbsoluteEdgeInsets
import com.yandex.div2.DivNinePatchBackground
import kotlin.math.roundToInt

@Composable
internal fun Modifier.ninePatchBackground(data: DivNinePatchBackground): Modifier {
    val imageUrl = data.imageUrl.observedValue()
    return key(imageUrl) { ninePatchBackground(imageUrl, data.insets) }
}

@Composable
private fun Modifier.ninePatchBackground(imageUrl: Uri, insets: DivAbsoluteEdgeInsets): Modifier {
    val component = divContext.component
    val painter = rememberNetworkRestoringImagePainter(
        model = rememberImageRequest(ImageRequestParams(data = imageUrl)),
        imageLoader = component.imageLoader,
        onState = component.debugConfiguration.imagePainterStateListener
    )
    val state by painter.state.collectAsState()
    val image = (state as? AsyncImagePainter.State.Success)?.result?.image ?: return this
    if (image !is BitmapImage) {
        reportError("Nine-patch background requires a bitmap image")
        return this
    }

    val resolvedInsets = Insets.of(
        insets.left.observedIntValue(),
        insets.top.observedIntValue(),
        insets.right.observedIntValue(),
        insets.bottom.observedIntValue(),
    )
    val ninePatch = remember(image, resolvedInsets) {
        image.bitmap.toNinePatch(resolvedInsets)
    }
    return drawWithCache {
        val bounds = Rect(0, 0, size.width.roundToInt(), size.height.roundToInt())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        onDrawBehind {
            drawIntoCanvas { canvas -> ninePatch.draw(canvas.nativeCanvas, bounds, paint) }
        }
    }
}
