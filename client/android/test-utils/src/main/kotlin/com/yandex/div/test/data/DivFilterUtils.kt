package com.yandex.div.test.data

import com.yandex.div2.DivBlur
import com.yandex.div2.DivFilter

fun blurFilter(radius: Int): DivFilter {
    return DivFilter.Blur(DivBlur(radius = constant(radius.toLong())))
}
