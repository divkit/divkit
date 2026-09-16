package com.yandex.div.test.data

import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAnimatedTextGradient
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivStaticTextGradient
import com.yandex.div2.DivTextGradient

fun animatedTextGradient(
    duration: Expression<Long> = constant(1600L),
    colors: List<Int> = listOf(0xFFFF0000.toInt(), 0xFF0000FF.toInt()),
) = DivTextGradient.Animated(
    DivAnimatedTextGradient(
        duration = duration,
        gradient = DivStaticTextGradient.Linear(
            DivLinearGradient(colors = ConstantExpressionList(colors)),
        ),
    ),
)
