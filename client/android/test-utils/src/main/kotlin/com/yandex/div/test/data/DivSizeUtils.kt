package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.DivWrapContentSize

fun fixed(value: Int): DivSize = fixed(constant(value.toLong()))

fun fixed(value: Expression<Long>): DivSize = DivSize.Fixed(DivFixedSize(value = value))

fun matchParent(
    weight: Expression<Double>? = null,
    minSize: Int? = null,
    maxSize: Int? = null,
): DivSize = DivSize.MatchParent(DivMatchParentSize(
    weight = weight,
    minSize = minSize?.let { DivSizeUnitValue(value = constant(it.toLong())) },
    maxSize = maxSize?.let { DivSizeUnitValue(value = constant(it.toLong())) },
))

fun wrapContent(): DivSize = DivSize.WrapContent(DivWrapContentSize())
