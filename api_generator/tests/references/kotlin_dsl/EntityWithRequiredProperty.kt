// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithRequiredProperty internal constructor(
    @JsonIgnore val property: Property<String>?,
) : Entity() {

    @JsonProperty("type") override val type = "entity_with_required_property"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "property" to property,
        )
    }
}

fun <T> TemplateContext<T>.entityWithRequiredProperty(): LiteralProperty<EntityWithRequiredProperty> {
    return value(EntityWithRequiredProperty(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithRequiredProperty(
    property: Property<String>? = null,
): LiteralProperty<EntityWithRequiredProperty> {
    return value(EntityWithRequiredProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithRequiredProperty(
    property: String? = null,
): LiteralProperty<EntityWithRequiredProperty> {
    return value(EntityWithRequiredProperty(
        property = optionalValue(property),
    ))
}

fun CardContext.entityWithRequiredProperty(
    property: ValueProperty<String>,
): EntityWithRequiredProperty {
    return EntityWithRequiredProperty(
        property = property,
    )
}

fun CardContext.entityWithRequiredProperty(
    property: String,
): EntityWithRequiredProperty {
    return EntityWithRequiredProperty(
        property = value(property),
    )
}
