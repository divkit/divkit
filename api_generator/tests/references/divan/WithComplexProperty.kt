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
 * Can be created using the method [withComplexProperty].
 * 
 * Required parameters: `type, property`.
 */
@Generated
class WithComplexProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_complex_property")
    )

    operator fun plus(additive: Properties): WithComplexProperty = WithComplexProperty(
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
     * Can be created using the method [withComplexPropertyProperty].
     * 
     * Required parameters: `value`.
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
fun DivScope.withComplexProperty(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexProperty.Property? = null,
): WithComplexProperty = WithComplexProperty(
    WithComplexProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withComplexPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexProperty.Property? = null,
) = WithComplexProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withComplexPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexProperty.Property>? = null,
) = WithComplexProperty.Properties(
    property = property,
)

@Generated
fun WithComplexProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexProperty.Property? = null,
): WithComplexProperty = WithComplexProperty(
    WithComplexProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithComplexProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexProperty.Property>? = null,
): WithComplexProperty = WithComplexProperty(
    WithComplexProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithComplexProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithComplexProperty.Property? = null,
): Component<WithComplexProperty> = Component(
    template = template,
    properties = WithComplexProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithComplexProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithComplexProperty.Property>? = null,
): Component<WithComplexProperty> = Component(
    template = template,
    properties = WithComplexProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithComplexProperty>.plus(additive: WithComplexProperty.Properties): Component<WithComplexProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithComplexProperty.asList() = listOf(this)

@Generated
fun DivScope.withComplexPropertyProperty(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): WithComplexProperty.Property = WithComplexProperty.Property(
    WithComplexProperty.Property.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.withComplexPropertyPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
) = WithComplexProperty.Property.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.withComplexPropertyPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
) = WithComplexProperty.Property.Properties(
    value = value,
)

@Generated
fun WithComplexProperty.Property.override(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): WithComplexProperty.Property = WithComplexProperty.Property(
    WithComplexProperty.Property.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun WithComplexProperty.Property.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
): WithComplexProperty.Property = WithComplexProperty.Property(
    WithComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexProperty.Property.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Url>? = null,
): WithComplexProperty.Property = WithComplexProperty.Property(
    WithComplexProperty.Property.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithComplexProperty.Property.asList() = listOf(this)
