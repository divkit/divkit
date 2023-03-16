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
 * Can be created using the method [withRequiredProperty].
 * 
 * Required parameters: `type, property`.
 */
@Generated
class WithRequiredProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_required_property")
    )

    operator fun plus(additive: Properties): WithRequiredProperty = WithRequiredProperty(
        Properties(
            property = additive.property ?: properties.property,
        )
    )

    class Properties internal constructor(
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
fun DivScope.withRequiredProperty(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): WithRequiredProperty = WithRequiredProperty(
    WithRequiredProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withRequiredPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
) = WithRequiredProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withRequiredPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
) = WithRequiredProperty.Properties(
    property = property,
)

@Generated
fun WithRequiredProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): WithRequiredProperty = WithRequiredProperty(
    WithRequiredProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithRequiredProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): WithRequiredProperty = WithRequiredProperty(
    WithRequiredProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun WithRequiredProperty.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): WithRequiredProperty = WithRequiredProperty(
    WithRequiredProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<WithRequiredProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): Component<WithRequiredProperty> = Component(
    template = template,
    properties = WithRequiredProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun Component<WithRequiredProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): Component<WithRequiredProperty> = Component(
    template = template,
    properties = WithRequiredProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun Component<WithRequiredProperty>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): Component<WithRequiredProperty> = Component(
    template = template,
    properties = WithRequiredProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithRequiredProperty>.plus(additive: WithRequiredProperty.Properties): Component<WithRequiredProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithRequiredProperty.asList() = listOf(this)
