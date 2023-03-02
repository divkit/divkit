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
 * Can be created using the method [withStringEnumProperty].
 * 
 * Required properties: `type, property`.
 */
@Generated
class WithStringEnumProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_string_enum_property")
    )

    operator fun plus(additive: Properties): WithStringEnumProperty = WithStringEnumProperty(
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

    fun Property.asList() = listOf(this)
}

@Generated
fun DivScope.withStringEnumProperty(
    `use named arguments`: Guard = Guard.instance,
    property: WithStringEnumProperty.Property,
): WithStringEnumProperty = WithStringEnumProperty(
    WithStringEnumProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withStringEnumPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: WithStringEnumProperty.Property? = null,
) = WithStringEnumProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withStringEnumPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithStringEnumProperty.Property>? = null,
) = WithStringEnumProperty.Properties(
    property = property,
)

@Generated
fun WithStringEnumProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithStringEnumProperty.Property? = null,
): WithStringEnumProperty = WithStringEnumProperty(
    WithStringEnumProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithStringEnumProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithStringEnumProperty.Property>? = null,
): WithStringEnumProperty = WithStringEnumProperty(
    WithStringEnumProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithStringEnumProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithStringEnumProperty.Property? = null,
): Component<WithStringEnumProperty> = Component(
    template = template,
    properties = WithStringEnumProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithStringEnumProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithStringEnumProperty.Property>? = null,
): Component<WithStringEnumProperty> = Component(
    template = template,
    properties = WithStringEnumProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithStringEnumProperty>.plus(additive: WithStringEnumProperty.Properties): Component<WithStringEnumProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithStringEnumProperty.asList() = listOf(this)
