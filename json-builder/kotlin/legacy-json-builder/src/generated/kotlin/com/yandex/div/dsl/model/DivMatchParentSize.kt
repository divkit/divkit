// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivMatchParentSize internal constructor(
    @JsonIgnore val weight: Property<Double>?,
) : DivSize {

    @JsonProperty("type") override val type = "match_parent"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "weight" to weight,
        )
    }
}

fun <T> TemplateContext<T>.divMatchParentSize(): LiteralProperty<DivMatchParentSize> {
    return value(DivMatchParentSize(
        weight = null,
    ))
}

fun <T> TemplateContext<T>.divMatchParentSize(
    weight: Property<Double>? = null,
): LiteralProperty<DivMatchParentSize> {
    return value(DivMatchParentSize(
        weight = weight,
    ))
}

fun <T> TemplateContext<T>.divMatchParentSize(
    weight: Double? = null,
): LiteralProperty<DivMatchParentSize> {
    return value(DivMatchParentSize(
        weight = optionalValue(weight),
    ))
}

fun CardContext.divMatchParentSize(): DivMatchParentSize {
    return DivMatchParentSize(
        weight = null,
    )
}

fun CardContext.divMatchParentSize(
    weight: ValueProperty<Double>? = null,
): DivMatchParentSize {
    return DivMatchParentSize(
        weight = weight,
    )
}

fun CardContext.divMatchParentSize(
    weight: Double? = null,
): DivMatchParentSize {
    return DivMatchParentSize(
        weight = optionalValue(weight),
    )
}
