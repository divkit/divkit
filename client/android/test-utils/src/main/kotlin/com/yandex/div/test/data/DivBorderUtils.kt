package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivBorder
import com.yandex.div2.DivCornersRadius

fun border(
    cornerRadius: Int? = null,
    cornersRadius: DivCornersRadius? = null,
): DivBorder = border(
    cornerRadius = cornerRadius?.let { constant(it.toLong()) },
    cornersRadius = cornersRadius,
)

fun border(
    cornerRadius: Expression<Long>?,
    cornersRadius: DivCornersRadius? = null,
): DivBorder = DivBorder(
    cornerRadius = cornerRadius,
    cornersRadius = cornersRadius,
)
