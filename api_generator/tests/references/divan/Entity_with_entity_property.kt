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
import kotlin.collections.Map

/**
 *
 * Можно создать при помощи метода [entity_with_entity_property].
 *
 * Обязательные поля: type
 */
@Generated
class Entity_with_entity_property internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_entity_property")
    )

    operator fun plus(additive: Properties): Entity_with_entity_property =
            Entity_with_entity_property(
    	Properties(
    		entity = additive.entity ?: properties.entity,
    	)
    )

    class Properties internal constructor(
        /**
         * Значение по умолчанию: { "type": "entity_with_string_enum_property", "property": "second"
         * }
         */
        val entity: Property<Entity>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("entity", entity)
            return result
        }
    }
}

/**
 * @param entity Значение по умолчанию: { "type": "entity_with_string_enum_property", "property":
 * "second" }
 */
@Generated
fun DivScope.entity_with_entity_property(`use named arguments`: Guard = Guard.instance,
        entity: Entity? = null): Entity_with_entity_property = Entity_with_entity_property(
	Entity_with_entity_property.Properties(
		entity = valueOrNull(entity),
	)
)

/**
 * @param entity Значение по умолчанию: { "type": "entity_with_string_enum_property", "property":
 * "second" }
 */
@Generated
fun DivScope.entity_with_entity_propertyProps(`use named arguments`: Guard = Guard.instance,
        entity: Entity? = null) = Entity_with_entity_property.Properties(
	entity = valueOrNull(entity),
)

/**
 * @param entity Значение по умолчанию: { "type": "entity_with_string_enum_property", "property":
 * "second" }
 */
@Generated
fun TemplateScope.entity_with_entity_propertyRefs(`use named arguments`: Guard =
        Guard.instance, entity: ReferenceProperty<Entity>? = null) =
        Entity_with_entity_property.Properties(
	entity = entity,
)

/**
 * @param entity Значение по умолчанию: { "type": "entity_with_string_enum_property", "property":
 * "second" }
 */
@Generated
fun Entity_with_entity_property.override(`use named arguments`: Guard = Guard.instance,
        entity: Entity? = null): Entity_with_entity_property = Entity_with_entity_property(
	Entity_with_entity_property.Properties(
		entity = valueOrNull(entity) ?: properties.entity,
	)
)

/**
 * @param entity Значение по умолчанию: { "type": "entity_with_string_enum_property", "property":
 * "second" }
 */
@Generated
fun Entity_with_entity_property.defer(`use named arguments`: Guard = Guard.instance,
        entity: ReferenceProperty<Entity>? = null): Entity_with_entity_property =
        Entity_with_entity_property(
	Entity_with_entity_property.Properties(
		entity = entity ?: properties.entity,
	)
)
