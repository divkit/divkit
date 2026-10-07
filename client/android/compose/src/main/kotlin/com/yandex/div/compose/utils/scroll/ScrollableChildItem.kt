package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.context.WithDivKey
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.Div

// Inline keeps the key in the caller's item loop so a child's composition can move with the item.
@Composable
internal inline fun ScrollableChildItem(
    data: Div,
    modifier: Modifier,
    isHorizontal: Boolean,
    crossAxisAlignment: CrossAxisAlignment
) {
    WithDivKey(data) {
        val divBase = data.value()
        val childCrossAlignment = if (isHorizontal) {
            divBase.alignmentVertical?.observedValue()?.toCrossAxisAlignment()
        } else {
            divBase.alignmentHorizontal?.observedValue()?.toCrossAxisAlignment(LocalLayoutDirection.current)
        } ?: crossAxisAlignment

        Box(
            modifier = modifier,
            contentAlignment = childCrossAlignment.toBoxAlignment(isHorizontal),
        ) {
            DivBlockView(
                data = data,
                defaultHorizontalAlignment = if (isHorizontal) {
                    Alignment.Start
                } else {
                    childCrossAlignment.toHorizontalAlignment()
                },
                defaultVerticalAlignment = if (isHorizontal) {
                    childCrossAlignment.toVerticalAlignment()
                } else {
                    Alignment.Top
                },
            )
        }
    }
}
