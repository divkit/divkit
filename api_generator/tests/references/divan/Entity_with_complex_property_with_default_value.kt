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
 * Можно создать при помощи метода [entity_with_complex_property_with_default_value].
 *
 * Обязательные поля: type
 */
@Generated
class Entity_with_complex_property_with_default_value internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_complex_property_with_default_value")
    )

    operator fun plus(additive: Properties): Entity_with_complex_property_with_default_value
            = Entity_with_complex_property_with_default_value(
    	Properties(
    		property = additive.property ?: properties.property,
    	)
    )

    class Properties internal constructor(
        /**
         * Значение по умолчанию: { "value": "Default text" }
         */
        val property: Property<ComplexProperty>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("property", property)
            return result
        }
    }

    /**
     *
     * Можно создать при помощи метода
     * [entity_with_complex_property_with_default_valueComplexProperty].
     *
     * Обязательные поля: value
     */
    @Generated
    class ComplexProperty internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): ComplexProperty = ComplexProperty(
        	Properties(
        		value = additive.value ?: properties.value,
        	)
        )

        class Properties internal constructor(
            val value: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("value", value)
                return result
            }
        }
    }
}

@Generated
fun DivScope.entity_with_complex_property_with_default_valueComplexProperty(`use named
        arguments`: Guard = Guard.instance, value: String? = null):
        Entity_with_complex_property_with_default_value.ComplexProperty =
        Entity_with_complex_property_with_default_value.ComplexProperty(
	Entity_with_complex_property_with_default_value.ComplexProperty.Properties(
		value = valueOrNull(value),
	)
)

@Generated
fun DivScope.entity_with_complex_property_with_default_valueComplexPropertyProps(`use named
        arguments`: Guard = Guard.instance, value: String? = null) =
        Entity_with_complex_property_with_default_value.ComplexProperty.Properties(
	value = valueOrNull(value),
)

@Generated
fun TemplateScope.entity_with_complex_property_with_default_valueComplexPropertyRefs(`use
        named arguments`: Guard = Guard.instance, value: ReferenceProperty<String>? = null) =
        Entity_with_complex_property_with_default_value.ComplexProperty.Properties(
	value = value,
)

@Generated
fun Entity_with_complex_property_with_default_value.ComplexProperty.override(`use named
        arguments`: Guard = Guard.instance, value: String? = null):
        Entity_with_complex_property_with_default_value.ComplexProperty =
        Entity_with_complex_property_with_default_value.ComplexProperty(
	Entity_with_complex_property_with_default_value.ComplexProperty.Properties(
		value = valueOrNull(value) ?: properties.value,
	)
)

@Generated
fun Entity_with_complex_property_with_default_value.ComplexProperty.defer(`use named
        arguments`: Guard = Guard.instance, value: ReferenceProperty<String>? = null):
        Entity_with_complex_property_with_default_value.ComplexProperty =
        Entity_with_complex_property_with_default_value.ComplexProperty(
	Entity_with_complex_property_with_default_value.ComplexProperty.Properties(
		value = value ?: properties.value,
	)
)

@Generated
fun Entity_with_complex_property_with_default_value.ComplexProperty.evaluate(`use named
        arguments`: Guard = Guard.instance, value: ExpressionProperty<String>? = null):
        Entity_with_complex_property_with_default_value.ComplexProperty =
        Entity_with_complex_property_with_default_value.ComplexProperty(
	Entity_with_complex_property_with_default_value.ComplexProperty.Properties(
		value = value ?: properties.value,
	)
)

/**
 * @param property Значение по умолчанию: { "value": "Default text" }
 */
@Generated
fun DivScope.entity_with_complex_property_with_default_value(`use named arguments`: Guard =
        Guard.instance, property: Entity_with_complex_property_with_default_value.ComplexProperty?
        = null): Entity_with_complex_property_with_default_value =
        Entity_with_complex_property_with_default_value(
	Entity_with_complex_property_with_default_value.Properties(
		property = valueOrNull(property),
	)
)

/**
 * @param property Значение по умолчанию: { "value": "Default text" }
 */
@Generated
fun DivScope.entity_with_complex_property_with_default_valueProps(`use named
        arguments`: Guard = Guard.instance,
        property: Entity_with_complex_property_with_default_value.ComplexProperty? = null) =
        Entity_with_complex_property_with_default_value.Properties(
	property = valueOrNull(property),
)

/**
 * @param property Значение по умолчанию: { "value": "Default text" }
 */
@Generated
fun TemplateScope.entity_with_complex_property_with_default_valueRefs(`use named
        arguments`: Guard = Guard.instance,
        property: ReferenceProperty<Entity_with_complex_property_with_default_value.ComplexProperty>?
        = null) = Entity_with_complex_property_with_default_value.Properties(
	property = property,
)

/**
 * @param property Значение по умолчанию: { "value": "Default text" }
 */
@Generated
fun Entity_with_complex_property_with_default_value.override(`use named arguments`: Guard =
        Guard.instance, property: Entity_with_complex_property_with_default_value.ComplexProperty?
        = null): Entity_with_complex_property_with_default_value =
        Entity_with_complex_property_with_default_value(
	Entity_with_complex_property_with_default_value.Properties(
		property = valueOrNull(property) ?: properties.property,
	)
)

/**
 * @param property Значение по умолчанию: { "value": "Default text" }
 */
@Generated
fun Entity_with_complex_property_with_default_value.defer(`use named arguments`: Guard =
        Guard.instance,
        property: ReferenceProperty<Entity_with_complex_property_with_default_value.ComplexProperty>?
        = null): Entity_with_complex_property_with_default_value =
        Entity_with_complex_property_with_default_value(
	Entity_with_complex_property_with_default_value.Properties(
		property = property ?: properties.property,
	)
)
