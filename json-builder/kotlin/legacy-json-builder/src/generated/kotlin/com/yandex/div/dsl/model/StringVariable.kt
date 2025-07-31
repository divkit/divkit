// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class StringVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<String>?,
) : DivVariable {

    @JsonProperty("type") override val type = "string"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.stringVariable(): LiteralProperty<StringVariable> {
    return value(StringVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.stringVariable(
    name: Property<String>? = null,
    value: Property<String>? = null,
): LiteralProperty<StringVariable> {
    return value(StringVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.stringVariable(
    name: String? = null,
    value: String? = null,
): LiteralProperty<StringVariable> {
    return value(StringVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.stringVariable(
    name: ValueProperty<String>,
    value: ValueProperty<String>,
): StringVariable {
    return StringVariable(
        name = name,
        value = value,
    )
}

fun CardContext.stringVariable(
    name: String,
    value: String,
): StringVariable {
    return StringVariable(
        name = value(name),
        value = value(value),
    )
}
