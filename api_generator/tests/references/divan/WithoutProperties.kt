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
 * Can be created using the method [withoutProperties].
 * 
 * Required properties: `type`.
 */
@Generated
class WithoutProperties internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_without_properties")
    )

    operator fun plus(additive: Properties): WithoutProperties = WithoutProperties(
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
fun DivScope.withoutProperties(

): WithoutProperties = WithoutProperties(
    WithoutProperties.Properties(
    )
)

@Generated
fun DivScope.withoutPropertiesProps(

) = WithoutProperties.Properties(
)

@Generated
fun TemplateScope.withoutPropertiesRefs(
    `use named arguments`: Guard = Guard.instance,
) = WithoutProperties.Properties(
)

@Generated
fun WithoutProperties.override(

): WithoutProperties = WithoutProperties(
    WithoutProperties.Properties(
    )
)

@Generated
fun WithoutProperties.defer(
    `use named arguments`: Guard = Guard.instance,
): WithoutProperties = WithoutProperties(
    WithoutProperties.Properties(
    )
)

@Generated
fun Component<WithoutProperties>.override(

): Component<WithoutProperties> = Component(
    template = template,
    properties = WithoutProperties.Properties(
    ).mergeWith(properties)
)

@Generated
fun Component<WithoutProperties>.defer(
    `use named arguments`: Guard = Guard.instance,
): Component<WithoutProperties> = Component(
    template = template,
    properties = WithoutProperties.Properties(
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithoutProperties>.plus(additive: WithoutProperties.Properties): Component<WithoutProperties> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithoutProperties.asList() = listOf(this)
