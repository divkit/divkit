// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithArrayWithTransform internal constructor(
    @JsonIgnore val array: Property<List<Color>>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_array_with_transform"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "array" to array,
        )
    }
}

fun <T> TemplateContext<T>.entityWithArrayWithTransform(): LiteralProperty<EntityWithArrayWithTransform> {
    return value(EntityWithArrayWithTransform(
        array = null,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayWithTransform(
    array: Property<List<Color>>? = null,
): LiteralProperty<EntityWithArrayWithTransform> {
    return value(EntityWithArrayWithTransform(
        array = array,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayWithTransform(
    array: List<Color>? = null,
): LiteralProperty<EntityWithArrayWithTransform> {
    return value(EntityWithArrayWithTransform(
        array = optionalValue(array),
    ))
}

fun CardContext.entityWithArrayWithTransform(
    array: ValueProperty<List<Color>>,
): EntityWithArrayWithTransform {
    return EntityWithArrayWithTransform(
        array = array,
    )
}

fun CardContext.entityWithArrayWithTransform(
    array: List<Color>,
): EntityWithArrayWithTransform {
    return EntityWithArrayWithTransform(
        array = value(array),
    )
}
