// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithComplexPropertyWithDefaultValue internal constructor(
    @JsonIgnore val property: Property<Property>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_complex_property_with_default_value"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "property" to property,
        )
    }

    class Property internal constructor(
        @JsonIgnore val value: Property<String>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "value" to value,
            )
        }
    }
}

fun <T> TemplateContext<T>.entityWithComplexPropertyWithDefaultValue(): LiteralProperty<EntityWithComplexPropertyWithDefaultValue> {
    return value(EntityWithComplexPropertyWithDefaultValue(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithComplexPropertyWithDefaultValue(
    property: Property<EntityWithComplexPropertyWithDefaultValue.Property>? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue> {
    return value(EntityWithComplexPropertyWithDefaultValue(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithComplexPropertyWithDefaultValue(
    property: EntityWithComplexPropertyWithDefaultValue.Property? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue> {
    return value(EntityWithComplexPropertyWithDefaultValue(
        property = optionalValue(property),
    ))
}

fun <T> TemplateContext<T>.property(): LiteralProperty<EntityWithComplexPropertyWithDefaultValue.Property> {
    return value(EntityWithComplexPropertyWithDefaultValue.Property(
        value = null,
    ))
}

fun <T> TemplateContext<T>.property(
    value: Property<String>? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue.Property> {
    return value(EntityWithComplexPropertyWithDefaultValue.Property(
        value = value,
    ))
}

fun <T> TemplateContext<T>.property(
    value: String? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue.Property> {
    return value(EntityWithComplexPropertyWithDefaultValue.Property(
        value = optionalValue(value),
    ))
}

fun CardContext.entityWithComplexPropertyWithDefaultValue(): EntityWithComplexPropertyWithDefaultValue {
    return EntityWithComplexPropertyWithDefaultValue(
        property = null,
    )
}

fun CardContext.entityWithComplexPropertyWithDefaultValue(
    property: ValueProperty<EntityWithComplexPropertyWithDefaultValue.Property>? = null,
): EntityWithComplexPropertyWithDefaultValue {
    return EntityWithComplexPropertyWithDefaultValue(
        property = property,
    )
}

fun CardContext.entityWithComplexPropertyWithDefaultValue(
    property: EntityWithComplexPropertyWithDefaultValue.Property? = null,
): EntityWithComplexPropertyWithDefaultValue {
    return EntityWithComplexPropertyWithDefaultValue(
        property = optionalValue(property),
    )
}

fun CardContext.property(
    value: ValueProperty<String>,
): EntityWithComplexPropertyWithDefaultValue.Property {
    return EntityWithComplexPropertyWithDefaultValue.Property(
        value = value,
    )
}

fun CardContext.property(
    value: String,
): EntityWithComplexPropertyWithDefaultValue.Property {
    return EntityWithComplexPropertyWithDefaultValue.Property(
        value = value(value),
    )
}
