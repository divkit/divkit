@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import divan.annotation.Generated
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
import kotlin.collections.List
import kotlin.collections.Map

/**
 *
 * Можно создать при помощи метода [entity_with_string_array_property].
 *
 * Обязательные поля: type, array
 */
@Generated
class Entity_with_string_array_property internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_string_array_property")
    )

    operator fun plus(additive: Properties): Entity_with_string_array_property =
            Entity_with_string_array_property(
    	Properties(
    		array = additive.array ?: properties.array,
    	)
    )

    class Properties internal constructor(
        val array: Property<List<String>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("array", array)
            return result
        }
    }
}

@Generated
fun DivScope.entity_with_string_array_property(`use named arguments`: Guard = Guard.instance,
        array: List<String>? = null): Entity_with_string_array_property =
        Entity_with_string_array_property(
	Entity_with_string_array_property.Properties(
		array = valueOrNull(array),
	)
)

@Generated
fun DivScope.entity_with_string_array_propertyProps(`use named arguments`: Guard =
        Guard.instance, array: List<String>? = null) = Entity_with_string_array_property.Properties(
	array = valueOrNull(array),
)

@Generated
fun TemplateScope.entity_with_string_array_propertyRefs(`use named arguments`: Guard =
        Guard.instance, array: ReferenceProperty<List<String>>? = null) =
        Entity_with_string_array_property.Properties(
	array = array,
)

@Generated
fun Entity_with_string_array_property.override(`use named arguments`: Guard =
        Guard.instance, array: List<String>? = null): Entity_with_string_array_property =
        Entity_with_string_array_property(
	Entity_with_string_array_property.Properties(
		array = valueOrNull(array) ?: properties.array,
	)
)

@Generated
fun Entity_with_string_array_property.defer(`use named arguments`: Guard = Guard.instance,
        array: ReferenceProperty<List<String>>? = null): Entity_with_string_array_property =
        Entity_with_string_array_property(
	Entity_with_string_array_property.Properties(
		array = array ?: properties.array,
	)
)
