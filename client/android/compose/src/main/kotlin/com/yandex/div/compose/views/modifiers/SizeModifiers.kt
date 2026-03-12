package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.evaluate
import com.yandex.div.compose.views.toDp
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivSize

@Composable
internal fun Modifier.width(
    width: DivSize,
    horizontalAlignment: DivAlignmentHorizontal? = null
): Modifier {
    val align = when (horizontalAlignment) {
        DivAlignmentHorizontal.CENTER -> Alignment.CenterHorizontally
        DivAlignmentHorizontal.RIGHT, DivAlignmentHorizontal.END -> Alignment.End
        else -> Alignment.Start
    }
    return when (width) {
        is DivSize.MatchParent -> fillMaxWidth()
        is DivSize.WrapContent -> wrapContentWidth(align = align)
        is DivSize.Fixed -> wrapContentWidth(align = align, unbounded = true)
            .requiredWidth(width.value.value.evaluate().toDp())
    }
}

@Composable
internal fun Modifier.height(
    height: DivSize,
    verticalAlignment: DivAlignmentVertical? = null
): Modifier {
    val align = when (verticalAlignment) {
        DivAlignmentVertical.CENTER -> Alignment.CenterVertically
        DivAlignmentVertical.BOTTOM -> Alignment.Bottom
        else -> Alignment.Top
    }
    return when (height) {
        is DivSize.MatchParent -> fillMaxHeight()
        is DivSize.WrapContent -> wrapContentHeight(align = align)
        is DivSize.Fixed -> wrapContentHeight(align = align, unbounded = true)
            .requiredHeight(height.value.value.evaluate().toDp())
    }
}
