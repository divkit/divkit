// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivBorder internal constructor(
    @JsonIgnore val cornerRadius: Property<Int>?,
    @JsonIgnore val cornersRadius: Property<DivCornersRadius>?,
    @JsonIgnore val hasShadow: Property<BoolInt>?,
    @JsonIgnore val shadow: Property<DivShadow>?,
    @JsonIgnore val stroke: Property<DivStroke>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "corner_radius" to cornerRadius,
            "corners_radius" to cornersRadius,
            "has_shadow" to hasShadow,
            "shadow" to shadow,
            "stroke" to stroke,
        )
    }
}

fun <T> TemplateContext<T>.divBorder(): LiteralProperty<DivBorder> {
    return value(DivBorder(
        cornerRadius = null,
        cornersRadius = null,
        hasShadow = null,
        shadow = null,
        stroke = null,
    ))
}

fun <T> TemplateContext<T>.divBorder(
    cornerRadius: Property<Int>? = null,
    cornersRadius: Property<DivCornersRadius>? = null,
    hasShadow: Property<BoolInt>? = null,
    shadow: Property<DivShadow>? = null,
    stroke: Property<DivStroke>? = null,
): LiteralProperty<DivBorder> {
    return value(DivBorder(
        cornerRadius = cornerRadius,
        cornersRadius = cornersRadius,
        hasShadow = hasShadow,
        shadow = shadow,
        stroke = stroke,
    ))
}

fun <T> TemplateContext<T>.divBorder(
    cornerRadius: Int? = null,
    cornersRadius: DivCornersRadius? = null,
    hasShadow: BoolInt? = null,
    shadow: DivShadow? = null,
    stroke: DivStroke? = null,
): LiteralProperty<DivBorder> {
    return value(DivBorder(
        cornerRadius = optionalValue(cornerRadius),
        cornersRadius = optionalValue(cornersRadius),
        hasShadow = optionalValue(hasShadow),
        shadow = optionalValue(shadow),
        stroke = optionalValue(stroke),
    ))
}

fun CardContext.divBorder(): DivBorder {
    return DivBorder(
        cornerRadius = null,
        cornersRadius = null,
        hasShadow = null,
        shadow = null,
        stroke = null,
    )
}

fun CardContext.divBorder(
    cornerRadius: ValueProperty<Int>? = null,
    cornersRadius: ValueProperty<DivCornersRadius>? = null,
    hasShadow: ValueProperty<BoolInt>? = null,
    shadow: ValueProperty<DivShadow>? = null,
    stroke: ValueProperty<DivStroke>? = null,
): DivBorder {
    return DivBorder(
        cornerRadius = cornerRadius,
        cornersRadius = cornersRadius,
        hasShadow = hasShadow,
        shadow = shadow,
        stroke = stroke,
    )
}

fun CardContext.divBorder(
    cornerRadius: Int? = null,
    cornersRadius: DivCornersRadius? = null,
    hasShadow: BoolInt? = null,
    shadow: DivShadow? = null,
    stroke: DivStroke? = null,
): DivBorder {
    return DivBorder(
        cornerRadius = optionalValue(cornerRadius),
        cornersRadius = optionalValue(cornersRadius),
        hasShadow = optionalValue(hasShadow),
        shadow = optionalValue(shadow),
        stroke = optionalValue(stroke),
    )
}
