package com.yandex.div.compose.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.yandex.div2.Div

@Composable
internal inline fun WithDivKey(data: Div, content: @Composable () -> Unit) {
    key(LocalDivViewContext.current.compositionKeyStorage.get(data.value())) { content() }
}
