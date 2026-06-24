package com.yandex.div.compose.views.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.decodePreview
import com.yandex.div.compose.images.observedContentScale
import com.yandex.div.compose.utils.observedAlignment
import com.yandex.div.compose.utils.toColor
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivImage

@Composable
internal fun DivImageView(
    modifier: Modifier,
    data: DivImage
) {
    val colorFilter = data.tintColor?.observedValue()?.let {
        toColorFilter(it, data.tintMode.observedValue())
    }
    DivImageContent(
        modifier = modifier,
        data = data,
        model = data.imageUrl.observedValue(),
        contentScale = data.scale.observedContentScale(),
        alignment = observedAlignment(
            data.contentAlignmentHorizontal,
            data.contentAlignmentVertical
        ),
        placeholderColor = data.placeholderColor.observedColorValue(),
        transformations = data.filters.resolveTransformations(),
        colorFilter = colorFilter,
        preview = { data.preview?.observedValue(transform = ::decodePreview) }
    )
}

private fun toColorFilter(tintColor: Int, tintMode: DivBlendMode): ColorFilter {
    val blendMode = when (tintMode) {
        DivBlendMode.SOURCE_IN -> BlendMode.SrcIn
        DivBlendMode.SOURCE_ATOP -> BlendMode.SrcAtop
        DivBlendMode.DARKEN -> BlendMode.Darken
        DivBlendMode.LIGHTEN -> BlendMode.Lighten
        DivBlendMode.MULTIPLY -> BlendMode.Modulate
        DivBlendMode.SCREEN -> BlendMode.Screen
    }
    return ColorFilter.tint(tintColor.toColor(), blendMode)
}
