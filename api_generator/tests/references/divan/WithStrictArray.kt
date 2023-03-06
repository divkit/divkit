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
 * Can be created using the method [withStrictArray].
 * 
 * Required properties: `type, array`.
 */
@Generated
class WithStrictArray internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_strict_array")
    )

    operator fun plus(additive: Properties): WithStrictArray = WithStrictArray(
        Properties(
            array = additive.array ?: properties.array,
        )
    )

    class Properties internal constructor(
        val array: Property<List<Entity>>?,
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
fun DivScope.withStrictArray(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): WithStrictArray = WithStrictArray(
    WithStrictArray.Properties(
        array = valueOrNull(array),
    )
)

@Generated
fun DivScope.withStrictArrayProps(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
) = WithStrictArray.Properties(
    array = valueOrNull(array),
)

@Generated
fun TemplateScope.withStrictArrayRefs(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
) = WithStrictArray.Properties(
    array = array,
)

@Generated
fun WithStrictArray.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): WithStrictArray = WithStrictArray(
    WithStrictArray.Properties(
        array = valueOrNull(array) ?: properties.array,
    )
)

@Generated
fun WithStrictArray.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): WithStrictArray = WithStrictArray(
    WithStrictArray.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun Component<WithStrictArray>.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): Component<WithStrictArray> = Component(
    template = template,
    properties = WithStrictArray.Properties(
        array = valueOrNull(array),
    ).mergeWith(properties)
)

@Generated
fun Component<WithStrictArray>.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): Component<WithStrictArray> = Component(
    template = template,
    properties = WithStrictArray.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithStrictArray>.plus(additive: WithStrictArray.Properties): Component<WithStrictArray> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithStrictArray.asList() = listOf(this)
