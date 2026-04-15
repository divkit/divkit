package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical

@Composable
internal fun ContainerOverlapView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()

    val modifier = modifier.adaptiveContainerPadding(
        data.paddings,
        horizontalAlignment,
        verticalAlignment,
    )
    val defaultAlignment = combineAlignment(
        horizontalAlignment.toCrossAxisHorizontalAlignment(),
        verticalAlignment.toCrossAxisVerticalAlignment(),
    )

    Box(modifier, contentAlignment = defaultAlignment) {
        data.visibleItems().forEach { childDiv ->
            val childAlignment = resolveOverlapChildAlignment(
                childHorizontal = childDiv.value().alignmentHorizontal?.observedValue(),
                childVertical = childDiv.value().alignmentVertical?.observedValue(),
                defaultHorizontal = horizontalAlignment,
                defaultVertical = verticalAlignment,
            )
            DivBlockView(childDiv, Modifier.align(childAlignment))
        }
    }
}

private fun resolveOverlapChildAlignment(
    childHorizontal: DivAlignmentHorizontal?,
    childVertical: DivAlignmentVertical?,
    defaultHorizontal: DivContentAlignmentHorizontal,
    defaultVertical: DivContentAlignmentVertical,
): Alignment {
    val horizontal = childHorizontal?.toHorizontalAlignment()
        ?: defaultHorizontal.toCrossAxisHorizontalAlignment()
    val vertical = childVertical?.toVerticalAlignment()
        ?: defaultVertical.toCrossAxisVerticalAlignment()
    return combineAlignment(horizontal, vertical)
}
