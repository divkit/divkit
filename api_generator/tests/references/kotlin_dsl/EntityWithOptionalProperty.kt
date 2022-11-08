// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithOptionalProperty internal constructor(
    @JsonIgnore val property: Property<String>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_optional_property"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "property" to property,
        )
    }
}

fun <T> TemplateContext<T>.entityWithOptionalProperty(): LiteralProperty<EntityWithOptionalProperty> {
    return value(EntityWithOptionalProperty(
        property = null,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalProperty(
    property: Property<String>? = null,
): LiteralProperty<EntityWithOptionalProperty> {
    return value(EntityWithOptionalProperty(
        property = property,
    ))
}

fun <T> TemplateContext<T>.entityWithOptionalProperty(
    property: String? = null,
): LiteralProperty<EntityWithOptionalProperty> {
    return value(EntityWithOptionalProperty(
        property = optionalValue(property),
    ))
}

fun CardContext.entityWithOptionalProperty(): EntityWithOptionalProperty {
    return EntityWithOptionalProperty(
        property = null,
    )
}

fun CardContext.entityWithOptionalProperty(
    property: ValueProperty<String>? = null,
): EntityWithOptionalProperty {
    return EntityWithOptionalProperty(
        property = property,
    )
}

fun CardContext.entityWithOptionalProperty(
    property: String? = null,
): EntityWithOptionalProperty {
    return EntityWithOptionalProperty(
        property = optionalValue(property),
    )
}
