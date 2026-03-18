package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.observedValue
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical

@Composable
internal fun ContainerOverlapView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()

    val containerModifier = modifier.adaptiveContainerPadding(
        data.paddings.toContainerInsets(),
        horizontalAlignment,
        verticalAlignment,
    )
    val defaultAlignment = horizontalAlignment.toOverlapHorizontal() + verticalAlignment.toOverlapVertical()

    Box(containerModifier, contentAlignment = defaultAlignment) {
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
    val horizontal = childHorizontal?.toOverlapHorizontal() ?: defaultHorizontal.toOverlapHorizontal()
    val vertical = childVertical?.toOverlapVertical() ?: defaultVertical.toOverlapVertical()
    return horizontal + vertical
}

private enum class OverlapHorizontal { Start, Center, End }
private enum class OverlapVertical { Top, Center, Bottom }

private operator fun OverlapHorizontal.plus(vertical: OverlapVertical): Alignment = when (this) {
    OverlapHorizontal.Start -> when (vertical) {
        OverlapVertical.Top -> Alignment.TopStart
        OverlapVertical.Center -> Alignment.CenterStart
        OverlapVertical.Bottom -> Alignment.BottomStart
    }
    OverlapHorizontal.Center -> when (vertical) {
        OverlapVertical.Top -> Alignment.TopCenter
        OverlapVertical.Center -> Alignment.Center
        OverlapVertical.Bottom -> Alignment.BottomCenter
    }
    OverlapHorizontal.End -> when (vertical) {
        OverlapVertical.Top -> Alignment.TopEnd
        OverlapVertical.Center -> Alignment.CenterEnd
        OverlapVertical.Bottom -> Alignment.BottomEnd
    }
}

private fun DivAlignmentHorizontal.toOverlapHorizontal(): OverlapHorizontal =
    when (this) {
        DivAlignmentHorizontal.CENTER -> OverlapHorizontal.Center
        DivAlignmentHorizontal.RIGHT, DivAlignmentHorizontal.END -> OverlapHorizontal.End
        else -> OverlapHorizontal.Start
    }

private fun DivAlignmentVertical.toOverlapVertical(): OverlapVertical =
    when (this) {
        DivAlignmentVertical.CENTER -> OverlapVertical.Center
        DivAlignmentVertical.BOTTOM -> OverlapVertical.Bottom
        else -> OverlapVertical.Top
    }

private fun DivContentAlignmentHorizontal.toOverlapHorizontal(): OverlapHorizontal =
    when (this) {
        DivContentAlignmentHorizontal.CENTER -> OverlapHorizontal.Center
        DivContentAlignmentHorizontal.RIGHT, DivContentAlignmentHorizontal.END -> OverlapHorizontal.End
        else -> OverlapHorizontal.Start
    }

private fun DivContentAlignmentVertical.toOverlapVertical(): OverlapVertical =
    when (this) {
        DivContentAlignmentVertical.CENTER -> OverlapVertical.Center
        DivContentAlignmentVertical.BOTTOM -> OverlapVertical.Bottom
        else -> OverlapVertical.Top
    }
