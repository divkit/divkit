// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithArrayOfEnums internal constructor(
    @JsonIgnore val items: Property<List<Item>>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_array_of_enums"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "items" to items,
        )
    }

    enum class Item(@JsonValue val value: String) {
        FIRST("first"),
        SECOND("second"),
    }
}

fun <T> TemplateContext<T>.entityWithArrayOfEnums(): LiteralProperty<EntityWithArrayOfEnums> {
    return value(EntityWithArrayOfEnums(
        items = null,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayOfEnums(
    items: Property<List<EntityWithArrayOfEnums.Item>>? = null,
): LiteralProperty<EntityWithArrayOfEnums> {
    return value(EntityWithArrayOfEnums(
        items = items,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayOfEnums(
    items: List<EntityWithArrayOfEnums.Item>? = null,
): LiteralProperty<EntityWithArrayOfEnums> {
    return value(EntityWithArrayOfEnums(
        items = optionalValue(items),
    ))
}

fun CardContext.entityWithArrayOfEnums(
    items: ValueProperty<List<EntityWithArrayOfEnums.Item>>,
): EntityWithArrayOfEnums {
    return EntityWithArrayOfEnums(
        items = items,
    )
}

fun CardContext.entityWithArrayOfEnums(
    items: List<EntityWithArrayOfEnums.Item>,
): EntityWithArrayOfEnums {
    return EntityWithArrayOfEnums(
        items = value(items),
    )
}
