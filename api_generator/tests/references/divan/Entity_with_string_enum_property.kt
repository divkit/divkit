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
 * Можно создать при помощи метода [entity_with_string_enum_property].
 *
 * Обязательные поля: type, property
 */
@Generated
class Entity_with_string_enum_property internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_string_enum_property")
    )

    operator fun plus(additive: Properties): Entity_with_string_enum_property =
            Entity_with_string_enum_property(
    	Properties(
    		property = additive.property ?: properties.property,
    	)
    )

    class Properties internal constructor(
        /**
         * Допустимые значения: first, second
         */
        val property: Property<StringEnumProperty>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("property", property)
            return result
        }
    }

    /**
     * [first, second]
     */
    @Generated
    sealed interface StringEnumProperty
}

/**
 * @param property Допустимые значения: first, second
 */
@Generated
fun DivScope.entity_with_string_enum_property(`use named arguments`: Guard = Guard.instance,
        property: Entity_with_string_enum_property.StringEnumProperty? = null):
        Entity_with_string_enum_property = Entity_with_string_enum_property(
	Entity_with_string_enum_property.Properties(
		property = valueOrNull(property),
	)
)

/**
 * @param property Допустимые значения: first, second
 */
@Generated
fun DivScope.entity_with_string_enum_propertyProps(`use named arguments`: Guard =
        Guard.instance, property: Entity_with_string_enum_property.StringEnumProperty? = null) =
        Entity_with_string_enum_property.Properties(
	property = valueOrNull(property),
)

/**
 * @param property Допустимые значения: first, second
 */
@Generated
fun TemplateScope.entity_with_string_enum_propertyRefs(`use named arguments`: Guard =
        Guard.instance,
        property: ReferenceProperty<Entity_with_string_enum_property.StringEnumProperty>? = null)
        = Entity_with_string_enum_property.Properties(
	property = property,
)

/**
 * @param property Допустимые значения: first, second
 */
@Generated
fun Entity_with_string_enum_property.override(`use named arguments`: Guard =
        Guard.instance, property: Entity_with_string_enum_property.StringEnumProperty? = null):
        Entity_with_string_enum_property = Entity_with_string_enum_property(
	Entity_with_string_enum_property.Properties(
		property = valueOrNull(property) ?: properties.property,
	)
)

/**
 * @param property Допустимые значения: first, second
 */
@Generated
fun Entity_with_string_enum_property.defer(`use named arguments`: Guard = Guard.instance,
        property: ReferenceProperty<Entity_with_string_enum_property.StringEnumProperty>? = null):
        Entity_with_string_enum_property = Entity_with_string_enum_property(
	Entity_with_string_enum_property.Properties(
		property = property ?: properties.property,
	)
)

/**
 * @param property Допустимые значения: first, second
 */
@Generated
fun Entity_with_string_enum_property.evaluate(`use named arguments`: Guard = Guard.instance,
        property: ExpressionProperty<Entity_with_string_enum_property.StringEnumProperty>? =
        null): Entity_with_string_enum_property = Entity_with_string_enum_property(
	Entity_with_string_enum_property.Properties(
		property = property ?: properties.property,
	)
)
