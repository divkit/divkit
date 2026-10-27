package com.yandex.div.test.data

import com.yandex.div2.DivCount
import com.yandex.div2.DivFixedCount
import com.yandex.div2.DivInfinityCount

fun fixedCount(value: Long): DivCount = DivCount.Fixed(DivFixedCount(constant(value)))

fun infinityCount(): DivCount = DivCount.Infinity(DivInfinityCount())
