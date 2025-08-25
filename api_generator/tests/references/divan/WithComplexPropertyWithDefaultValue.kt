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
 * Can be created using the method [withComplexPropertyWithDefaultValue].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class WithComplexPropertyWithDefaultValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_complex_property_with_default_value")
    )

    operator fun plus(additive: Properties): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
        Properties(
            property = additive.property ?: properties.property,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
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
     * Can be created using the method [withComplexPropertyWithDefaultValueProperty].
     * 
     * Required parameters: `value`.
     */
    @Generated
    @ExposedCopyVisibility
    data class Property internal constructor(
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

        @ExposedCopyVisibility
        data class Properties internal constructor(
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
fun DivScope.withComplexPropertyWithDefaultValue(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexPropertyWithDefaultValue.Property? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withComplexPropertyWithDefaultValueProps(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexPropertyWithDefaultValue.Property? = null,
) = WithComplexPropertyWithDefaultValue.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withComplexPropertyWithDefaultValueRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexPropertyWithDefaultValue.Property>? = null,
) = WithComplexPropertyWithDefaultValue.Properties(
    property = property,
)

@Generated
fun WithComplexPropertyWithDefaultValue.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexPropertyWithDefaultValue.Property? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexPropertyWithDefaultValue.Property>? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<WithComplexPropertyWithDefaultValue.Property>? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithComplexPropertyWithDefaultValue>.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexPropertyWithDefaultValue.Property? = null,
): Component<WithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithComplexPropertyWithDefaultValue>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexPropertyWithDefaultValue.Property>? = null,
): Component<WithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithComplexPropertyWithDefaultValue.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun Component<WithComplexPropertyWithDefaultValue>.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<WithComplexPropertyWithDefaultValue.Property>? = null,
): Component<WithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithComplexPropertyWithDefaultValue.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithComplexPropertyWithDefaultValue>.plus(additive: WithComplexPropertyWithDefaultValue.Properties): Component<WithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithComplexPropertyWithDefaultValue.asList() = listOf(this)

@Generated
fun DivScope.withComplexPropertyWithDefaultValueProperty(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): WithComplexPropertyWithDefaultValue.Property = WithComplexPropertyWithDefaultValue.Property(
    WithComplexPropertyWithDefaultValue.Property.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.withComplexPropertyWithDefaultValuePropertyProps(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
) = WithComplexPropertyWithDefaultValue.Property.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.withComplexPropertyWithDefaultValuePropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
) = WithComplexPropertyWithDefaultValue.Property.Properties(
    value = value,
)

@Generated
fun WithComplexPropertyWithDefaultValue.Property.override(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): WithComplexPropertyWithDefaultValue.Property = WithComplexPropertyWithDefaultValue.Property(
    WithComplexPropertyWithDefaultValue.Property.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.Property.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
): WithComplexPropertyWithDefaultValue.Property = WithComplexPropertyWithDefaultValue.Property(
    WithComplexPropertyWithDefaultValue.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.Property.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<String>? = null,
): WithComplexPropertyWithDefaultValue.Property = WithComplexPropertyWithDefaultValue.Property(
    WithComplexPropertyWithDefaultValue.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.Property.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): WithComplexPropertyWithDefaultValue.Property = WithComplexPropertyWithDefaultValue.Property(
    WithComplexPropertyWithDefaultValue.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.Property.asList() = listOf(this)
