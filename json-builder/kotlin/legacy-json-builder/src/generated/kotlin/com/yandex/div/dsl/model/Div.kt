// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

sealed interface Div {
    val type: String
}

private class TemplatedDiv constructor(
    @JsonProperty("type") override val type: String,
    @JsonIgnore val bindings: Array<out TemplateBinding<*>>
) : Div {

    @JsonAnyGetter
    fun properties(): Map<String, Any> {
        return propertyMapOf(*bindings)
    }
}

fun <T> TemplateContext<T>.template(
    type: String,
    vararg bindings: PropertyOverriding<*>
): LiteralProperty<Div> {
    return LiteralProperty(TemplatedDiv(type, bindings))
}

fun CardContext.template(
    type: String,
    vararg bindings: TemplateBinding<*>
): Div {
    return TemplatedDiv(type, bindings)
}
