// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithComplexProperty internal constructor(
    @JsonIgnore val property: Property<Property>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_complex_property"

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

fun <T> TemplateContext<T>.entityWithComplexProperty(): LiteralProperty<EntityWithComplexProperty> {
    return value(EntityWithComplexProperty(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithComplexProperty(
    property: Property<EntityWithComplexProperty.Property>? = null,
): LiteralProperty<EntityWithComplexProperty> {
    return value(EntityWithComplexProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithComplexProperty(
    property: EntityWithComplexProperty.Property? = null,
): LiteralProperty<EntityWithComplexProperty> {
    return value(EntityWithComplexProperty(
        property = optionalValue(property),
    ))
}

fun <T> TemplateContext<T>.property(): LiteralProperty<EntityWithComplexProperty.Property> {
    return value(EntityWithComplexProperty.Property(
        value = null,
    ))
}

fun <T> TemplateContext<T>.property(
    value: Property<URI>? = null,
): LiteralProperty<EntityWithComplexProperty.Property> {
    return value(EntityWithComplexProperty.Property(
        value = value,
    ))
}

fun <T> TemplateContext<T>.property(
    value: URI? = null,
): LiteralProperty<EntityWithComplexProperty.Property> {
    return value(EntityWithComplexProperty.Property(
        value = optionalValue(value),
    ))
}

fun CardContext.entityWithComplexProperty(
    property: ValueProperty<EntityWithComplexProperty.Property>,
): EntityWithComplexProperty {
    return EntityWithComplexProperty(
        property = property,
    )
}

fun CardContext.entityWithComplexProperty(
    property: EntityWithComplexProperty.Property,
): EntityWithComplexProperty {
    return EntityWithComplexProperty(
        property = value(property),
    )
}

fun CardContext.property(
    value: ValueProperty<URI>,
): EntityWithComplexProperty.Property {
    return EntityWithComplexProperty.Property(
        value = value,
    )
}

fun CardContext.property(
    value: URI,
): EntityWithComplexProperty.Property {
    return EntityWithComplexProperty.Property(
        value = value(value),
    )
}
