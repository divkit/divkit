package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.Div

@Composable
internal fun ScrollableChildItem(
    childDiv: Div,
    modifier: Modifier,
    isHorizontal: Boolean,
    crossAxisAlignment: CrossAxisAlignment
) {
    val childBase = childDiv.value()

    val childCrossAlignment = if (isHorizontal) {
        childBase.alignmentVertical?.observedValue()?.toCrossAxisAlignment()
    } else {
        childBase.alignmentHorizontal?.observedValue()?.toCrossAxisAlignment()
    } ?: crossAxisAlignment

    Box(
        modifier = modifier,
        contentAlignment = childCrossAlignment.toBoxAlignment(isHorizontal),
    ) {
        DivBlockView(childDiv)
    }
}
