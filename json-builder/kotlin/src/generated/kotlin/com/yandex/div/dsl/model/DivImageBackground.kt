// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivImageBackground internal constructor(
    @JsonIgnore val alpha: Property<Double>?,
    @JsonIgnore val contentAlignmentHorizontal: Property<DivAlignmentHorizontal>?,
    @JsonIgnore val contentAlignmentVertical: Property<DivAlignmentVertical>?,
    @JsonIgnore val filters: Property<List<DivFilter>>?,
    @JsonIgnore val imageUrl: Property<URI>?,
    @JsonIgnore val preloadRequired: Property<BoolInt>?,
    @JsonIgnore val scale: Property<DivImageScale>?,
) : DivBackground {

    @JsonProperty("type") override val type = "image"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "alpha" to alpha,
            "content_alignment_horizontal" to contentAlignmentHorizontal,
            "content_alignment_vertical" to contentAlignmentVertical,
            "filters" to filters,
            "image_url" to imageUrl,
            "preload_required" to preloadRequired,
            "scale" to scale,
        )
    }
}

fun <T> TemplateContext<T>.divImageBackground(): LiteralProperty<DivImageBackground> {
    return value(DivImageBackground(
        alpha = null,
        contentAlignmentHorizontal = null,
        contentAlignmentVertical = null,
        filters = null,
        imageUrl = null,
        preloadRequired = null,
        scale = null,
    ))
}

fun <T> TemplateContext<T>.divImageBackground(
    imageUrl: Property<URI>? = null,
    alpha: Property<Double>? = null,
    contentAlignmentHorizontal: Property<DivAlignmentHorizontal>? = null,
    contentAlignmentVertical: Property<DivAlignmentVertical>? = null,
    filters: Property<List<DivFilter>>? = null,
    preloadRequired: Property<BoolInt>? = null,
    scale: Property<DivImageScale>? = null,
): LiteralProperty<DivImageBackground> {
    return value(DivImageBackground(
        alpha = alpha,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        filters = filters,
        imageUrl = imageUrl,
        preloadRequired = preloadRequired,
        scale = scale,
    ))
}

fun <T> TemplateContext<T>.divImageBackground(
    imageUrl: URI? = null,
    alpha: Double? = null,
    contentAlignmentHorizontal: DivAlignmentHorizontal? = null,
    contentAlignmentVertical: DivAlignmentVertical? = null,
    filters: List<DivFilter>? = null,
    preloadRequired: BoolInt? = null,
    scale: DivImageScale? = null,
): LiteralProperty<DivImageBackground> {
    return value(DivImageBackground(
        alpha = optionalValue(alpha),
        contentAlignmentHorizontal = optionalValue(contentAlignmentHorizontal),
        contentAlignmentVertical = optionalValue(contentAlignmentVertical),
        filters = optionalValue(filters),
        imageUrl = optionalValue(imageUrl),
        preloadRequired = optionalValue(preloadRequired),
        scale = optionalValue(scale),
    ))
}

fun CardContext.divImageBackground(
    imageUrl: ValueProperty<URI>,
    alpha: ValueProperty<Double>? = null,
    contentAlignmentHorizontal: ValueProperty<DivAlignmentHorizontal>? = null,
    contentAlignmentVertical: ValueProperty<DivAlignmentVertical>? = null,
    filters: ValueProperty<List<DivFilter>>? = null,
    preloadRequired: ValueProperty<BoolInt>? = null,
    scale: ValueProperty<DivImageScale>? = null,
): DivImageBackground {
    return DivImageBackground(
        alpha = alpha,
        contentAlignmentHorizontal = contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical,
        filters = filters,
        imageUrl = imageUrl,
        preloadRequired = preloadRequired,
        scale = scale,
    )
}

fun CardContext.divImageBackground(
    imageUrl: URI,
    alpha: Double? = null,
    contentAlignmentHorizontal: DivAlignmentHorizontal? = null,
    contentAlignmentVertical: DivAlignmentVertical? = null,
    filters: List<DivFilter>? = null,
    preloadRequired: BoolInt? = null,
    scale: DivImageScale? = null,
): DivImageBackground {
    return DivImageBackground(
        alpha = optionalValue(alpha),
        contentAlignmentHorizontal = optionalValue(contentAlignmentHorizontal),
        contentAlignmentVertical = optionalValue(contentAlignmentVertical),
        filters = optionalValue(filters),
        imageUrl = value(imageUrl),
        preloadRequired = optionalValue(preloadRequired),
        scale = optionalValue(scale),
    )
}
