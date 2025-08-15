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
 * Can be created using the method [withOptionalComplexProperty].
 * 
 * Required parameters: `type`.
 */
@Generated
data class WithOptionalComplexProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_optional_complex_property")
    )

    operator fun plus(additive: Properties): WithOptionalComplexProperty = WithOptionalComplexProperty(
        Properties(
            property = additive.property ?: properties.property,
        )
    )

    data class Properties internal constructor(
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
     * Can be created using the method [withOptionalComplexPropertyProperty].
     * 
     * Required parameters: `value`.
     */
    @Generated
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

        data class Properties internal constructor(
            val value: Property<Url>?,
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
fun DivScope.withOptionalComplexProperty(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalComplexProperty.Property? = null,
): WithOptionalComplexProperty = WithOptionalComplexProperty(
    WithOptionalComplexProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withOptionalComplexPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalComplexProperty.Property? = null,
) = WithOptionalComplexProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withOptionalComplexPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithOptionalComplexProperty.Property>? = null,
) = WithOptionalComplexProperty.Properties(
    property = property,
)

@Generated
fun WithOptionalComplexProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalComplexProperty.Property? = null,
): WithOptionalComplexProperty = WithOptionalComplexProperty(
    WithOptionalComplexProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithOptionalComplexProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithOptionalComplexProperty.Property>? = null,
): WithOptionalComplexProperty = WithOptionalComplexProperty(
    WithOptionalComplexProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun WithOptionalComplexProperty.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<WithOptionalComplexProperty.Property>? = null,
): WithOptionalComplexProperty = WithOptionalComplexProperty(
    WithOptionalComplexProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithOptionalComplexProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalComplexProperty.Property? = null,
): Component<WithOptionalComplexProperty> = Component(
    template = template,
    properties = WithOptionalComplexProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithOptionalComplexProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithOptionalComplexProperty.Property>? = null,
): Component<WithOptionalComplexProperty> = Component(
    template = template,
    properties = WithOptionalComplexProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun Component<WithOptionalComplexProperty>.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<WithOptionalComplexProperty.Property>? = null,
): Component<WithOptionalComplexProperty> = Component(
    template = template,
    properties = WithOptionalComplexProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithOptionalComplexProperty>.plus(additive: WithOptionalComplexProperty.Properties): Component<WithOptionalComplexProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithOptionalComplexProperty.asList() = listOf(this)

@Generated
fun DivScope.withOptionalComplexPropertyProperty(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): WithOptionalComplexProperty.Property = WithOptionalComplexProperty.Property(
    WithOptionalComplexProperty.Property.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.withOptionalComplexPropertyPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
) = WithOptionalComplexProperty.Property.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.withOptionalComplexPropertyPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
) = WithOptionalComplexProperty.Property.Properties(
    value = value,
)

@Generated
fun WithOptionalComplexProperty.Property.override(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): WithOptionalComplexProperty.Property = WithOptionalComplexProperty.Property(
    WithOptionalComplexProperty.Property.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun WithOptionalComplexProperty.Property.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
): WithOptionalComplexProperty.Property = WithOptionalComplexProperty.Property(
    WithOptionalComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithOptionalComplexProperty.Property.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Url>? = null,
): WithOptionalComplexProperty.Property = WithOptionalComplexProperty.Property(
    WithOptionalComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithOptionalComplexProperty.Property.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Url>? = null,
): WithOptionalComplexProperty.Property = WithOptionalComplexProperty.Property(
    WithOptionalComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithOptionalComplexProperty.Property.asList() = listOf(this)
