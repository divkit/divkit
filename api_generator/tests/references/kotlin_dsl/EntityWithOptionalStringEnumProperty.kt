// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithOptionalStringEnumProperty internal constructor(
    @JsonIgnore val property: Property<Property>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_optional_string_enum_property"

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

fun <T> TemplateContext<T>.entityWithOptionalStringEnumProperty(): LiteralProperty<EntityWithOptionalStringEnumProperty> {
    return value(EntityWithOptionalStringEnumProperty(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalStringEnumProperty(
    property: Property<EntityWithOptionalStringEnumProperty.Property>? = null,
): LiteralProperty<EntityWithOptionalStringEnumProperty> {
    return value(EntityWithOptionalStringEnumProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalStringEnumProperty(
    property: EntityWithOptionalStringEnumProperty.Property? = null,
): LiteralProperty<EntityWithOptionalStringEnumProperty> {
    return value(EntityWithOptionalStringEnumProperty(
        property = optionalValue(property),
    ))
}

fun CardContext.entityWithOptionalStringEnumProperty(): EntityWithOptionalStringEnumProperty {
    return EntityWithOptionalStringEnumProperty(
        property = null,
    )
}

fun CardContext.entityWithOptionalStringEnumProperty(
    property: ValueProperty<EntityWithOptionalStringEnumProperty.Property>? = null,
): EntityWithOptionalStringEnumProperty {
    return EntityWithOptionalStringEnumProperty(
        property = property,
    )
}

fun CardContext.entityWithOptionalStringEnumProperty(
    property: EntityWithOptionalStringEnumProperty.Property? = null,
): EntityWithOptionalStringEnumProperty {
    return EntityWithOptionalStringEnumProperty(
        property = optionalValue(property),
    )
}
