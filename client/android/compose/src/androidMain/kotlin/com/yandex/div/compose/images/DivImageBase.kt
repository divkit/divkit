package com.yandex.div.compose.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.yandex.div.compose.utils.observedAlignment
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivImageScale

@InternalApi
public fun Div.asImageBase(): DivImageBase? {
    return when (this) {
        is Div.GifImage -> DivImageBase(
            contentAlignmentHorizontal = value.contentAlignmentHorizontal,
            contentAlignmentVertical = value.contentAlignmentVertical,
            scale = value.scale
        )

        is Div.Image -> DivImageBase(
            contentAlignmentHorizontal = value.contentAlignmentHorizontal,
            contentAlignmentVertical = value.contentAlignmentVertical,
            scale = value.scale
        )

        else -> null
    }
}

@InternalApi
public class DivImageBase(
    public val contentAlignmentHorizontal: Expression<DivAlignmentHorizontal>,
    public val contentAlignmentVertical: Expression<DivAlignmentVertical>,
    public val scale: Expression<DivImageScale>
) {
    @Composable
    public fun observedAlignment(): Alignment {
        return observedAlignment(contentAlignmentHorizontal, contentAlignmentVertical)
    }

    @Composable
    public fun observedContentScale(): ContentScale {
        return scale.observedContentScale()
    }
}
