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
 * Can be created using the method [`entityWithOptionalStringEnumProperty`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithOptionalStringEnumProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_optional_string_enum_property")
    )

    operator fun plus(additive: Properties): EntityWithOptionalStringEnumProperty = EntityWithOptionalStringEnumProperty(
        Properties(
            property = additive.property ?: properties.property,
        )
    )

    class Properties internal constructor(
        val property: Property<Property>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("property", property)
            return result
        }
    }

    /**
     * Possible values: [first, second].
     */
    @Generated
    sealed interface Property
}

@Generated
fun DivScope.entityWithOptionalStringEnumProperty(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalStringEnumProperty.Property? = null,
): EntityWithOptionalStringEnumProperty = EntityWithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithOptionalStringEnumPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalStringEnumProperty.Property? = null,
) = EntityWithOptionalStringEnumProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithOptionalStringEnumPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithOptionalStringEnumProperty.Property>? = null,
) = EntityWithOptionalStringEnumProperty.Properties(
    property = property,
)

@Generated
fun EntityWithOptionalStringEnumProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalStringEnumProperty.Property? = null,
): EntityWithOptionalStringEnumProperty = EntityWithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithOptionalStringEnumProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalStringEnumProperty.Property? = null,
): Component<EntityWithOptionalStringEnumProperty> = Component(
    template = template,
    properties = EntityWithOptionalStringEnumProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithOptionalStringEnumProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithOptionalStringEnumProperty.Property>? = null,
): EntityWithOptionalStringEnumProperty = EntityWithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithOptionalStringEnumProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithOptionalStringEnumProperty.Property>? = null,
): Component<EntityWithOptionalStringEnumProperty> = Component(
    template = template,
    properties = EntityWithOptionalStringEnumProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithOptionalStringEnumProperty>.plus(additive: Properties): Component<EntityWithOptionalStringEnumProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithOptionalStringEnumProperty.asList() = listOf(this)
