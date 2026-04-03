package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivSize
import com.yandex.div2.DivWrapContentSize

fun fixed(value: Expression<Long>): DivSize = DivSize.Fixed(DivFixedSize(value = value))

fun wrapContent(): DivSize = DivSize.WrapContent(DivWrapContentSize())
