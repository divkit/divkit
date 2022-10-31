@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import divan.annotation.Generated
import divan.core.ExpressionProperty
import divan.core.Guard
import divan.core.Property
import divan.core.ReferenceProperty
import divan.core.tryPutProperty
import divan.core.valueOrNull
import divan.scope.DivScope
import divan.scope.TemplateScope
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

/**
 *
 * Можно создать при помощи метода [entity_with_string_enum_property_with_default_value].
 *
 * Обязательные поля: type
 */
@Generated
class Entity_with_string_enum_property_with_default_value internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_string_enum_property_with_default_value")
    )

    operator fun plus(additive: Properties):
            Entity_with_string_enum_property_with_default_value =
            Entity_with_string_enum_property_with_default_value(
    	Properties(
    		value = additive.value ?: properties.value,
    	)
    )

    class Properties internal constructor(
        /**
         * Допустимые значения: first, second, third
         * Значение по умолчанию: second
         */
        val value: Property<Value>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }

    /**
     * [first, second, third]
     */
    @Generated
    sealed interface Value
}

/**
 * @param value Допустимые значения: first, second, third
 * Значение по умолчанию: second
 */
@Generated
fun DivScope.entity_with_string_enum_property_with_default_value(`use named arguments`: Guard
        = Guard.instance, value: Entity_with_string_enum_property_with_default_value.Value? =
        null): Entity_with_string_enum_property_with_default_value =
        Entity_with_string_enum_property_with_default_value(
	Entity_with_string_enum_property_with_default_value.Properties(
		value = valueOrNull(value),
	)
)

/**
 * @param value Допустимые значения: first, second, third
 * Значение по умолчанию: second
 */
@Generated
fun DivScope.entity_with_string_enum_property_with_default_valueProps(`use named
        arguments`: Guard = Guard.instance,
        value: Entity_with_string_enum_property_with_default_value.Value? = null) =
        Entity_with_string_enum_property_with_default_value.Properties(
	value = valueOrNull(value),
)

/**
 * @param value Допустимые значения: first, second, third
 * Значение по умолчанию: second
 */
@Generated
fun TemplateScope.entity_with_string_enum_property_with_default_valueRefs(`use named
        arguments`: Guard = Guard.instance,
        value: ReferenceProperty<Entity_with_string_enum_property_with_default_value.Value>? =
        null) = Entity_with_string_enum_property_with_default_value.Properties(
	value = value,
)

/**
 * @param value Допустимые значения: first, second, third
 * Значение по умолчанию: second
 */
@Generated
fun Entity_with_string_enum_property_with_default_value.override(`use named
        arguments`: Guard = Guard.instance,
        value: Entity_with_string_enum_property_with_default_value.Value? = null):
        Entity_with_string_enum_property_with_default_value =
        Entity_with_string_enum_property_with_default_value(
	Entity_with_string_enum_property_with_default_value.Properties(
		value = valueOrNull(value) ?: properties.value,
	)
)

/**
 * @param value Допустимые значения: first, second, third
 * Значение по умолчанию: second
 */
@Generated
fun Entity_with_string_enum_property_with_default_value.defer(`use named arguments`: Guard =
        Guard.instance,
        value: ReferenceProperty<Entity_with_string_enum_property_with_default_value.Value>? =
        null): Entity_with_string_enum_property_with_default_value =
        Entity_with_string_enum_property_with_default_value(
	Entity_with_string_enum_property_with_default_value.Properties(
		value = value ?: properties.value,
	)
)

/**
 * @param value Допустимые значения: first, second, third
 * Значение по умолчанию: second
 */
@Generated
fun Entity_with_string_enum_property_with_default_value.evaluate(`use named arguments`: Guard
        = Guard.instance,
        value: ExpressionProperty<Entity_with_string_enum_property_with_default_value.Value>? =
        null): Entity_with_string_enum_property_with_default_value =
        Entity_with_string_enum_property_with_default_value(
	Entity_with_string_enum_property_with_default_value.Properties(
		value = value ?: properties.value,
	)
)
