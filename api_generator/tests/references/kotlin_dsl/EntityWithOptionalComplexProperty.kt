// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithOptionalComplexProperty internal constructor(
    @JsonIgnore val property: Property<ComplexProperty>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_optional_complex_property"

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

fun <T> TemplateContext<T>.entityWithOptionalComplexProperty(): LiteralProperty<EntityWithOptionalComplexProperty> {
    return value(EntityWithOptionalComplexProperty(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalComplexProperty(
    property: Property<EntityWithOptionalComplexProperty.ComplexProperty>? = null,
): LiteralProperty<EntityWithOptionalComplexProperty> {
    return value(EntityWithOptionalComplexProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalComplexProperty(
    property: EntityWithOptionalComplexProperty.ComplexProperty? = null,
): LiteralProperty<EntityWithOptionalComplexProperty> {
    return value(EntityWithOptionalComplexProperty(
        property = optionalValue(property),
    ))
}

fun <T> TemplateContext<T>.complexProperty(): LiteralProperty<EntityWithOptionalComplexProperty.ComplexProperty> {
    return value(EntityWithOptionalComplexProperty.ComplexProperty(
        value = null,
    ))
}

fun <T> TemplateContext<T>.complexProperty(
    value: Property<URI>? = null,
): LiteralProperty<EntityWithOptionalComplexProperty.ComplexProperty> {
    return value(EntityWithOptionalComplexProperty.ComplexProperty(
        value = value,
    ))
}

fun <T> TemplateContext<T>.complexProperty(
    value: URI? = null,
): LiteralProperty<EntityWithOptionalComplexProperty.ComplexProperty> {
    return value(EntityWithOptionalComplexProperty.ComplexProperty(
        value = optionalValue(value),
    ))
}

fun CardContext.entityWithOptionalComplexProperty(): EntityWithOptionalComplexProperty {
    return EntityWithOptionalComplexProperty(
        property = null,
    )
}

fun CardContext.entityWithOptionalComplexProperty(
    property: ValueProperty<EntityWithOptionalComplexProperty.ComplexProperty>? = null,
): EntityWithOptionalComplexProperty {
    return EntityWithOptionalComplexProperty(
        property = property,
    )
}

fun CardContext.entityWithOptionalComplexProperty(
    property: EntityWithOptionalComplexProperty.ComplexProperty? = null,
): EntityWithOptionalComplexProperty {
    return EntityWithOptionalComplexProperty(
        property = optionalValue(property),
    )
}

fun CardContext.complexProperty(
    value: ValueProperty<URI>,
): EntityWithOptionalComplexProperty.ComplexProperty {
    return EntityWithOptionalComplexProperty.ComplexProperty(
        value = value,
    )
}

fun CardContext.complexProperty(
    value: URI,
): EntityWithOptionalComplexProperty.ComplexProperty {
    return EntityWithOptionalComplexProperty.ComplexProperty(
        value = value(value),
    )
}
