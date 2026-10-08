package com.yandex.div.test.data

import com.yandex.div2.DivCornersRadius

fun cornersRadius(
    topLeft: Int? = null,
    topRight: Int? = null,
    bottomLeft: Int? = null,
    bottomRight: Int? = null,
): DivCornersRadius = DivCornersRadius(
    topLeft = topLeft?.let { constant(it.toLong()) },
    topRight = topRight?.let { constant(it.toLong()) },
    bottomLeft = bottomLeft?.let { constant(it.toLong()) },
    bottomRight = bottomRight?.let { constant(it.toLong()) },
)
