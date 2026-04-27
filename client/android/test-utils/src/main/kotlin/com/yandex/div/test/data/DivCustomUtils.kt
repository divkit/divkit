package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivCustom
import org.json.JSONObject

fun custom(
    type: String,
    customProps: JSONObject? = null
): Div {
    return Div.Custom(
        DivCustom(
            customType = type,
            customProps = customProps
        )
    )
}
