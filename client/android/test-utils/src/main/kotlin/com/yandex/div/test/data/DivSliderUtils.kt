package com.yandex.div.test.data

import android.graphics.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivCircleShape
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivFocus
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSize
import com.yandex.div2.DivSlider

fun slider(
    id: String? = null,
    minValue: Expression<Long> = constant(0L),
    maxValue: Expression<Long> = constant(100L),
    thumbValueVariable: String? = null,
    hasTickMarks: Boolean = false,
    width: DivSize = fixed(constant(200L)),
    focus: DivFocus? = null,
    isEnabled: Expression<Boolean> = constant(true),
    accessibility: DivAccessibility? = null,
    pressStartActions: List<DivAction>? = null,
    pressEndActions: List<DivAction>? = null,
    thumbTextStyle: DivSlider.TextStyle? = null,
    thumbSecondaryValueVariable: String? = null,
    thumbSecondaryTextStyle: DivSlider.TextStyle? = null,
    tickMarkStyle: DivDrawable? = null,
): Div {
    val drawable =
        DivDrawable.Shape(
            DivShapeDrawable(
                color = constant(Color.BLACK),
                shape = DivShape.Circle(DivCircleShape()),
            ),
        )
    val tickMark = tickMarkStyle ?: drawable.takeIf { hasTickMarks }
    return Div.Slider(
        DivSlider(
            accessibility = accessibility,
            id = id,
            focus = focus,
            isEnabled = isEnabled,
            maxValue = maxValue,
            minValue = minValue,
            pressEndActions = pressEndActions,
            pressStartActions = pressStartActions,
            thumbSecondaryTextStyle = thumbSecondaryTextStyle,
            thumbSecondaryValueVariable = thumbSecondaryValueVariable,
            thumbStyle = drawable,
            thumbTextStyle = thumbTextStyle,
            thumbValueVariable = thumbValueVariable,
            tickMarkActiveStyle = tickMark,
            tickMarkInactiveStyle = tickMark,
            trackActiveStyle = drawable,
            trackInactiveStyle = drawable,
            width = width,
        ),
    )
}
