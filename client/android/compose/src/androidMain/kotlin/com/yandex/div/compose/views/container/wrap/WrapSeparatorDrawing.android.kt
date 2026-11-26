package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.container.observeSeparatorStroke
import com.yandex.div.compose.views.container.resolveSeparatorVisibility
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape

/** The separator to draw, or null when it is not shown anywhere. */
@Composable
internal fun DivContainer.Separator?.observeWrapSeparator(): SeparatorDrawInfo? {
    val visibility = resolveSeparatorVisibility()
    if (this == null || !visibility.hasAnySeparator) return null
    val drawable = (style as? DivDrawable.Shape)?.value ?: return null
    val shape = drawable.shape
    val color = when (shape) {
        is DivShape.RoundedRectangle -> shape.value.backgroundColor
        is DivShape.Circle -> shape.value.backgroundColor
    } ?: drawable.color
    if (color == null) {
        reportError("Separator color not defined")
        return null
    }
    val stroke = when (shape) {
        is DivShape.RoundedRectangle -> shape.value.stroke
        is DivShape.Circle -> shape.value.stroke
    } ?: drawable.stroke
    val radius = when (shape) {
        is DivShape.RoundedRectangle -> shape.value.cornerRadius.observedValue()
        is DivShape.Circle -> shape.value.radius.observedValue()
    }
    val layoutDirection = LocalLayoutDirection.current
    val margins = margins.observeInsets()
    return SeparatorDrawInfo(
        visibility = visibility,
        color = color.observedColorValue(),
        shapeWidthDp = if (shape is DivShape.RoundedRectangle) shape.value.itemWidth.observedValue() else radius * 2,
        shapeHeightDp = if (shape is DivShape.RoundedRectangle) shape.value.itemHeight.observedValue() else radius * 2,
        cornerRadiusDp = radius,
        marginStartDp = margins.calculateStartPadding(layoutDirection),
        marginEndDp = margins.calculateEndPadding(layoutDirection),
        marginTopDp = margins.calculateTopPadding(),
        marginBottomDp = margins.calculateBottomPadding(),
        isCircle = shape is DivShape.Circle,
        stroke = stroke?.observeSeparatorStroke(),
    )
}
