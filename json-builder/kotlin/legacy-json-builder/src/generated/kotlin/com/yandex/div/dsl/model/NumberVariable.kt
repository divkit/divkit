// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class NumberVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<Double>?,
) : DivVariable {

    @JsonProperty("type") override val type = "number"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.numberVariable(): LiteralProperty<NumberVariable> {
    return value(NumberVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.numberVariable(
    name: Property<String>? = null,
    value: Property<Double>? = null,
): LiteralProperty<NumberVariable> {
    return value(NumberVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.numberVariable(
    name: String? = null,
    value: Double? = null,
): LiteralProperty<NumberVariable> {
    return value(NumberVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.numberVariable(
    name: ValueProperty<String>,
    value: ValueProperty<Double>,
): NumberVariable {
    return NumberVariable(
        name = name,
        value = value,
    )
}

fun CardContext.numberVariable(
    name: String,
    value: Double,
): NumberVariable {
    return NumberVariable(
        name = value(name),
        value = value(value),
    )
}
