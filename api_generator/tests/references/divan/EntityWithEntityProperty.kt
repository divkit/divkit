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
 * Can be created using the method [`entityWithEntityProperty`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithEntityProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_entity_property")
    )

    operator fun plus(additive: Properties): EntityWithEntityProperty = EntityWithEntityProperty(
        Properties(
            entity = additive.entity ?: properties.entity,
        )
    )

    class Properties internal constructor(
        /**
         * Default value: `{ "type": "entity_with_string_enum_property", "property": "second" }`.
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

@Generated
fun DivScope.entityWithEntityProperty(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
): EntityWithEntityProperty = EntityWithEntityProperty(
    EntityWithEntityProperty.Properties(
        entity = valueOrNull(entity),
    )
)

@Generated
fun DivScope.entityWithEntityPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
) = EntityWithEntityProperty.Properties(
    entity = valueOrNull(entity),
)

@Generated
fun TemplateScope.entityWithEntityPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
) = EntityWithEntityProperty.Properties(
    entity = entity,
)

@Generated
fun EntityWithEntityProperty.override(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
): EntityWithEntityProperty = EntityWithEntityProperty(
    EntityWithEntityProperty.Properties(
        entity = valueOrNull(entity) ?: properties.entity,
    )
)

@Generated
fun Component<EntityWithEntityProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
): Component<EntityWithEntityProperty> = Component(
    template = template,
    properties = EntityWithEntityProperty.Properties(
        entity = valueOrNull(entity),
    ).mergeWith(properties)
)

@Generated
fun EntityWithEntityProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
): EntityWithEntityProperty = EntityWithEntityProperty(
    EntityWithEntityProperty.Properties(
        entity = entity ?: properties.entity,
    )
)

@Generated
fun Component<EntityWithEntityProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
): Component<EntityWithEntityProperty> = Component(
    template = template,
    properties = EntityWithEntityProperty.Properties(
        entity = entity,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithEntityProperty>.plus(additive: Properties): Component<EntityWithEntityProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithEntityProperty.asList() = listOf(this)
