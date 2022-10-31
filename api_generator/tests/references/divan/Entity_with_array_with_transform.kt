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
 * Можно создать при помощи метода [entity_with_array_with_transform].
 *
 * Обязательные поля: type, array
 */
@Generated
class Entity_with_array_with_transform internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_array_with_transform")
    )

    operator fun plus(additive: Properties): Entity_with_array_with_transform =
            Entity_with_array_with_transform(
    	Properties(
    		array = additive.array ?: properties.array,
    	)
    )

    class Properties internal constructor(
        val array: Property<List<Color>>?,
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
fun DivScope.entity_with_array_with_transform(`use named arguments`: Guard = Guard.instance,
        array: List<Color>? = null): Entity_with_array_with_transform =
        Entity_with_array_with_transform(
	Entity_with_array_with_transform.Properties(
		array = valueOrNull(array),
	)
)

@Generated
fun DivScope.entity_with_array_with_transformProps(`use named arguments`: Guard =
        Guard.instance, array: List<Color>? = null) = Entity_with_array_with_transform.Properties(
	array = valueOrNull(array),
)

@Generated
fun TemplateScope.entity_with_array_with_transformRefs(`use named arguments`: Guard =
        Guard.instance, array: ReferenceProperty<List<Color>>? = null) =
        Entity_with_array_with_transform.Properties(
	array = array,
)

@Generated
fun Entity_with_array_with_transform.override(`use named arguments`: Guard =
        Guard.instance, array: List<Color>? = null): Entity_with_array_with_transform =
        Entity_with_array_with_transform(
	Entity_with_array_with_transform.Properties(
		array = valueOrNull(array) ?: properties.array,
	)
)

@Generated
fun Entity_with_array_with_transform.defer(`use named arguments`: Guard = Guard.instance,
        array: ReferenceProperty<List<Color>>? = null): Entity_with_array_with_transform =
        Entity_with_array_with_transform(
	Entity_with_array_with_transform.Properties(
		array = array ?: properties.array,
	)
)
