// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class BooleanVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<BoolInt>?,
) : DivVariable {

    @JsonProperty("type") override val type = "boolean"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.booleanVariable(): LiteralProperty<BooleanVariable> {
    return value(BooleanVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.booleanVariable(
    name: Property<String>? = null,
    value: Property<BoolInt>? = null,
): LiteralProperty<BooleanVariable> {
    return value(BooleanVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.booleanVariable(
    name: String? = null,
    value: BoolInt? = null,
): LiteralProperty<BooleanVariable> {
    return value(BooleanVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.booleanVariable(
    name: ValueProperty<String>,
    value: ValueProperty<BoolInt>,
): BooleanVariable {
    return BooleanVariable(
        name = name,
        value = value,
    )
}

fun CardContext.booleanVariable(
    name: String,
    value: BoolInt,
): BooleanVariable {
    return BooleanVariable(
        name = value(name),
        value = value(value),
    )
}
