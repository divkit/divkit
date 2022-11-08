// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithStringEnumProperty internal constructor(
    @JsonIgnore val property: Property<Property>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_string_enum_property"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "property" to property,
        )
    }

    enum class Property(@JsonValue val value: String) {
        FIRST("first"),
        SECOND("second"),
    }
}

fun <T> TemplateContext<T>.entityWithStringEnumProperty(): LiteralProperty<EntityWithStringEnumProperty> {
    return value(EntityWithStringEnumProperty(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithStringEnumProperty(
    property: Property<EntityWithStringEnumProperty.Property>? = null,
): LiteralProperty<EntityWithStringEnumProperty> {
    return value(EntityWithStringEnumProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithStringEnumProperty(
    property: EntityWithStringEnumProperty.Property? = null,
): LiteralProperty<EntityWithStringEnumProperty> {
    return value(EntityWithStringEnumProperty(
        property = optionalValue(property),
    ))
}

fun CardContext.entityWithStringEnumProperty(
    property: ValueProperty<EntityWithStringEnumProperty.Property>,
): EntityWithStringEnumProperty {
    return EntityWithStringEnumProperty(
        property = property,
    )
}

fun CardContext.entityWithStringEnumProperty(
    property: EntityWithStringEnumProperty.Property,
): EntityWithStringEnumProperty {
    return EntityWithStringEnumProperty(
        property = value(property),
    )
}
