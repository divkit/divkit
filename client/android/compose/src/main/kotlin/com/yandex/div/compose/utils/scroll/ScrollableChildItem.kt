package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.context.WithDivKey
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.Div

/**
 * [matchParentMaxSize] bounds a match_parent child along the scroll axis, where a lazy list
 * measures its items with unbounded constraints.
 */
// Inline keeps the key in the caller's item loop so a child's composition can move with the item.
@Composable
internal inline fun ScrollableChildItem(
    data: Div,
    modifier: Modifier,
    isHorizontal: Boolean,
    crossAxisAlignment: CrossAxisAlignment,
    matchParentMaxSize: Dp = Dp.Unspecified,
) {
    val (matchParentMaxWidth, matchParentMaxHeight) = if (isHorizontal) {
        matchParentMaxSize to Dp.Unspecified
    } else {
        Dp.Unspecified to matchParentMaxSize
    }
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
                matchParentMaxWidth = matchParentMaxWidth,
                matchParentMaxHeight = matchParentMaxHeight,
            )
        }
    }
}
