// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithOptionalComplexProperty internal constructor(
    @JsonIgnore val property: Property<Property>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_optional_complex_property"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "property" to property,
        )
    }

    class Property internal constructor(
        @JsonIgnore val value: Property<URI>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "value" to value,
            )
        }
    }
}

fun <T> TemplateContext<T>.entityWithOptionalComplexProperty(): LiteralProperty<EntityWithOptionalComplexProperty> {
    return value(EntityWithOptionalComplexProperty(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalComplexProperty(
    property: Property<EntityWithOptionalComplexProperty.Property>? = null,
): LiteralProperty<EntityWithOptionalComplexProperty> {
    return value(EntityWithOptionalComplexProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalComplexProperty(
    property: EntityWithOptionalComplexProperty.Property? = null,
): LiteralProperty<EntityWithOptionalComplexProperty> {
    return value(EntityWithOptionalComplexProperty(
        property = optionalValue(property),
    ))
}

fun <T> TemplateContext<T>.property(): LiteralProperty<EntityWithOptionalComplexProperty.Property> {
    return value(EntityWithOptionalComplexProperty.Property(
        value = null,
    ))
}

fun <T> TemplateContext<T>.property(
    value: Property<URI>? = null,
): LiteralProperty<EntityWithOptionalComplexProperty.Property> {
    return value(EntityWithOptionalComplexProperty.Property(
        value = value,
    ))
}

fun <T> TemplateContext<T>.property(
    value: URI? = null,
): LiteralProperty<EntityWithOptionalComplexProperty.Property> {
    return value(EntityWithOptionalComplexProperty.Property(
        value = optionalValue(value),
    ))
}

fun CardContext.entityWithOptionalComplexProperty(): EntityWithOptionalComplexProperty {
    return EntityWithOptionalComplexProperty(
        property = null,
    )
}

fun CardContext.entityWithOptionalComplexProperty(
    property: ValueProperty<EntityWithOptionalComplexProperty.Property>? = null,
): EntityWithOptionalComplexProperty {
    return EntityWithOptionalComplexProperty(
        property = property,
    )
}

fun CardContext.entityWithOptionalComplexProperty(
    property: EntityWithOptionalComplexProperty.Property? = null,
): EntityWithOptionalComplexProperty {
    return EntityWithOptionalComplexProperty(
        property = optionalValue(property),
    )
}

fun CardContext.property(
    value: ValueProperty<URI>,
): EntityWithOptionalComplexProperty.Property {
    return EntityWithOptionalComplexProperty.Property(
        value = value,
    )
}

fun CardContext.property(
    value: URI,
): EntityWithOptionalComplexProperty.Property {
    return EntityWithOptionalComplexProperty.Property(
        value = value(value),
    )
}
