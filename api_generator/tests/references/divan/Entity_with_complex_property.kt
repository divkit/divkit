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
import java.net.URI
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

/**
 *
 * Можно создать при помощи метода [entity_with_complex_property].
 *
 * Обязательные поля: type, property
 */
@Generated
class Entity_with_complex_property internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_complex_property")
    )

    operator fun plus(additive: Properties): Entity_with_complex_property =
            Entity_with_complex_property(
    	Properties(
    		property = additive.property ?: properties.property,
    	)
    )

    class Properties internal constructor(
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
     * Можно создать при помощи метода [entity_with_complex_propertyComplexProperty].
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
            val value: Property<URI>?,
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
fun DivScope.entity_with_complex_propertyComplexProperty(`use named arguments`: Guard =
        Guard.instance, value: URI? = null): Entity_with_complex_property.ComplexProperty =
        Entity_with_complex_property.ComplexProperty(
	Entity_with_complex_property.ComplexProperty.Properties(
		value = valueOrNull(value),
	)
)

@Generated
fun DivScope.entity_with_complex_propertyComplexPropertyProps(`use named arguments`: Guard =
        Guard.instance, value: URI? = null) =
        Entity_with_complex_property.ComplexProperty.Properties(
	value = valueOrNull(value),
)

@Generated
fun TemplateScope.entity_with_complex_propertyComplexPropertyRefs(`use named
        arguments`: Guard = Guard.instance, value: ReferenceProperty<URI>? = null) =
        Entity_with_complex_property.ComplexProperty.Properties(
	value = value,
)

@Generated
fun Entity_with_complex_property.ComplexProperty.override(`use named arguments`: Guard =
        Guard.instance, value: URI? = null): Entity_with_complex_property.ComplexProperty =
        Entity_with_complex_property.ComplexProperty(
	Entity_with_complex_property.ComplexProperty.Properties(
		value = valueOrNull(value) ?: properties.value,
	)
)

@Generated
fun Entity_with_complex_property.ComplexProperty.defer(`use named arguments`: Guard =
        Guard.instance, value: ReferenceProperty<URI>? = null):
        Entity_with_complex_property.ComplexProperty = Entity_with_complex_property.ComplexProperty(
	Entity_with_complex_property.ComplexProperty.Properties(
		value = value ?: properties.value,
	)
)

@Generated
fun Entity_with_complex_property.ComplexProperty.evaluate(`use named arguments`: Guard =
        Guard.instance, value: ExpressionProperty<URI>? = null):
        Entity_with_complex_property.ComplexProperty = Entity_with_complex_property.ComplexProperty(
	Entity_with_complex_property.ComplexProperty.Properties(
		value = value ?: properties.value,
	)
)

@Generated
fun DivScope.entity_with_complex_property(`use named arguments`: Guard = Guard.instance,
        property: Entity_with_complex_property.ComplexProperty? = null):
        Entity_with_complex_property = Entity_with_complex_property(
	Entity_with_complex_property.Properties(
		property = valueOrNull(property),
	)
)

@Generated
fun DivScope.entity_with_complex_propertyProps(`use named arguments`: Guard = Guard.instance,
        property: Entity_with_complex_property.ComplexProperty? = null) =
        Entity_with_complex_property.Properties(
	property = valueOrNull(property),
)

@Generated
fun TemplateScope.entity_with_complex_propertyRefs(`use named arguments`: Guard =
        Guard.instance, property: ReferenceProperty<Entity_with_complex_property.ComplexProperty>?
        = null) = Entity_with_complex_property.Properties(
	property = property,
)

@Generated
fun Entity_with_complex_property.override(`use named arguments`: Guard = Guard.instance,
        property: Entity_with_complex_property.ComplexProperty? = null):
        Entity_with_complex_property = Entity_with_complex_property(
	Entity_with_complex_property.Properties(
		property = valueOrNull(property) ?: properties.property,
	)
)

@Generated
fun Entity_with_complex_property.defer(`use named arguments`: Guard = Guard.instance,
        property: ReferenceProperty<Entity_with_complex_property.ComplexProperty>? = null):
        Entity_with_complex_property = Entity_with_complex_property(
	Entity_with_complex_property.Properties(
		property = property ?: properties.property,
	)
)
