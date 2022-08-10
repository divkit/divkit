// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class BoolIntVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<BoolInt>?,
) : DivVariable() {

    @JsonProperty("type") override val type = "bool_int"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.boolIntVariable(): LiteralProperty<BoolIntVariable> {
    return value(BoolIntVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.boolIntVariable(
    name: Property<String>? = null,
    value: Property<BoolInt>? = null,
): LiteralProperty<BoolIntVariable> {
    return value(BoolIntVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.boolIntVariable(
    name: String? = null,
    value: BoolInt? = null,
): LiteralProperty<BoolIntVariable> {
    return value(BoolIntVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.boolIntVariable(
    name: ValueProperty<String>,
    value: ValueProperty<BoolInt>,
): BoolIntVariable {
    return BoolIntVariable(
        name = name,
        value = value,
    )
}

fun CardContext.boolIntVariable(
    name: String,
    value: BoolInt,
): BoolIntVariable {
    return BoolIntVariable(
        name = value(name),
        value = value(value),
    )
}
