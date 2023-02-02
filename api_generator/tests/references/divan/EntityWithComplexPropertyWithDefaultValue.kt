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
 * Can be created using the method [`entityWithComplexPropertyWithDefaultValue`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithComplexPropertyWithDefaultValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_complex_property_with_default_value")
    )

    operator fun plus(additive: Properties): EntityWithComplexPropertyWithDefaultValue = EntityWithComplexPropertyWithDefaultValue(
        Properties(
            property = additive.property ?: properties.property,
        )
    )

    class Properties internal constructor(
        /**
         * Default value: `{ "value": "Default text" }`.
         */
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
     * Can be created using the method [`entityWithComplexPropertyWithDefaultValueProperty`].
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
            val value: Property<String>?,
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
fun DivScope.entityWithComplexPropertyWithDefaultValue(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexPropertyWithDefaultValue.Property? = null,
): EntityWithComplexPropertyWithDefaultValue = EntityWithComplexPropertyWithDefaultValue(
    EntityWithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithComplexPropertyWithDefaultValueProps(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexPropertyWithDefaultValue.Property? = null,
) = EntityWithComplexPropertyWithDefaultValue.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithComplexPropertyWithDefaultValueRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithComplexPropertyWithDefaultValue.Property>? = null,
) = EntityWithComplexPropertyWithDefaultValue.Properties(
    property = property,
)

@Generated
fun EntityWithComplexPropertyWithDefaultValue.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexPropertyWithDefaultValue.Property? = null,
): EntityWithComplexPropertyWithDefaultValue = EntityWithComplexPropertyWithDefaultValue(
    EntityWithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithComplexPropertyWithDefaultValue>.override(
    `use named arguments`: Guard = Guard.instance,
    property: EntityWithComplexPropertyWithDefaultValue.Property? = null,
): Component<EntityWithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = EntityWithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithComplexPropertyWithDefaultValue.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithComplexPropertyWithDefaultValue.Property>? = null,
): EntityWithComplexPropertyWithDefaultValue = EntityWithComplexPropertyWithDefaultValue(
    EntityWithComplexPropertyWithDefaultValue.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithComplexPropertyWithDefaultValue>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<EntityWithComplexPropertyWithDefaultValue.Property>? = null,
): Component<EntityWithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = EntityWithComplexPropertyWithDefaultValue.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithComplexPropertyWithDefaultValue>.plus(additive: Properties): Component<EntityWithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithComplexPropertyWithDefaultValue.asList() = listOf(this)

@Generated
fun DivScope.entityWithComplexPropertyWithDefaultValueProperty(
    `use named arguments`: Guard = Guard.instance,
    value: String,
): EntityWithComplexPropertyWithDefaultValue.Property = EntityWithComplexPropertyWithDefaultValue.Property(
    EntityWithComplexPropertyWithDefaultValue.Property.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.entityWithComplexPropertyWithDefaultValuePropertyProps(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
) = EntityWithComplexPropertyWithDefaultValue.Property.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.entityWithComplexPropertyWithDefaultValuePropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
) = EntityWithComplexPropertyWithDefaultValue.Property.Properties(
    value = value,
)

@Generated
fun EntityWithComplexPropertyWithDefaultValue.Property.override(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): EntityWithComplexPropertyWithDefaultValue.Property = EntityWithComplexPropertyWithDefaultValue.Property(
    EntityWithComplexPropertyWithDefaultValue.Property.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun Component<EntityWithComplexPropertyWithDefaultValue.Property>.override(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): Component<EntityWithComplexPropertyWithDefaultValue.Property> = Component(
    template = template,
    properties = EntityWithComplexPropertyWithDefaultValue.Property.Properties(
        value = valueOrNull(value),
    ).mergeWith(properties)
)

@Generated
fun EntityWithComplexPropertyWithDefaultValue.Property.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
): EntityWithComplexPropertyWithDefaultValue.Property = EntityWithComplexPropertyWithDefaultValue.Property(
    EntityWithComplexPropertyWithDefaultValue.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<EntityWithComplexPropertyWithDefaultValue.Property>.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
): Component<EntityWithComplexPropertyWithDefaultValue.Property> = Component(
    template = template,
    properties = EntityWithComplexPropertyWithDefaultValue.Property.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
fun EntityWithComplexPropertyWithDefaultValue.Property.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): EntityWithComplexPropertyWithDefaultValue.Property = EntityWithComplexPropertyWithDefaultValue.Property(
    EntityWithComplexPropertyWithDefaultValue.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<EntityWithComplexPropertyWithDefaultValue.Property>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): Component<EntityWithComplexPropertyWithDefaultValue.Property> = Component(
    template = template,
    properties = EntityWithComplexPropertyWithDefaultValue.Property.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Property>.plus(additive: Properties): Component<Property> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithComplexPropertyWithDefaultValue.Property.asList() = listOf(this)
