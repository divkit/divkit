package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivPatch

fun change(
    id: String,
    items: List<Div>? = null,
): DivPatch.Change {
    return DivPatch.Change(
        id = id,
        items = items,
    )
}
