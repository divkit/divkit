package com.yandex.div.test.data

import com.yandex.div.evaluable.types.Color
import com.yandex.div2.DivBackground
import com.yandex.div2.DivSolidBackground

fun solidBackground(color: Color): DivBackground {
    return DivBackground.Solid(
        value = DivSolidBackground(
            color = constant(color.value)
        )
    )
}
