package com.yandex.div.compose.views.background

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.IntRect
import coil3.BitmapImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.images.rememberNetworkRestoringImagePainter
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivAbsoluteEdgeInsets
import com.yandex.div2.DivNinePatchBackground

@Composable
internal fun Modifier.ninePatchBackground(data: DivNinePatchBackground): Modifier {
    val imageUrl = data.imageUrl.observedValue()
    return key(imageUrl) {
        ninePatchBackground(
            request = rememberImageRequest(ImageRequestParams(data = imageUrl)),
            insets = data.insets,
        )
    }
}

@Composable
private fun Modifier.ninePatchBackground(
    request: ImageRequest,
    insets: DivAbsoluteEdgeInsets,
): Modifier {
    val painter = rememberNetworkRestoringImagePainter(request)
    val state by painter.state.collectAsState()
    val image = (state as? AsyncImagePainter.State.Success)?.result?.image ?: return this
    if (image !is BitmapImage) {
        reportError("Nine-patch background requires a bitmap image")
        return this
    }

    val center = IntRect(
        left = insets.left.observedIntValue(),
        right = image.width - insets.right.observedIntValue(),
        top = insets.top.observedIntValue(),
        bottom = image.height - insets.bottom.observedIntValue(),
    )
    val ninePatch = remember(image, center) {
        NinePatchImage(image, center)
    }
    return drawBehind {
        ninePatch.draw(this)
    }
}
