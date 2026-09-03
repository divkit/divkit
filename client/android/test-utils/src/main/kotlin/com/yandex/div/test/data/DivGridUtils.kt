package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivGrid
import com.yandex.div2.DivSize

fun grid(
    columnCount: Expression<Long>,
    height: DivSize = wrapContent(),
    id: String? = null,
    items: List<Div> = emptyList(),
    width: DivSize = matchParent(),
): Div = Div.Grid(
    DivGrid(
        columnCount = columnCount,
        height = height,
        id = id,
        items = items,
        width = width,
    )
)
