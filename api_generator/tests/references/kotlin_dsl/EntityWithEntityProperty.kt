// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithEntityProperty internal constructor(
    @JsonIgnore val entity: Property<Entity>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_entity_property"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "entity" to entity,
        )
    }
}

fun <T> TemplateContext<T>.entityWithEntityProperty(): LiteralProperty<EntityWithEntityProperty> {
    return value(EntityWithEntityProperty(
        entity = null,
    ))
}

fun <T> TemplateContext<T>.entityWithEntityProperty(
    entity: Property<Entity>? = null,
): LiteralProperty<EntityWithEntityProperty> {
    return value(EntityWithEntityProperty(
        entity = entity,
    ))
}

fun <T> TemplateContext<T>.entityWithEntityProperty(
    entity: Entity? = null,
): LiteralProperty<EntityWithEntityProperty> {
    return value(EntityWithEntityProperty(
        entity = optionalValue(entity),
    ))
}

fun CardContext.entityWithEntityProperty(): EntityWithEntityProperty {
    return EntityWithEntityProperty(
        entity = null,
    )
}

fun CardContext.entityWithEntityProperty(
    entity: ValueProperty<Entity>? = null,
): EntityWithEntityProperty {
    return EntityWithEntityProperty(
        entity = entity,
    )
}

fun CardContext.entityWithEntityProperty(
    entity: Entity? = null,
): EntityWithEntityProperty {
    return EntityWithEntityProperty(
        entity = optionalValue(entity),
    )
}
