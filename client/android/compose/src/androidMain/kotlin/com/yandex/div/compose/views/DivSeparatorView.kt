package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.toColor
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSeparator.DelimiterStyle.Orientation

@Composable
internal fun DivSeparatorView(
    modifier: Modifier,
    data: DivSeparator
) {
    val style = data.delimiterStyle
    val color = style?.color.observedValue(COLOR_DEFAULT)
    val orientation = style?.orientation.observedValue(Orientation.HORIZONTAL)

    DivSeparatorLayout(
        modifier = modifier,
        color = color.toColor(),
        vertical = orientation == Orientation.VERTICAL,
    )
}

private val COLOR_DEFAULT = Color(alpha = 0.078f, red = 0f, green = 0f, blue = 0f).toArgb()
