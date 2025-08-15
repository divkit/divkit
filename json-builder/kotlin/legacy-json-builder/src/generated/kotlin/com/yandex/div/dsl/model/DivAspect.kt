// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivAspect internal constructor(
    @JsonIgnore val ratio: Property<Double>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "ratio" to ratio,
        )
    }
}

fun <T> TemplateContext<T>.divAspect(): LiteralProperty<DivAspect> {
    return value(DivAspect(
        ratio = null,
    ))
}

fun <T> TemplateContext<T>.divAspect(
    ratio: Property<Double>? = null,
): LiteralProperty<DivAspect> {
    return value(DivAspect(
        ratio = ratio,
    ))
}

fun <T> TemplateContext<T>.divAspect(
    ratio: Double? = null,
): LiteralProperty<DivAspect> {
    return value(DivAspect(
        ratio = optionalValue(ratio),
    ))
}

fun CardContext.divAspect(
    ratio: ValueProperty<Double>,
): DivAspect {
    return DivAspect(
        ratio = ratio,
    )
}

fun CardContext.divAspect(
    ratio: Double,
): DivAspect {
    return DivAspect(
        ratio = value(ratio),
    )
}
