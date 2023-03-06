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
 * Can be created using the method [withOptionalStringEnumProperty].
 * 
 * Required properties: `type`.
 */
@Generated
class WithOptionalStringEnumProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_optional_string_enum_property")
    )

    operator fun plus(additive: Properties): WithOptionalStringEnumProperty = WithOptionalStringEnumProperty(
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
fun DivScope.withOptionalStringEnumProperty(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalStringEnumProperty.Property? = null,
): WithOptionalStringEnumProperty = WithOptionalStringEnumProperty(
    WithOptionalStringEnumProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withOptionalStringEnumPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalStringEnumProperty.Property? = null,
) = WithOptionalStringEnumProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withOptionalStringEnumPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithOptionalStringEnumProperty.Property>? = null,
) = WithOptionalStringEnumProperty.Properties(
    property = property,
)

@Generated
fun WithOptionalStringEnumProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalStringEnumProperty.Property? = null,
): WithOptionalStringEnumProperty = WithOptionalStringEnumProperty(
    WithOptionalStringEnumProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithOptionalStringEnumProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithOptionalStringEnumProperty.Property>? = null,
): WithOptionalStringEnumProperty = WithOptionalStringEnumProperty(
    WithOptionalStringEnumProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun WithOptionalStringEnumProperty.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<WithOptionalStringEnumProperty.Property>? = null,
): WithOptionalStringEnumProperty = WithOptionalStringEnumProperty(
    WithOptionalStringEnumProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithOptionalStringEnumProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: WithOptionalStringEnumProperty.Property? = null,
): Component<WithOptionalStringEnumProperty> = Component(
    template = template,
    properties = WithOptionalStringEnumProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithOptionalStringEnumProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<WithOptionalStringEnumProperty.Property>? = null,
): Component<WithOptionalStringEnumProperty> = Component(
    template = template,
    properties = WithOptionalStringEnumProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun Component<WithOptionalStringEnumProperty>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<WithOptionalStringEnumProperty.Property>? = null,
): Component<WithOptionalStringEnumProperty> = Component(
    template = template,
    properties = WithOptionalStringEnumProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithOptionalStringEnumProperty>.plus(additive: WithOptionalStringEnumProperty.Properties): Component<WithOptionalStringEnumProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithOptionalStringEnumProperty.asList() = listOf(this)

@Generated
fun WithOptionalStringEnumProperty.Property.asList() = listOf(this)
