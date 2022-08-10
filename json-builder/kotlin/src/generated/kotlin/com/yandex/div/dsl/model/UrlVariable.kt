// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class UrlVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<URI>?,
) : DivVariable() {

    @JsonProperty("type") override val type = "url"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.urlVariable(): LiteralProperty<UrlVariable> {
    return value(UrlVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.urlVariable(
    name: Property<String>? = null,
    value: Property<URI>? = null,
): LiteralProperty<UrlVariable> {
    return value(UrlVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.urlVariable(
    name: String? = null,
    value: URI? = null,
): LiteralProperty<UrlVariable> {
    return value(UrlVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.urlVariable(
    name: ValueProperty<String>,
    value: ValueProperty<URI>,
): UrlVariable {
    return UrlVariable(
        name = name,
        value = value,
    )
}

fun CardContext.urlVariable(
    name: String,
    value: URI,
): UrlVariable {
    return UrlVariable(
        name = value(name),
        value = value(value),
    )
}
