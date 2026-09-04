package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivGallery
import com.yandex.div2.DivSize

fun gallery(
    accessibility: DivAccessibility? = null,
    columnCount: Expression<Long>? = null,
    defaultItem: Expression<Long> = constant(0L),
    height: DivSize = wrapContent(),
    id: String? = null,
    itemSpacing: Expression<Long> = constant(8L),
    items: List<Div> = emptyList(),
    paddings: DivEdgeInsets? = null,
    scrollContentAlignment: Expression<DivGallery.ContentAlignment>? = null,
    scrollMode: Expression<DivGallery.ScrollMode> = constant(DivGallery.ScrollMode.DEFAULT),
    width: DivSize = matchParent(),
): Div {
    return Div.Gallery(
        value = DivGallery(
            accessibility = accessibility,
            columnCount = columnCount,
            defaultItem = defaultItem,
            height = height,
            id = id,
            itemSpacing = itemSpacing,
            items = items,
            paddings = paddings,
            scrollContentAlignment = scrollContentAlignment,
            scrollMode = scrollMode,
            width = width,
        )
    )
}
