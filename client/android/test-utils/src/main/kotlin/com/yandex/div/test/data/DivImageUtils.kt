package com.yandex.div.test.data

import android.net.Uri
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivImage

fun image(
    accessibility: DivAccessibility? = null,
    id: String? = null,
    imageUrl: Expression<Uri>,
): Div {
    return Div.Image(
        value = DivImage(
            accessibility = accessibility,
            id = id,
            imageUrl = imageUrl,
        )
    )
}
