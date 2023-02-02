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
 * Can be created using the method [`entityWithRequiredProperty`].
 * 
 * Required properties: `type, property`.
 */
@Generated
class EntityWithRequiredProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_required_property")
    )

    operator fun plus(additive: Properties): EntityWithRequiredProperty = EntityWithRequiredProperty(
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
fun DivScope.entityWithRequiredProperty(
    `use named arguments`: Guard = Guard.instance,
    property: String,
): EntityWithRequiredProperty = EntityWithRequiredProperty(
    EntityWithRequiredProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithRequiredPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
) = EntityWithRequiredProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithRequiredPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
) = EntityWithRequiredProperty.Properties(
    property = property,
)

@Generated
fun EntityWithRequiredProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): EntityWithRequiredProperty = EntityWithRequiredProperty(
    EntityWithRequiredProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithRequiredProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): Component<EntityWithRequiredProperty> = Component(
    template = template,
    properties = EntityWithRequiredProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithRequiredProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): EntityWithRequiredProperty = EntityWithRequiredProperty(
    EntityWithRequiredProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithRequiredProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): Component<EntityWithRequiredProperty> = Component(
    template = template,
    properties = EntityWithRequiredProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun EntityWithRequiredProperty.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): EntityWithRequiredProperty = EntityWithRequiredProperty(
    EntityWithRequiredProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithRequiredProperty>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): Component<EntityWithRequiredProperty> = Component(
    template = template,
    properties = EntityWithRequiredProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithRequiredProperty>.plus(additive: Properties): Component<EntityWithRequiredProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithRequiredProperty.asList() = listOf(this)
