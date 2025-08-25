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
 * Can be created using the method [withOptionalProperty].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class WithOptionalProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_optional_property")
    )

    operator fun plus(additive: Properties): WithOptionalProperty = WithOptionalProperty(
        Properties(
            property = additive.property ?: properties.property,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val property: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("property", property)
            return result
        }
    }
}

@Generated
fun DivScope.withOptionalProperty(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): WithOptionalProperty = WithOptionalProperty(
    WithOptionalProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withOptionalPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
) = WithOptionalProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withOptionalPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
) = WithOptionalProperty.Properties(
    property = property,
)

@Generated
fun WithOptionalProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): WithOptionalProperty = WithOptionalProperty(
    WithOptionalProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithOptionalProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): WithOptionalProperty = WithOptionalProperty(
    WithOptionalProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun WithOptionalProperty.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<String>? = null,
): WithOptionalProperty = WithOptionalProperty(
    WithOptionalProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun WithOptionalProperty.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): WithOptionalProperty = WithOptionalProperty(
    WithOptionalProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithOptionalProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): Component<WithOptionalProperty> = Component(
    template = template,
    properties = WithOptionalProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithOptionalProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): Component<WithOptionalProperty> = Component(
    template = template,
    properties = WithOptionalProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun Component<WithOptionalProperty>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): Component<WithOptionalProperty> = Component(
    template = template,
    properties = WithOptionalProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun Component<WithOptionalProperty>.modify(
    `use named arguments`: Guard = Guard.instance,
    property: Property<String>? = null,
): Component<WithOptionalProperty> = Component(
    template = template,
    properties = WithOptionalProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithOptionalProperty>.plus(additive: WithOptionalProperty.Properties): Component<WithOptionalProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithOptionalProperty.asList() = listOf(this)
