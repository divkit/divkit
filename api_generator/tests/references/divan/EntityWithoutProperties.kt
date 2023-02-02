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
 * Can be created using the method [`entityWithoutProperties`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithoutProperties internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_without_properties")
    )

    operator fun plus(additive: Properties): EntityWithoutProperties = EntityWithoutProperties(
        Properties(
        )
    )

    class Properties internal constructor(
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            return result
        }
    }
}

@Generated
fun DivScope.entityWithoutProperties(

): EntityWithoutProperties = EntityWithoutProperties(
    EntityWithoutProperties.Properties(
    )
)

@Generated
fun DivScope.entityWithoutPropertiesProps(

) = EntityWithoutProperties.Properties(
)

@Generated
fun TemplateScope.entityWithoutPropertiesRefs(
    `use named arguments`: Guard = Guard.instance,
) = EntityWithoutProperties.Properties(
)

@Generated
fun EntityWithoutProperties.override(

): EntityWithoutProperties = EntityWithoutProperties(
    EntityWithoutProperties.Properties(
    )
)

@Generated
fun Component<EntityWithoutProperties>.override(

): Component<EntityWithoutProperties> = Component(
    template = template,
    properties = EntityWithoutProperties.Properties(
    ).mergeWith(properties)
)

@Generated
fun EntityWithoutProperties.defer(
    `use named arguments`: Guard = Guard.instance,
): EntityWithoutProperties = EntityWithoutProperties(
    EntityWithoutProperties.Properties(
    )
)

@Generated
fun Component<EntityWithoutProperties>.defer(
    `use named arguments`: Guard = Guard.instance,
): Component<EntityWithoutProperties> = Component(
    template = template,
    properties = EntityWithoutProperties.Properties(
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithoutProperties>.plus(additive: Properties): Component<EntityWithoutProperties> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithoutProperties.asList() = listOf(this)
