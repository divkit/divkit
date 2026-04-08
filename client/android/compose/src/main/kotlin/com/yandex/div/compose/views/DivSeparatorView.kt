package com.yandex.div.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.observedValue
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

    Box(modifier = modifier) {
        when (orientation) {
            Orientation.HORIZONTAL -> Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color.toColor())
            )

            Orientation.VERTICAL -> Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(color.toColor())
            )
        }
    }
}

private val COLOR_DEFAULT = Color(alpha = 0.078f, red = 0f, green = 0f, blue = 0f).toArgb()
