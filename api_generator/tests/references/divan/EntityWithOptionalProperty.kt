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
 * Can be created using the method [`entityWithOptionalProperty`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithOptionalProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_optional_property")
    )

    operator fun plus(additive: Properties): EntityWithOptionalProperty = EntityWithOptionalProperty(
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
fun DivScope.entityWithOptionalProperty(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): EntityWithOptionalProperty = EntityWithOptionalProperty(
    EntityWithOptionalProperty.Properties(
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithOptionalPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
) = EntityWithOptionalProperty.Properties(
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithOptionalPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
) = EntityWithOptionalProperty.Properties(
    property = property,
)

@Generated
fun EntityWithOptionalProperty.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): EntityWithOptionalProperty = EntityWithOptionalProperty(
    EntityWithOptionalProperty.Properties(
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithOptionalProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
): Component<EntityWithOptionalProperty> = Component(
    template = template,
    properties = EntityWithOptionalProperty.Properties(
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithOptionalProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): EntityWithOptionalProperty = EntityWithOptionalProperty(
    EntityWithOptionalProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithOptionalProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
): Component<EntityWithOptionalProperty> = Component(
    template = template,
    properties = EntityWithOptionalProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
fun EntityWithOptionalProperty.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): EntityWithOptionalProperty = EntityWithOptionalProperty(
    EntityWithOptionalProperty.Properties(
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithOptionalProperty>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): Component<EntityWithOptionalProperty> = Component(
    template = template,
    properties = EntityWithOptionalProperty.Properties(
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithOptionalProperty>.plus(additive: Properties): Component<EntityWithOptionalProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithOptionalProperty.asList() = listOf(this)
