// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithoutProperties internal constructor(
) : Entity {

    @JsonProperty("type") override val type = "entity_without_properties"
}

fun <T> TemplateContext<T>.entityWithoutProperties(): LiteralProperty<EntityWithoutProperties> {
    return value(EntityWithoutProperties(
    ))
}

fun CardContext.entityWithoutProperties(): EntityWithoutProperties {
    return EntityWithoutProperties(
    )
}
