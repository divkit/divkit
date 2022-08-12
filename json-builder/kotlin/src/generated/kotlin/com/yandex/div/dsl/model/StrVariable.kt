// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class StrVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<String>?,
) : DivVariable() {

    @JsonProperty("type") override val type = "string"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.strVariable(): LiteralProperty<StrVariable> {
    return value(StrVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.strVariable(
    name: Property<String>? = null,
    value: Property<String>? = null,
): LiteralProperty<StrVariable> {
    return value(StrVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.strVariable(
    name: String? = null,
    value: String? = null,
): LiteralProperty<StrVariable> {
    return value(StrVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.strVariable(
    name: ValueProperty<String>,
    value: ValueProperty<String>,
): StrVariable {
    return StrVariable(
        name = name,
        value = value,
    )
}

fun CardContext.strVariable(
    name: String,
    value: String,
): StrVariable {
    return StrVariable(
        name = value(name),
        value = value(value),
    )
}
