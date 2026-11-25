package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextMeasurer
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivText

@Composable
internal fun DivText.observeLineLimits(): TextLineLimits {
    val maxLines = maxLines?.observedIntValue()?.coerceAtLeast(1)
    return TextLineLimits(
        maxLines = maxLines,
        minHiddenLines = if (maxLines != null) minHiddenLines.observedSaturatedIntValue() else 0,
        autoEllipsize = autoEllipsize.observedValue(false) && truncate.observedValue().hasMarker(),
    )
}

private fun DivText.Truncate.hasMarker(): Boolean {
    return when (this) {
        DivText.Truncate.NONE -> false
        DivText.Truncate.START, DivText.Truncate.MIDDLE, DivText.Truncate.END -> true
    }
}

@Composable
private fun Expression<Long>?.observedSaturatedIntValue(): Int {
    return observedValue(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}

internal fun TextMeasurer.measureMaxLines(
    limits: TextLineLimits,
    input: TextMeasureInput,
    width: Int,
): Int = measureMaxLines(
    limits = limits,
    text = input.text.text,
    style = input.style,
    width = width,
    inlineContent = input.inlineContent,
)

@Composable
internal fun Modifier.textHeightLimit(
    limits: TextLineLimits,
    input: TextMeasureInput,
): Modifier = textHeightLimit(
    limits = limits,
    text = input.text.text,
    style = input.style,
    inlineContent = input.inlineContent,
)

@Composable
internal fun rememberSingleLineTruncation(input: TextMeasureInput): SingleLineTruncation =
    rememberSingleLineTruncation(
        text = input.text.text,
        style = input.style,
        inlineContent = input.inlineContent,
    )
