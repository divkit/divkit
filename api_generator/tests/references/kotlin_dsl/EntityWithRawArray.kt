// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithRawArray internal constructor(
    @JsonIgnore val array: Property<List<Any>>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_raw_array"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "array" to array,
        )
    }
}

fun <T> TemplateContext<T>.entityWithRawArray(): LiteralProperty<EntityWithRawArray> {
    return value(EntityWithRawArray(
        array = null,
    ))
}

fun <T> TemplateContext<T>.entityWithRawArray(
    array: Property<List<Any>>? = null,
): LiteralProperty<EntityWithRawArray> {
    return value(EntityWithRawArray(
        array = array,
    ))
}

fun <T> TemplateContext<T>.entityWithRawArray(
    array: List<Any>? = null,
): LiteralProperty<EntityWithRawArray> {
    return value(EntityWithRawArray(
        array = optionalValue(array),
    ))
}

fun CardContext.entityWithRawArray(
    array: ValueProperty<List<Any>>,
): EntityWithRawArray {
    return EntityWithRawArray(
        array = array,
    )
}

fun CardContext.entityWithRawArray(
    array: List<Any>,
): EntityWithRawArray {
    return EntityWithRawArray(
        array = value(array),
    )
}
