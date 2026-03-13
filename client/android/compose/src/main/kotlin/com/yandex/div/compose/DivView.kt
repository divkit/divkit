package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.LocalDivViewContext
import com.yandex.div.compose.views.divContext
import com.yandex.div2.DivData

@Composable
fun DivView(
    data: DivData,
    modifier: Modifier = Modifier
) {
    val divContext = divContext
    val viewContext = remember(data) {
        divContext.createDivViewContext(data)
    }
    CompositionLocalProvider(LocalDivViewContext provides viewContext) {
        DivBlockView(
            data = data.states.first().div,
            modifier = modifier
        )
    }
}
