package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.text.observeBaseTextStyle
import com.yandex.div.compose.utils.observeRoundedCornerShape
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivTabs

private const val DEFAULT_LINE_HEIGHT_COEFFICIENT = 1.3f

@Composable
internal fun DivTabs.TabTitleStyle.observeTabShape(): Shape {
    return observeRoundedCornerShape(
        cornerRadius = cornerRadius,
        cornersRadius = cornersRadius,
        defaultShape = RoundedCornerShape(percent = 50),
    )
}

@Composable
internal fun DivTabs.TabTitleStyle.observeRowHeight(): Dp {
    val fontSize = fontSize.observedIntValue()
    val lineHeight = lineHeight?.observedIntValue() ?: (fontSize * DEFAULT_LINE_HEIGHT_COEFFICIENT).toInt()
    val tabTop = paddings.top.observedIntValue()
    val tabBottom = paddings.bottom.observedIntValue()
    return (lineHeight + tabTop + tabBottom).dp
}

@Composable
internal fun DivTabs.TabTitleStyle.observeTextStyle(isSelected: Boolean): TextStyle {
    val weight = if (isSelected) {
        activeFontWeight ?: fontWeight
    } else {
        inactiveFontWeight ?: fontWeight
    }
    val weightValue = if (isSelected) activeFontWeightValue else inactiveFontWeightValue
    val textColor = if (isSelected) activeTextColor else inactiveTextColor
    val fontVariationSettings = if (isSelected) activeFontVariationSettings else inactiveFontVariationSettings

    return observeBaseTextStyle(
        fontSize = fontSize.observedIntValue(),
        textAlignmentHorizontal = DivAlignmentHorizontal.CENTER,
        fontSizeUnit = fontSizeUnit,
        textColor = textColor,
        fontWeight = weight,
        fontWeightValue = weightValue,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        fontVariationSettings = fontVariationSettings
    )
}
