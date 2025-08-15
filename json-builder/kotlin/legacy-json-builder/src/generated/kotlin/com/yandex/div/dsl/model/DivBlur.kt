// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivBlur internal constructor(
    @JsonIgnore val radius: Property<Int>?,
) : DivFilter {

    @JsonProperty("type") override val type = "blur"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "radius" to radius,
        )
    }
}

fun <T> TemplateContext<T>.divBlur(): LiteralProperty<DivBlur> {
    return value(DivBlur(
        radius = null,
    ))
}

fun <T> TemplateContext<T>.divBlur(
    radius: Property<Int>? = null,
): LiteralProperty<DivBlur> {
    return value(DivBlur(
        radius = radius,
    ))
}

fun <T> TemplateContext<T>.divBlur(
    radius: Int? = null,
): LiteralProperty<DivBlur> {
    return value(DivBlur(
        radius = optionalValue(radius),
    ))
}

fun CardContext.divBlur(
    radius: ValueProperty<Int>,
): DivBlur {
    return DivBlur(
        radius = radius,
    )
}

fun CardContext.divBlur(
    radius: Int,
): DivBlur {
    return DivBlur(
        radius = value(radius),
    )
}
