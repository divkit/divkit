// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithJsonProperty internal constructor(
    @JsonIgnore val jsonProperty: Property<Map<String, Any>>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "json_property" to jsonProperty,
        )
    }
}

fun <T> TemplateContext<T>.entityWithJsonProperty(): LiteralProperty<EntityWithJsonProperty> {
    return value(EntityWithJsonProperty(
        jsonProperty = null,
    ))
}

fun <T> TemplateContext<T>.entityWithJsonProperty(
    jsonProperty: Property<Map<String, Any>>? = null,
): LiteralProperty<EntityWithJsonProperty> {
    return value(EntityWithJsonProperty(
        jsonProperty = jsonProperty,
    ))
}

fun <T> TemplateContext<T>.entityWithJsonProperty(
    jsonProperty: Map<String, Any>? = null,
): LiteralProperty<EntityWithJsonProperty> {
    return value(EntityWithJsonProperty(
        jsonProperty = optionalValue(jsonProperty),
    ))
}

fun CardContext.entityWithJsonProperty(): EntityWithJsonProperty {
    return EntityWithJsonProperty(
        jsonProperty = null,
    )
}

fun CardContext.entityWithJsonProperty(
    jsonProperty: ValueProperty<Map<String, Any>>? = null,
): EntityWithJsonProperty {
    return EntityWithJsonProperty(
        jsonProperty = jsonProperty,
    )
}

fun CardContext.entityWithJsonProperty(
    jsonProperty: Map<String, Any>? = null,
): EntityWithJsonProperty {
    return EntityWithJsonProperty(
        jsonProperty = optionalValue(jsonProperty),
    )
}
