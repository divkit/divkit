package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivCustom
import com.yandex.div2.DivSize
import org.json.JSONObject

fun custom(
    type: String,
    customProps: JSONObject? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    items: List<Div>? = null,
    transitionOut: DivAppearanceTransition? = null,
    width: DivSize = matchParent(),
): Div {
    return Div.Custom(
        DivCustom(
            customProps = customProps,
            customType = type,
            height = height,
            id = id,
            items = items,
            transitionOut = transitionOut,
            width = width,
        )
    )
}
