// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivPercentageSize internal constructor(
    @JsonIgnore val value: Property<Double>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divPercentageSize(): LiteralProperty<DivPercentageSize> {
    return value(DivPercentageSize(
        value = null,
    ))
}

fun <T> TemplateContext<T>.divPercentageSize(
    value: Property<Double>? = null,
): LiteralProperty<DivPercentageSize> {
    return value(DivPercentageSize(
        value = value,
    ))
}

fun <T> TemplateContext<T>.divPercentageSize(
    value: Double? = null,
): LiteralProperty<DivPercentageSize> {
    return value(DivPercentageSize(
        value = optionalValue(value),
    ))
}

fun CardContext.divPercentageSize(
    value: ValueProperty<Double>,
): DivPercentageSize {
    return DivPercentageSize(
        value = value,
    )
}

fun CardContext.divPercentageSize(
    value: Double,
): DivPercentageSize {
    return DivPercentageSize(
        value = value(value),
    )
}
