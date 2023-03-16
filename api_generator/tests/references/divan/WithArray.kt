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
 * Can be created using the method [withArray].
 * 
 * Required parameters: `type, array`.
 */
@Generated
class WithArray internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array")
    )

    operator fun plus(additive: Properties): WithArray = WithArray(
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
fun DivScope.withArray(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): WithArray = WithArray(
    WithArray.Properties(
        array = valueOrNull(array),
    )
)

@Generated
fun DivScope.withArrayProps(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
) = WithArray.Properties(
    array = valueOrNull(array),
)

@Generated
fun TemplateScope.withArrayRefs(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
) = WithArray.Properties(
    array = array,
)

@Generated
fun WithArray.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): WithArray = WithArray(
    WithArray.Properties(
        array = valueOrNull(array) ?: properties.array,
    )
)

@Generated
fun WithArray.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): WithArray = WithArray(
    WithArray.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun Component<WithArray>.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): Component<WithArray> = Component(
    template = template,
    properties = WithArray.Properties(
        array = valueOrNull(array),
    ).mergeWith(properties)
)

@Generated
fun Component<WithArray>.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): Component<WithArray> = Component(
    template = template,
    properties = WithArray.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithArray>.plus(additive: WithArray.Properties): Component<WithArray> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithArray.asList() = listOf(this)
