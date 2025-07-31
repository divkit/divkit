// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivShadow internal constructor(
    @JsonIgnore val alpha: Property<Double>?,
    @JsonIgnore val blur: Property<Int>?,
    @JsonIgnore val color: Property<Color>?,
    @JsonIgnore val offset: Property<DivPoint>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "alpha" to alpha,
            "blur" to blur,
            "color" to color,
            "offset" to offset,
        )
    }
}

fun <T> TemplateContext<T>.divShadow(): LiteralProperty<DivShadow> {
    return value(DivShadow(
        alpha = null,
        blur = null,
        color = null,
        offset = null,
    ))
}

fun <T> TemplateContext<T>.divShadow(
    offset: Property<DivPoint>? = null,
    alpha: Property<Double>? = null,
    blur: Property<Int>? = null,
    color: Property<Color>? = null,
): LiteralProperty<DivShadow> {
    return value(DivShadow(
        alpha = alpha,
        blur = blur,
        color = color,
        offset = offset,
    ))
}

fun <T> TemplateContext<T>.divShadow(
    offset: DivPoint? = null,
    alpha: Double? = null,
    blur: Int? = null,
    color: Color? = null,
): LiteralProperty<DivShadow> {
    return value(DivShadow(
        alpha = optionalValue(alpha),
        blur = optionalValue(blur),
        color = optionalValue(color),
        offset = optionalValue(offset),
    ))
}

fun CardContext.divShadow(
    offset: ValueProperty<DivPoint>,
    alpha: ValueProperty<Double>? = null,
    blur: ValueProperty<Int>? = null,
    color: ValueProperty<Color>? = null,
): DivShadow {
    return DivShadow(
        alpha = alpha,
        blur = blur,
        color = color,
        offset = offset,
    )
}

fun CardContext.divShadow(
    offset: DivPoint,
    alpha: Double? = null,
    blur: Int? = null,
    color: Color? = null,
): DivShadow {
    return DivShadow(
        alpha = optionalValue(alpha),
        blur = optionalValue(blur),
        color = optionalValue(color),
        offset = value(offset),
    )
}
