package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.Div

@Composable
internal fun ScrollableChildItem(
    data: Div,
    modifier: Modifier,
    isHorizontal: Boolean,
    crossAxisAlignment: CrossAxisAlignment
) {
    val divBase = data.value()
    val childCrossAlignment = if (isHorizontal) {
        divBase.alignmentVertical?.observedValue()?.toCrossAxisAlignment()
    } else {
        divBase.alignmentHorizontal?.observedValue()?.toCrossAxisAlignment()
    } ?: crossAxisAlignment

    Box(
        modifier = modifier,
        contentAlignment = childCrossAlignment.toBoxAlignment(isHorizontal),
    ) {
        DivBlockView(
            data = data,
            checkVisibility = false
        )
    }
}
