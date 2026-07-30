package com.yandex.div.test.data

import com.yandex.div2.DivPivot
import com.yandex.div2.DivPivotFixed

fun fixedPivot(value: Int): DivPivot {
    return DivPivot.Fixed(DivPivotFixed(value = constant(value.toLong())))
}
