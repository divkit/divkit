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
 * Can be created using the method [`entityWithStringEnumProperty`].
 * 
 * Required properties: `type, property`.
 */
@Generated
class EntityWithStringEnumProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_string_enum_property")
    )

    operator fun plus(additive: Properties): EntityWithStringEnumProperty = EntityWithStringEnumProperty(
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
fun DivScope.entityWithStringEnumProperty(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithStringEnumProperty.Property,
): EntityWithStringEnumProperty = EntityWithStringEnumProperty(
    EntityWithStringEnumProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithStringEnumPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithStringEnumProperty.Property? = null,
) = EntityWithStringEnumProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithStringEnumPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithStringEnumProperty.Property>? = null,
) = EntityWithStringEnumProperty.Properties(
    property = property,
)

@Generated
fun EntityWithStringEnumProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithStringEnumProperty.Property? = null,
): EntityWithStringEnumProperty = EntityWithStringEnumProperty(
    EntityWithStringEnumProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithStringEnumProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithStringEnumProperty.Property? = null,
): Component<EntityWithStringEnumProperty> = Component(
    template = template,
    properties = EntityWithStringEnumProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithStringEnumProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithStringEnumProperty.Property>? = null,
): EntityWithStringEnumProperty = EntityWithStringEnumProperty(
    EntityWithStringEnumProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithStringEnumProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithStringEnumProperty.Property>? = null,
): Component<EntityWithStringEnumProperty> = Component(
    template = template,
    properties = EntityWithStringEnumProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithStringEnumProperty>.plus(additive: Properties): Component<EntityWithStringEnumProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithStringEnumProperty.asList() = listOf(this)
