package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.context.WithDivKey
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.Div
import com.yandex.div2.DivBase

/**
 * Renders [data] in [component], the scope its collection owner resolved for it; a `null`
 * [component] renders it in the current scope of the owner.
 * [matchParentMaxSize] bounds a match_parent child along the scroll axis, where a lazy list
 * measures its items with unbounded constraints.
 */
// Inline keeps the key in the caller's item loop so a child's composition can move with the item.
@Composable
internal inline fun ScrollableChildItem(
    data: Div,
    component: DivLocalComponent?,
    modifier: Modifier,
    isHorizontal: Boolean,
    crossAxisAlignment: CrossAxisAlignment,
    matchParentMaxSize: Dp = Dp.Unspecified,
    contentModifier: Modifier = Modifier,
) {
    val (matchParentMaxWidth, matchParentMaxHeight) = if (isHorizontal) {
        matchParentMaxSize to Dp.Unspecified
    } else {
        Dp.Unspecified to matchParentMaxSize
    }
    WithDivKey(data) {
        WithLocalComponent(component) {
            val childCrossAlignment =
                data.value().observeCrossAxisAlignment(isHorizontal, crossAxisAlignment)

            Box(
                modifier = modifier,
                contentAlignment = childCrossAlignment.toBoxAlignment(isHorizontal),
            ) {
                DivBlockView(
                    data = data,
                    modifier = contentModifier,
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
}

/**
 * The cross-axis alignment the child declares for the scroll orientation, or [defaultAlignment].
 */
@Composable
internal fun DivBase.observeCrossAxisAlignment(
    isHorizontal: Boolean,
    defaultAlignment: CrossAxisAlignment,
): CrossAxisAlignment {
    val alignment = if (isHorizontal) {
        alignmentVertical?.observedValue()?.toCrossAxisAlignment()
    } else {
        alignmentHorizontal?.observedValue()?.toCrossAxisAlignment(LocalLayoutDirection.current)
    }
    return alignment ?: defaultAlignment
}
