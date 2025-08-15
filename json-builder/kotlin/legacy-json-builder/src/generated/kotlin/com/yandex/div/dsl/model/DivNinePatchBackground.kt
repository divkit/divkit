// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivNinePatchBackground internal constructor(
    @JsonIgnore val imageUrl: Property<URI>?,
    @JsonIgnore val insets: Property<DivAbsoluteEdgeInsets>?,
) : DivBackground {

    @JsonProperty("type") override val type = "nine_patch_image"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "image_url" to imageUrl,
            "insets" to insets,
        )
    }
}

fun <T> TemplateContext<T>.divNinePatchBackground(): LiteralProperty<DivNinePatchBackground> {
    return value(DivNinePatchBackground(
        imageUrl = null,
        insets = null,
    ))
}

fun <T> TemplateContext<T>.divNinePatchBackground(
    imageUrl: Property<URI>? = null,
    insets: Property<DivAbsoluteEdgeInsets>? = null,
): LiteralProperty<DivNinePatchBackground> {
    return value(DivNinePatchBackground(
        imageUrl = imageUrl,
        insets = insets,
    ))
}

fun <T> TemplateContext<T>.divNinePatchBackground(
    imageUrl: URI? = null,
    insets: DivAbsoluteEdgeInsets? = null,
): LiteralProperty<DivNinePatchBackground> {
    return value(DivNinePatchBackground(
        imageUrl = optionalValue(imageUrl),
        insets = optionalValue(insets),
    ))
}

fun CardContext.divNinePatchBackground(
    imageUrl: ValueProperty<URI>,
    insets: ValueProperty<DivAbsoluteEdgeInsets>,
): DivNinePatchBackground {
    return DivNinePatchBackground(
        imageUrl = imageUrl,
        insets = insets,
    )
}

fun CardContext.divNinePatchBackground(
    imageUrl: URI,
    insets: DivAbsoluteEdgeInsets,
): DivNinePatchBackground {
    return DivNinePatchBackground(
        imageUrl = value(imageUrl),
        insets = value(insets),
    )
}
