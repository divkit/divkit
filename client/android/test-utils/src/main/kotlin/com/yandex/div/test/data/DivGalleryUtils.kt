package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivGallery

fun gallery(
    accessibility: DivAccessibility? = null,
    id: String? = null,
    items: List<Div> = emptyList(),
): Div {
    return Div.Gallery(
        value = DivGallery(
            accessibility = accessibility,
            id = id,
            items = items,
        )
    )
}
