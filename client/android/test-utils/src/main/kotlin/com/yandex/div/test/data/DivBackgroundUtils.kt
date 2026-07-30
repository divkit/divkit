package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivBackground
import com.yandex.div2.DivImageBackground
import com.yandex.div2.DivSolidBackground

fun imageBackground(
    imageUrl: String,
    preloadRequired: Boolean = false
): DivBackground.Image {
    return DivBackground.Image(
        value = DivImageBackground(
            imageUrl = constant(imageUrl.toUri()),
            preloadRequired = constant(preloadRequired)
        )
    )
}

fun solidBackground(color: Color): DivBackground = solidBackground(constant(color.value))

fun solidBackground(color: Expression<Int>): DivBackground {
    return DivBackground.Solid(
        value = DivSolidBackground(color = color)
    )
}
