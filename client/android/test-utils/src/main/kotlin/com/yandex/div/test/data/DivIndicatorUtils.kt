package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivSize

fun indicator(
    activeShape: DivRoundedRectangleShape? = null,
    id: String? = null,
    pagerId: String? = null,
    spaceBetweenCenters: DivFixedSize = DivFixedSize(value = constant(15)),
    width: DivSize = matchParent(),
): Div.Indicator = Div.Indicator(DivIndicator(
    activeShape = activeShape,
    id = id,
    pagerId = pagerId,
    spaceBetweenCenters = spaceBetweenCenters,
    width = width,
))
