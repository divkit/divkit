package com.yandex.div.test.data

import com.yandex.div2.DivBlur
import com.yandex.div2.DivFilter
import com.yandex.div2.DivFilterRtlMirror

fun blurFilter(radius: Int): DivFilter {
    return DivFilter.Blur(DivBlur(radius = constant(radius.toLong())))
}

fun rtlMirrorFilter(): DivFilter {
    return DivFilter.RtlMirror(DivFilterRtlMirror())
}
