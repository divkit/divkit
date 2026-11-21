package com.yandex.div.compose.views.text

import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Constraints

// Keep the untruncated width for containers using width(IntrinsicSize.Max). Intrinsic heights
// come from the rendered text, including any larger font in its custom ellipsis.
internal class SourceTextIntrinsics(
    private val measurer: TextMeasurer,
    private val input: TextMeasureInput,
) : LayoutModifier {
    private val placeholders = input.text.text.placeholders(input.inlineContent)

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int {
        return measurer.measure(
            text = input.text.text,
            style = input.style,
            softWrap = false,
            placeholders = placeholders,
        ).size.width
    }
}
