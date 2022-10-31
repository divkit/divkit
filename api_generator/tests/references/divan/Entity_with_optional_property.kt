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
 * Можно создать при помощи метода [entity_with_optional_property].
 *
 * Обязательные поля: type
 */
@Generated
class Entity_with_optional_property internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_optional_property")
    )

    operator fun plus(additive: Properties): Entity_with_optional_property =
            Entity_with_optional_property(
    	Properties(
    		property = additive.property ?: properties.property,
    	)
    )

    class Properties internal constructor(
        val property: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("property", property)
            return result
        }
    }
}

@Generated
fun DivScope.entity_with_optional_property(`use named arguments`: Guard = Guard.instance,
        property: String? = null): Entity_with_optional_property = Entity_with_optional_property(
	Entity_with_optional_property.Properties(
		property = valueOrNull(property),
	)
)

@Generated
fun DivScope.entity_with_optional_propertyProps(`use named arguments`: Guard =
        Guard.instance, property: String? = null) = Entity_with_optional_property.Properties(
	property = valueOrNull(property),
)

@Generated
fun TemplateScope.entity_with_optional_propertyRefs(`use named arguments`: Guard =
        Guard.instance, property: ReferenceProperty<String>? = null) =
        Entity_with_optional_property.Properties(
	property = property,
)

@Generated
fun Entity_with_optional_property.override(`use named arguments`: Guard = Guard.instance,
        property: String? = null): Entity_with_optional_property = Entity_with_optional_property(
	Entity_with_optional_property.Properties(
		property = valueOrNull(property) ?: properties.property,
	)
)

@Generated
fun Entity_with_optional_property.defer(`use named arguments`: Guard = Guard.instance,
        property: ReferenceProperty<String>? = null): Entity_with_optional_property =
        Entity_with_optional_property(
	Entity_with_optional_property.Properties(
		property = property ?: properties.property,
	)
)

@Generated
fun Entity_with_optional_property.evaluate(`use named arguments`: Guard = Guard.instance,
        property: ExpressionProperty<String>? = null): Entity_with_optional_property =
        Entity_with_optional_property(
	Entity_with_optional_property.Properties(
		property = property ?: properties.property,
	)
)
