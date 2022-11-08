// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class IntegerVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<Int>?,
) : DivVariable {

    @JsonProperty("type") override val type = "integer"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.integerVariable(): LiteralProperty<IntegerVariable> {
    return value(IntegerVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.integerVariable(
    name: Property<String>? = null,
    value: Property<Int>? = null,
): LiteralProperty<IntegerVariable> {
    return value(IntegerVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.integerVariable(
    name: String? = null,
    value: Int? = null,
): LiteralProperty<IntegerVariable> {
    return value(IntegerVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.integerVariable(
    name: ValueProperty<String>,
    value: ValueProperty<Int>,
): IntegerVariable {
    return IntegerVariable(
        name = name,
        value = value,
    )
}

fun CardContext.integerVariable(
    name: String,
    value: Int,
): IntegerVariable {
    return IntegerVariable(
        name = value(name),
        value = value(value),
    )
}
