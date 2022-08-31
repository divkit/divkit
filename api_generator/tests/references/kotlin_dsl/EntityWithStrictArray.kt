// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithStrictArray internal constructor(
    @JsonIgnore val array: Property<List<Entity>>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_strict_array"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "array" to array,
        )
    }
}

fun <T> TemplateContext<T>.entityWithStrictArray(): LiteralProperty<EntityWithStrictArray> {
    return value(EntityWithStrictArray(
        array = null,
    ))
}

fun <T> TemplateContext<T>.entityWithStrictArray(
    array: Property<List<Entity>>? = null,
): LiteralProperty<EntityWithStrictArray> {
    return value(EntityWithStrictArray(
        array = array,
    ))
}

fun <T> TemplateContext<T>.entityWithStrictArray(
    array: List<Entity>? = null,
): LiteralProperty<EntityWithStrictArray> {
    return value(EntityWithStrictArray(
        array = optionalValue(array),
    ))
}

fun CardContext.entityWithStrictArray(
    array: ValueProperty<List<Entity>>,
): EntityWithStrictArray {
    return EntityWithStrictArray(
        array = array,
    )
}

fun CardContext.entityWithStrictArray(
    array: List<Entity>,
): EntityWithStrictArray {
    return EntityWithStrictArray(
        array = value(array),
    )
}
