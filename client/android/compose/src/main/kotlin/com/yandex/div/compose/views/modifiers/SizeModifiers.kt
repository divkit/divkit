package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.evaluate
import com.yandex.div.compose.views.toDp
import com.yandex.div2.DivSize

@Composable
internal fun Modifier.height(height: DivSize): Modifier {
    return when (height) {
        is DivSize.MatchParent -> fillMaxHeight()
        is DivSize.WrapContent -> wrapContentHeight()
        is DivSize.Fixed -> height(height.value.value.evaluate().toDp())
    }
}

@Composable
internal fun Modifier.width(width: DivSize): Modifier {
    return when (width) {
        is DivSize.MatchParent -> fillMaxWidth()
        is DivSize.WrapContent -> wrapContentWidth()
        is DivSize.Fixed -> width(width.value.value.evaluate().toDp())
    }
}
