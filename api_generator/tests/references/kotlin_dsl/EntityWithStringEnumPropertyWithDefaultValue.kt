// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithStringEnumPropertyWithDefaultValue internal constructor(
    @JsonIgnore val value: Property<Value>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_string_enum_property_with_default_value"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "value" to value,
        )
    }

    enum class Value(@JsonValue val value: String) {
        FIRST("first"),
        SECOND("second"),
        THIRD("third"),
    }
}

fun <T> TemplateContext<T>.entityWithStringEnumPropertyWithDefaultValue(): LiteralProperty<EntityWithStringEnumPropertyWithDefaultValue> {
    return value(EntityWithStringEnumPropertyWithDefaultValue(
        value = null,
    ))
}

fun <T> TemplateContext<T>.entityWithStringEnumPropertyWithDefaultValue(
    value: Property<EntityWithStringEnumPropertyWithDefaultValue.Value>? = null,
): LiteralProperty<EntityWithStringEnumPropertyWithDefaultValue> {
    return value(EntityWithStringEnumPropertyWithDefaultValue(
        value = value,
    ))
}

fun <T> TemplateContext<T>.entityWithStringEnumPropertyWithDefaultValue(
    value: EntityWithStringEnumPropertyWithDefaultValue.Value? = null,
): LiteralProperty<EntityWithStringEnumPropertyWithDefaultValue> {
    return value(EntityWithStringEnumPropertyWithDefaultValue(
        value = optionalValue(value),
    ))
}

fun CardContext.entityWithStringEnumPropertyWithDefaultValue(): EntityWithStringEnumPropertyWithDefaultValue {
    return EntityWithStringEnumPropertyWithDefaultValue(
        value = null,
    )
}

fun CardContext.entityWithStringEnumPropertyWithDefaultValue(
    value: ValueProperty<EntityWithStringEnumPropertyWithDefaultValue.Value>? = null,
): EntityWithStringEnumPropertyWithDefaultValue {
    return EntityWithStringEnumPropertyWithDefaultValue(
        value = value,
    )
}

fun CardContext.entityWithStringEnumPropertyWithDefaultValue(
    value: EntityWithStringEnumPropertyWithDefaultValue.Value? = null,
): EntityWithStringEnumPropertyWithDefaultValue {
    return EntityWithStringEnumPropertyWithDefaultValue(
        value = optionalValue(value),
    )
}
