package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.modifiers.apply
import com.yandex.div2.Div

@Composable
internal fun DivBlockView(
    data: Div,
    modifier: Modifier = Modifier
) {
    val modifier = modifier.apply(data.value())
    when (data) {
        is Div.Container -> DivContainerView(modifier, data.value)
        is Div.Image -> DivImageView(modifier, data.value)
        is Div.Text -> DivTextView(modifier, data.value)
        else -> TODO()
    }
}
