package com.yandex.div.test.data

import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivBackground
import com.yandex.div2.DivSolidBackground

fun solidBackground(color: Color): DivBackground = solidBackground(constant(color.value))

fun solidBackground(color: Expression<Int>): DivBackground {
    return DivBackground.Solid(
        value = DivSolidBackground(color = color)
    )
}
