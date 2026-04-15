package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.Hyphens
import com.yandex.div.compose.utils.observeBaseTextStyle
import com.yandex.div.compose.utils.observeShadow
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivText

@Composable
internal fun DivText.observeTextStyle(
    fontSize: Int,
    textAlignmentHorizontal: DivAlignmentHorizontal,
    hyphens: Hyphens
): TextStyle {
    val baseStyle = observeBaseTextStyle(
        fontSize = fontSize,
        textAlignmentHorizontal = textAlignmentHorizontal,
        fontSizeUnit = fontSizeUnit,
        textColor = textColor,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
    )

    val shadow = textShadow?.observeShadow(baseStyle.color.alpha)
    val textDecoration = textDecoration(
        strike.observedValue(),
        underline.observedValue()
    )

    return baseStyle.copy(
        hyphens = hyphens,
        textDecoration = textDecoration,
        shadow = shadow,
    )
}
