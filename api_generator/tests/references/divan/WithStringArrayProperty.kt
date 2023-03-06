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
 * Can be created using the method [withStringArrayProperty].
 * 
 * Required properties: `type, array`.
 */
@Generated
class WithStringArrayProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_string_array_property")
    )

    operator fun plus(additive: Properties): WithStringArrayProperty = WithStringArrayProperty(
        Properties(
            array = additive.array ?: properties.array,
        )
    )

    class Properties internal constructor(
        val array: Property<List<String>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("array", array)
            return result
        }
    }
}

@Generated
fun DivScope.withStringArrayProperty(
    `use named arguments`: Guard = Guard.instance,
    array: List<String>? = null,
): WithStringArrayProperty = WithStringArrayProperty(
    WithStringArrayProperty.Properties(
        array = valueOrNull(array),
    )
)

@Generated
fun DivScope.withStringArrayPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    array: List<String>? = null,
) = WithStringArrayProperty.Properties(
    array = valueOrNull(array),
)

@Generated
fun TemplateScope.withStringArrayPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<String>>? = null,
) = WithStringArrayProperty.Properties(
    array = array,
)

@Generated
fun WithStringArrayProperty.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<String>? = null,
): WithStringArrayProperty = WithStringArrayProperty(
    WithStringArrayProperty.Properties(
        array = valueOrNull(array) ?: properties.array,
    )
)

@Generated
fun WithStringArrayProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<String>>? = null,
): WithStringArrayProperty = WithStringArrayProperty(
    WithStringArrayProperty.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun Component<WithStringArrayProperty>.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<String>? = null,
): Component<WithStringArrayProperty> = Component(
    template = template,
    properties = WithStringArrayProperty.Properties(
        array = valueOrNull(array),
    ).mergeWith(properties)
)

@Generated
fun Component<WithStringArrayProperty>.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<String>>? = null,
): Component<WithStringArrayProperty> = Component(
    template = template,
    properties = WithStringArrayProperty.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithStringArrayProperty>.plus(additive: WithStringArrayProperty.Properties): Component<WithStringArrayProperty> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithStringArrayProperty.asList() = listOf(this)
