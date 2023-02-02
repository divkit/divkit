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
 * Can be created using the method [`entityWithOptionalComplexProperty`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithOptionalComplexProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_optional_complex_property")
    )

    operator fun plus(additive: Properties): EntityWithOptionalComplexProperty = EntityWithOptionalComplexProperty(
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
     * Can be created using the method [`entityWithOptionalComplexPropertyProperty`].
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
fun DivScope.entityWithOptionalComplexProperty(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalComplexProperty.Property? = null,
): EntityWithOptionalComplexProperty = EntityWithOptionalComplexProperty(
    EntityWithOptionalComplexProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithOptionalComplexPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalComplexProperty.Property? = null,
) = EntityWithOptionalComplexProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithOptionalComplexPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithOptionalComplexProperty.Property>? = null,
) = EntityWithOptionalComplexProperty.Properties(
    property = property,
)

@Generated
fun EntityWithOptionalComplexProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalComplexProperty.Property? = null,
): EntityWithOptionalComplexProperty = EntityWithOptionalComplexProperty(
    EntityWithOptionalComplexProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithOptionalComplexProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithOptionalComplexProperty.Property? = null,
): Component<EntityWithOptionalComplexProperty> = Component(
    template = template,
    properties = EntityWithOptionalComplexProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithOptionalComplexProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithOptionalComplexProperty.Property>? = null,
): EntityWithOptionalComplexProperty = EntityWithOptionalComplexProperty(
    EntityWithOptionalComplexProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithOptionalComplexProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithOptionalComplexProperty.Property>? = null,
): Component<EntityWithOptionalComplexProperty> = Component(
    template = template,
    properties = EntityWithOptionalComplexProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithOptionalComplexProperty>.plus(additive: Properties): Component<EntityWithOptionalComplexProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithOptionalComplexProperty.asList() = listOf(this)

@Generated
fun DivScope.entityWithOptionalComplexPropertyProperty(
    `use named arguments`: Guard = Guard.instance,
    value: URI,
): EntityWithOptionalComplexProperty.Property = EntityWithOptionalComplexProperty.Property(
    EntityWithOptionalComplexProperty.Property.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.entityWithOptionalComplexPropertyPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    value: URI? = null,
) = EntityWithOptionalComplexProperty.Property.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.entityWithOptionalComplexPropertyPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<URI>? = null,
) = EntityWithOptionalComplexProperty.Property.Properties(
    value = value,
)

@Generated
fun EntityWithOptionalComplexProperty.Property.override(
    `use named arguments`: Guard = Guard.instance,
    value: URI? = null,
): EntityWithOptionalComplexProperty.Property = EntityWithOptionalComplexProperty.Property(
    EntityWithOptionalComplexProperty.Property.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun Component<EntityWithOptionalComplexProperty.Property>.override(
    `use named arguments`: Guard = Guard.instance,
    value: URI? = null,
): Component<EntityWithOptionalComplexProperty.Property> = Component(
    template = template,
    properties = EntityWithOptionalComplexProperty.Property.Properties(
        value = valueOrNull(value),
    ).mergeWith(properties)
)

@Generated
fun EntityWithOptionalComplexProperty.Property.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<URI>? = null,
): EntityWithOptionalComplexProperty.Property = EntityWithOptionalComplexProperty.Property(
    EntityWithOptionalComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<EntityWithOptionalComplexProperty.Property>.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<URI>? = null,
): Component<EntityWithOptionalComplexProperty.Property> = Component(
    template = template,
    properties = EntityWithOptionalComplexProperty.Property.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
fun EntityWithOptionalComplexProperty.Property.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<URI>? = null,
): EntityWithOptionalComplexProperty.Property = EntityWithOptionalComplexProperty.Property(
    EntityWithOptionalComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<EntityWithOptionalComplexProperty.Property>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<URI>? = null,
): Component<EntityWithOptionalComplexProperty.Property> = Component(
    template = template,
    properties = EntityWithOptionalComplexProperty.Property.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Property>.plus(additive: Properties): Component<Property> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithOptionalComplexProperty.Property.asList() = listOf(this)
