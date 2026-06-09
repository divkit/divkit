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
        val property: Property<ComplexProperty>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("property", property)
            return result
        }
    }

    /**
     * Can be created using the method [withComplexPropertyWithDefaultValueComplexProperty].
     * 
     * Required parameters: `value`.
     */
    @Generated
    @ExposedCopyVisibility
    data class ComplexProperty internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): ComplexProperty = ComplexProperty(
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
    property: WithComplexPropertyWithDefaultValue.ComplexProperty? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withComplexPropertyWithDefaultValueProps(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexPropertyWithDefaultValue.ComplexProperty? = null,
) = WithComplexPropertyWithDefaultValue.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withComplexPropertyWithDefaultValueRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexPropertyWithDefaultValue.ComplexProperty>? = null,
) = WithComplexPropertyWithDefaultValue.Properties(
    property = property,
)

@Generated
fun WithComplexPropertyWithDefaultValue.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexPropertyWithDefaultValue.ComplexProperty? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexPropertyWithDefaultValue.ComplexProperty>? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<WithComplexPropertyWithDefaultValue.ComplexProperty>? = null,
): WithComplexPropertyWithDefaultValue = WithComplexPropertyWithDefaultValue(
    WithComplexPropertyWithDefaultValue.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithComplexPropertyWithDefaultValue>.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexPropertyWithDefaultValue.ComplexProperty? = null,
): Component<WithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithComplexPropertyWithDefaultValue.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithComplexPropertyWithDefaultValue>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexPropertyWithDefaultValue.ComplexProperty>? = null,
): Component<WithComplexPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithComplexPropertyWithDefaultValue.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun Component<WithComplexPropertyWithDefaultValue>.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<WithComplexPropertyWithDefaultValue.ComplexProperty>? = null,
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
fun DivScope.withComplexPropertyWithDefaultValueComplexProperty(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): WithComplexPropertyWithDefaultValue.ComplexProperty = WithComplexPropertyWithDefaultValue.ComplexProperty(
    WithComplexPropertyWithDefaultValue.ComplexProperty.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.withComplexPropertyWithDefaultValueComplexPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
) = WithComplexPropertyWithDefaultValue.ComplexProperty.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.withComplexPropertyWithDefaultValueComplexPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
) = WithComplexPropertyWithDefaultValue.ComplexProperty.Properties(
    value = value,
)

@Generated
fun WithComplexPropertyWithDefaultValue.ComplexProperty.override(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): WithComplexPropertyWithDefaultValue.ComplexProperty = WithComplexPropertyWithDefaultValue.ComplexProperty(
    WithComplexPropertyWithDefaultValue.ComplexProperty.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.ComplexProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
): WithComplexPropertyWithDefaultValue.ComplexProperty = WithComplexPropertyWithDefaultValue.ComplexProperty(
    WithComplexPropertyWithDefaultValue.ComplexProperty.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.ComplexProperty.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<String>? = null,
): WithComplexPropertyWithDefaultValue.ComplexProperty = WithComplexPropertyWithDefaultValue.ComplexProperty(
    WithComplexPropertyWithDefaultValue.ComplexProperty.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.ComplexProperty.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): WithComplexPropertyWithDefaultValue.ComplexProperty = WithComplexPropertyWithDefaultValue.ComplexProperty(
    WithComplexPropertyWithDefaultValue.ComplexProperty.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexPropertyWithDefaultValue.ComplexProperty.asList() = listOf(this)
