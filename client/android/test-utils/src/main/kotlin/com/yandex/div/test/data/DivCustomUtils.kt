package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivCustom
import com.yandex.div2.DivSize
import org.json.JSONObject

fun custom(
    type: String,
    customProps: JSONObject? = null,
    height: DivSize = wrapContent(),
): Div {
    return Div.Custom(
        DivCustom(
            customProps = customProps,
            customType = type,
            height = height,
        )
    )
}
