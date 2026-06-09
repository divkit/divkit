// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithComplexPropertyWithDefaultValue internal constructor(
    @JsonIgnore val property: Property<ComplexProperty>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_complex_property_with_default_value"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "property" to property,
        )
    }

    class ComplexProperty internal constructor(
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
    property: Property<EntityWithComplexPropertyWithDefaultValue.ComplexProperty>? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue> {
    return value(EntityWithComplexPropertyWithDefaultValue(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithComplexPropertyWithDefaultValue(
    property: EntityWithComplexPropertyWithDefaultValue.ComplexProperty? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue> {
    return value(EntityWithComplexPropertyWithDefaultValue(
        property = optionalValue(property),
    ))
}

fun <T> TemplateContext<T>.complexProperty(): LiteralProperty<EntityWithComplexPropertyWithDefaultValue.ComplexProperty> {
    return value(EntityWithComplexPropertyWithDefaultValue.ComplexProperty(
        value = null,
    ))
}

fun <T> TemplateContext<T>.complexProperty(
    value: Property<String>? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue.ComplexProperty> {
    return value(EntityWithComplexPropertyWithDefaultValue.ComplexProperty(
        value = value,
    ))
}

fun <T> TemplateContext<T>.complexProperty(
    value: String? = null,
): LiteralProperty<EntityWithComplexPropertyWithDefaultValue.ComplexProperty> {
    return value(EntityWithComplexPropertyWithDefaultValue.ComplexProperty(
        value = optionalValue(value),
    ))
}

fun CardContext.entityWithComplexPropertyWithDefaultValue(): EntityWithComplexPropertyWithDefaultValue {
    return EntityWithComplexPropertyWithDefaultValue(
        property = null,
    )
}

fun CardContext.entityWithComplexPropertyWithDefaultValue(
    property: ValueProperty<EntityWithComplexPropertyWithDefaultValue.ComplexProperty>? = null,
): EntityWithComplexPropertyWithDefaultValue {
    return EntityWithComplexPropertyWithDefaultValue(
        property = property,
    )
}

fun CardContext.entityWithComplexPropertyWithDefaultValue(
    property: EntityWithComplexPropertyWithDefaultValue.ComplexProperty? = null,
): EntityWithComplexPropertyWithDefaultValue {
    return EntityWithComplexPropertyWithDefaultValue(
        property = optionalValue(property),
    )
}

fun CardContext.complexProperty(
    value: ValueProperty<String>,
): EntityWithComplexPropertyWithDefaultValue.ComplexProperty {
    return EntityWithComplexPropertyWithDefaultValue.ComplexProperty(
        value = value,
    )
}

fun CardContext.complexProperty(
    value: String,
): EntityWithComplexPropertyWithDefaultValue.ComplexProperty {
    return EntityWithComplexPropertyWithDefaultValue.ComplexProperty(
        value = value(value),
    )
}
