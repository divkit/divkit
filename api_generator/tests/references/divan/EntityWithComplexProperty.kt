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
 * Can be created using the method [`entityWithComplexProperty`].
 * 
 * Required properties: `type, property`.
 */
@Generated
class EntityWithComplexProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_complex_property")
    )

    operator fun plus(additive: Properties): EntityWithComplexProperty = EntityWithComplexProperty(
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
     * Can be created using the method [`entityWithComplexPropertyProperty`].
     * 
     * Required properties: `value`.
     */
    @Generated
    class Property internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Property = Property(
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
fun DivScope.entityWithComplexProperty(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexProperty.Property,
): EntityWithComplexProperty = EntityWithComplexProperty(
    EntityWithComplexProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithComplexPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexProperty.Property? = null,
) = EntityWithComplexProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithComplexPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithComplexProperty.Property>? = null,
) = EntityWithComplexProperty.Properties(
    property = property,
)

@Generated
fun EntityWithComplexProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexProperty.Property? = null,
): EntityWithComplexProperty = EntityWithComplexProperty(
    EntityWithComplexProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithComplexProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexProperty.Property? = null,
): Component<EntityWithComplexProperty> = Component(
    template = template,
    properties = EntityWithComplexProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithComplexProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithComplexProperty.Property>? = null,
): EntityWithComplexProperty = EntityWithComplexProperty(
    EntityWithComplexProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithComplexProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithComplexProperty.Property>? = null,
): Component<EntityWithComplexProperty> = Component(
    template = template,
    properties = EntityWithComplexProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithComplexProperty>.plus(additive: Properties): Component<EntityWithComplexProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithComplexProperty.asList() = listOf(this)

@Generated
fun DivScope.entityWithComplexPropertyProperty(
    `use named arguments`: Guard = Guard.instance,
    value: URI,
): EntityWithComplexProperty.Property = EntityWithComplexProperty.Property(
    EntityWithComplexProperty.Property.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.entityWithComplexPropertyPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    value: URI? = null,
) = EntityWithComplexProperty.Property.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.entityWithComplexPropertyPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<URI>? = null,
) = EntityWithComplexProperty.Property.Properties(
    value = value,
)

@Generated
fun EntityWithComplexProperty.Property.override(
    `use named arguments`: Guard = Guard.instance,
    value: URI? = null,
): EntityWithComplexProperty.Property = EntityWithComplexProperty.Property(
    EntityWithComplexProperty.Property.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun Component<EntityWithComplexProperty.Property>.override(
    `use named arguments`: Guard = Guard.instance,
    value: URI? = null,
): Component<EntityWithComplexProperty.Property> = Component(
    template = template,
    properties = EntityWithComplexProperty.Property.Properties(
        value = valueOrNull(value),
    ).mergeWith(properties)
)

@Generated
fun EntityWithComplexProperty.Property.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<URI>? = null,
): EntityWithComplexProperty.Property = EntityWithComplexProperty.Property(
    EntityWithComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<EntityWithComplexProperty.Property>.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<URI>? = null,
): Component<EntityWithComplexProperty.Property> = Component(
    template = template,
    properties = EntityWithComplexProperty.Property.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
fun EntityWithComplexProperty.Property.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<URI>? = null,
): EntityWithComplexProperty.Property = EntityWithComplexProperty.Property(
    EntityWithComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<EntityWithComplexProperty.Property>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<URI>? = null,
): Component<EntityWithComplexProperty.Property> = Component(
    template = template,
    properties = EntityWithComplexProperty.Property.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Property>.plus(additive: Properties): Component<Property> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithComplexProperty.Property.asList() = listOf(this)
