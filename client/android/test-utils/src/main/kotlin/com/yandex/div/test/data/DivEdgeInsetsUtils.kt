package com.yandex.div.test.data

import com.yandex.div2.DivEdgeInsets

fun insets(
    start: Int? = null,
    end: Int? = null,
    top: Int = 0,
    bottom: Int = 0
): DivEdgeInsets {
    return DivEdgeInsets(
        start = start?.let { constant(it.toLong()) },
        end = end?.let { constant(it.toLong()) },
        top = constant(top.toLong()),
        bottom = constant(bottom.toLong())
    )
}
