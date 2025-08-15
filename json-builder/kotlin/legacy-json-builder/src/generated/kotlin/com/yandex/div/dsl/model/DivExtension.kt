// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivExtension internal constructor(
    @JsonIgnore val id: Property<String>?,
    @JsonIgnore val params: Property<Map<String, Any>>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "id" to id,
            "params" to params,
        )
    }
}

fun <T> TemplateContext<T>.divExtension(): LiteralProperty<DivExtension> {
    return value(DivExtension(
        id = null,
        params = null,
    ))
}

fun <T> TemplateContext<T>.divExtension(
    id: Property<String>? = null,
    params: Property<Map<String, Any>>? = null,
): LiteralProperty<DivExtension> {
    return value(DivExtension(
        id = id,
        params = params,
    ))
}

fun <T> TemplateContext<T>.divExtension(
    id: String? = null,
    params: Map<String, Any>? = null,
): LiteralProperty<DivExtension> {
    return value(DivExtension(
        id = optionalValue(id),
        params = optionalValue(params),
    ))
}

fun CardContext.divExtension(
    id: ValueProperty<String>,
    params: ValueProperty<Map<String, Any>>? = null,
): DivExtension {
    return DivExtension(
        id = id,
        params = params,
    )
}

fun CardContext.divExtension(
    id: String,
    params: Map<String, Any>? = null,
): DivExtension {
    return DivExtension(
        id = value(id),
        params = optionalValue(params),
    )
}
