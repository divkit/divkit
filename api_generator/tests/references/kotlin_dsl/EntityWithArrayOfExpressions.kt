// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithArrayOfExpressions internal constructor(
    @JsonIgnore val items: Property<List<String>>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_array_of_expressions"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "items" to items,
        )
    }
}

fun <T> TemplateContext<T>.entityWithArrayOfExpressions(): LiteralProperty<EntityWithArrayOfExpressions> {
    return value(EntityWithArrayOfExpressions(
        items = null,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayOfExpressions(
    items: Property<List<String>>? = null,
): LiteralProperty<EntityWithArrayOfExpressions> {
    return value(EntityWithArrayOfExpressions(
        items = items,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayOfExpressions(
    items: List<String>? = null,
): LiteralProperty<EntityWithArrayOfExpressions> {
    return value(EntityWithArrayOfExpressions(
        items = optionalValue(items),
    ))
}

fun CardContext.entityWithArrayOfExpressions(
    items: ValueProperty<List<String>>,
): EntityWithArrayOfExpressions {
    return EntityWithArrayOfExpressions(
        items = items,
    )
}

fun CardContext.entityWithArrayOfExpressions(
    items: List<String>,
): EntityWithArrayOfExpressions {
    return EntityWithArrayOfExpressions(
        items = value(items),
    )
}
