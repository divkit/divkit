package com.yandex.div.compose.views.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.decodePreview
import com.yandex.div.compose.images.observedContentScale
import com.yandex.div.compose.utils.observedAlignment
import com.yandex.div2.DivGifImage

@Composable
internal fun DivGifImageView(
    modifier: Modifier,
    data: DivGifImage
) {
    DivImageContent(
        modifier = modifier,
        data = data,
        imageUrl = data.gifUrl.observedValue(),
        contentScale = data.scale.observedContentScale(),
        alignment = observedAlignment(
            data.contentAlignmentHorizontal,
            data.contentAlignmentVertical
        ),
        placeholderColor = data.placeholderColor.observedColorValue(),
        preview = {
            data.preview?.observedValue(transform = ::decodePreview)
                ?: data.previewUrl?.observedValue()
        }
    )
}
