package com.yandex.div.test.data

import android.net.Uri
import androidx.core.net.toUri
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBackground
import com.yandex.div2.DivImage

fun image(
    accessibility: DivAccessibility? = null,
    background: List<DivBackground>? = null,
    id: String? = null,
    imageUrl: String? = null,
    preloadRequired: Boolean = false
): Div {
    return image(
        accessibility = accessibility,
        background = background,
        id = id,
        imageUrl = imageUrl?.let { constant(it.toUri()) },
        preloadRequired = preloadRequired
    )
}

fun image(
    accessibility: DivAccessibility? = null,
    background: List<DivBackground>? = null,
    id: String? = null,
    imageUrl: Expression<Uri>? = null,
    preloadRequired: Boolean = false
): Div {
    return Div.Image(
        value = DivImage(
            accessibility = accessibility,
            background = background,
            id = id,
            imageUrl = imageUrl,
            preloadRequired = constant(preloadRequired)
        )
    )
}
