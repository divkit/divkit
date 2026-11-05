package com.yandex.div.test.data

import android.net.Uri
import androidx.core.net.toUri
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivBackground
import com.yandex.div2.DivImage
import com.yandex.div2.DivSize
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivVisibilityAction

fun image(
    accessibility: DivAccessibility? = null,
    background: List<DivBackground>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    imageUrl: String?,
    preloadRequired: Boolean = false,
    transitionOut: DivAppearanceTransition? = null,
    visibility: DivVisibility = DivVisibility.VISIBLE,
    visibilityActions: List<DivVisibilityAction>? = null,
): Div {
    return image(
        accessibility = accessibility,
        background = background,
        height = height,
        id = id,
        imageUrl = imageUrl?.let { constant(it.toUri()) },
        preloadRequired = preloadRequired,
        transitionOut = transitionOut,
        visibility = visibility,
        visibilityActions = visibilityActions,
    )
}

fun image(
    accessibility: DivAccessibility? = null,
    background: List<DivBackground>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    imageUrl: Expression<Uri>? = null,
    preloadRequired: Boolean = false,
    transitionOut: DivAppearanceTransition? = null,
    visibility: DivVisibility = DivVisibility.VISIBLE,
    visibilityActions: List<DivVisibilityAction>? = null,
): Div {
    return Div.Image(
        value = DivImage(
            accessibility = accessibility,
            background = background,
            height = height,
            id = id,
            imageUrl = imageUrl,
            preloadRequired = constant(preloadRequired),
            transitionOut = transitionOut,
            visibility = constant(visibility),
            visibilityActions = visibilityActions,
        )
    )
}
