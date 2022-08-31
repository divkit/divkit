// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithArray internal constructor(
    @JsonIgnore val array: Property<List<Entity>>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_array"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "array" to array,
        )
    }
}

fun <T> TemplateContext<T>.entityWithArray(): LiteralProperty<EntityWithArray> {
    return value(EntityWithArray(
        array = null,
    ))
}

fun <T> TemplateContext<T>.entityWithArray(
    array: Property<List<Entity>>? = null,
): LiteralProperty<EntityWithArray> {
    return value(EntityWithArray(
        array = array,
    ))
}

fun <T> TemplateContext<T>.entityWithArray(
    array: List<Entity>? = null,
): LiteralProperty<EntityWithArray> {
    return value(EntityWithArray(
        array = optionalValue(array),
    ))
}

fun CardContext.entityWithArray(
    array: ValueProperty<List<Entity>>,
): EntityWithArray {
    return EntityWithArray(
        array = array,
    )
}

fun CardContext.entityWithArray(
    array: List<Entity>,
): EntityWithArray {
    return EntityWithArray(
        array = value(array),
    )
}
