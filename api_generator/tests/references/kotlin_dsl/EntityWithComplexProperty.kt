// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithComplexProperty internal constructor(
    @JsonIgnore val property: Property<ComplexProperty>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_complex_property"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "property" to property,
        )
    }

    class ComplexProperty internal constructor(
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
    property: Property<EntityWithComplexProperty.ComplexProperty>? = null,
): LiteralProperty<EntityWithComplexProperty> {
    return value(EntityWithComplexProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithComplexProperty(
    property: EntityWithComplexProperty.ComplexProperty? = null,
): LiteralProperty<EntityWithComplexProperty> {
    return value(EntityWithComplexProperty(
        property = optionalValue(property),
    ))
}

fun <T> TemplateContext<T>.complexProperty(): LiteralProperty<EntityWithComplexProperty.ComplexProperty> {
    return value(EntityWithComplexProperty.ComplexProperty(
        value = null,
    ))
}

fun <T> TemplateContext<T>.complexProperty(
    value: Property<URI>? = null,
): LiteralProperty<EntityWithComplexProperty.ComplexProperty> {
    return value(EntityWithComplexProperty.ComplexProperty(
        value = value,
    ))
}

fun <T> TemplateContext<T>.complexProperty(
    value: URI? = null,
): LiteralProperty<EntityWithComplexProperty.ComplexProperty> {
    return value(EntityWithComplexProperty.ComplexProperty(
        value = optionalValue(value),
    ))
}

fun CardContext.entityWithComplexProperty(
    property: ValueProperty<EntityWithComplexProperty.ComplexProperty>,
): EntityWithComplexProperty {
    return EntityWithComplexProperty(
        property = property,
    )
}

fun CardContext.entityWithComplexProperty(
    property: EntityWithComplexProperty.ComplexProperty,
): EntityWithComplexProperty {
    return EntityWithComplexProperty(
        property = value(property),
    )
}

fun CardContext.complexProperty(
    value: ValueProperty<URI>,
): EntityWithComplexProperty.ComplexProperty {
    return EntityWithComplexProperty.ComplexProperty(
        value = value,
    )
}

fun CardContext.complexProperty(
    value: URI,
): EntityWithComplexProperty.ComplexProperty {
    return EntityWithComplexProperty.ComplexProperty(
        value = value(value),
    )
}
