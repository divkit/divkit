// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithStringArrayProperty internal constructor(
    @JsonIgnore val array: Property<List<String>>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_string_array_property"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "array" to array,
        )
    }
}

fun <T> TemplateContext<T>.entityWithStringArrayProperty(): LiteralProperty<EntityWithStringArrayProperty> {
    return value(EntityWithStringArrayProperty(
        array = null,
    ))
}

fun <T> TemplateContext<T>.entityWithStringArrayProperty(
    array: Property<List<String>>? = null,
): LiteralProperty<EntityWithStringArrayProperty> {
    return value(EntityWithStringArrayProperty(
        array = array,
    ))
}

fun <T> TemplateContext<T>.entityWithStringArrayProperty(
    array: List<String>? = null,
): LiteralProperty<EntityWithStringArrayProperty> {
    return value(EntityWithStringArrayProperty(
        array = optionalValue(array),
    ))
}

fun CardContext.entityWithStringArrayProperty(
    array: ValueProperty<List<String>>,
): EntityWithStringArrayProperty {
    return EntityWithStringArrayProperty(
        array = array,
    )
}

fun CardContext.entityWithStringArrayProperty(
    array: List<String>,
): EntityWithStringArrayProperty {
    return EntityWithStringArrayProperty(
        array = value(array),
    )
}
