package com.yandex.div.compose.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.yandex.div2.DivText

@Composable
internal fun DivTextView(
    modifier: Modifier,
    data: DivText
) {
    Text(
        text = data.text.evaluate(expressionResolver),
        modifier = modifier,
        color = data.textColor.evaluate().toColor(),
        fontSize = data.fontSize.evaluate().toFloat().sp,
    )
}
