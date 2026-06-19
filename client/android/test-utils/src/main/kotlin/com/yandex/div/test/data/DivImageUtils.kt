package com.yandex.div.test.data

import android.net.Uri
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBackground
import com.yandex.div2.DivImage
import com.yandex.div2.DivImageBackground

fun image(
    accessibility: DivAccessibility? = null,
    background: List<DivBackground>? = null,
    id: String? = null,
    imageUrl: Expression<Uri>,
    preloadRequired: Expression<Boolean> = constant(false),
): Div {
    return Div.Image(
        value = DivImage(
            accessibility = accessibility,
            background = background,
            id = id,
            imageUrl = imageUrl,
            preloadRequired = preloadRequired,
        )
    )
}

fun imageBackground(
    imageUrl: Expression<Uri>,
    preloadRequired: Expression<Boolean> = constant(false),
): DivBackground.Image {
    return DivBackground.Image(
        value = DivImageBackground(
            imageUrl = imageUrl,
            preloadRequired = preloadRequired,
        )
    )
}
