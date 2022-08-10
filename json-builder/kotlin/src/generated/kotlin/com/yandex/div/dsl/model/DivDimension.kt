// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivDimension internal constructor(
    @JsonIgnore val unit: Property<DivSizeUnit>?,
    @JsonIgnore val value: Property<Double>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "unit" to unit,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divDimension(): LiteralProperty<DivDimension> {
    return value(DivDimension(
        unit = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.divDimension(
    value: Property<Double>? = null,
    unit: Property<DivSizeUnit>? = null,
): LiteralProperty<DivDimension> {
    return value(DivDimension(
        unit = unit,
        value = value,
    ))
}

fun <T> TemplateContext<T>.divDimension(
    value: Double? = null,
    unit: DivSizeUnit? = null,
): LiteralProperty<DivDimension> {
    return value(DivDimension(
        unit = optionalValue(unit),
        value = optionalValue(value),
    ))
}

fun CardContext.divDimension(
    value: ValueProperty<Double>,
    unit: ValueProperty<DivSizeUnit>? = null,
): DivDimension {
    return DivDimension(
        unit = unit,
        value = value,
    )
}

fun CardContext.divDimension(
    value: Double,
    unit: DivSizeUnit? = null,
): DivDimension {
    return DivDimension(
        unit = optionalValue(unit),
        value = value(value),
    )
}
