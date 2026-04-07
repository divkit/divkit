package com.yandex.div.compose.views.state

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div2.DivState

@Composable
internal fun DivStateView(
    modifier: Modifier,
    data: DivState
) {
    val clipToBounds = data.clipToBounds.observedValue()
    val modifier = if (clipToBounds) modifier.clipToBounds() else modifier

    val activeState = data.observeActiveState()
    val activeDiv = activeState?.div
    Box(modifier = modifier.padding(data.paddings)) {
        if (activeDiv != null) {
            DivBlockView(activeDiv)
        }
    }
}
