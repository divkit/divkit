package com.yandex.div.test.data

import android.graphics.Color
import com.yandex.div2.Div
import com.yandex.div2.DivCircleShape
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSize
import com.yandex.div2.DivSlider

fun slider(
    id: String? = null,
    minValue: Long = 0,
    maxValue: Long = 100,
    thumbValueVariable: String? = null,
    hasTickMarks: Boolean = false,
    width: DivSize = fixed(constant(200L)),
): Div {
    val drawable =
        DivDrawable.Shape(
            DivShapeDrawable(
                color = constant(Color.BLACK),
                shape = DivShape.Circle(DivCircleShape()),
            ),
        )
    val tickMark = drawable.takeIf { hasTickMarks }
    return Div.Slider(
        DivSlider(
            id = id,
            maxValue = constant(maxValue),
            minValue = constant(minValue),
            thumbStyle = drawable,
            thumbValueVariable = thumbValueVariable,
            tickMarkActiveStyle = tickMark,
            tickMarkInactiveStyle = tickMark,
            trackActiveStyle = drawable,
            trackInactiveStyle = drawable,
            width = width,
        ),
    )
}
