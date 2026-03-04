package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.DivData

@Composable
fun DivView(
    data: DivData,
    modifier: Modifier = Modifier
) {
    DivBlockView(
        data = data.states.first().div,
        modifier = modifier
    )
}
