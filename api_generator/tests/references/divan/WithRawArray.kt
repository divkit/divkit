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
 * Can be created using the method [withRawArray].
 * 
 * Required parameters: `type, array`.
 */
@Generated
class WithRawArray internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_raw_array")
    )

    operator fun plus(additive: Properties): WithRawArray = WithRawArray(
        Properties(
            array = additive.array ?: properties.array,
        )
    )

    class Properties internal constructor(
        val array: Property<List<Any>>?,
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
fun DivScope.withRawArray(
    `use named arguments`: Guard = Guard.instance,
    array: List<Any>? = null,
): WithRawArray = WithRawArray(
    WithRawArray.Properties(
        array = valueOrNull(array),
    )
)

@Generated
fun DivScope.withRawArrayProps(
    `use named arguments`: Guard = Guard.instance,
    array: List<Any>? = null,
) = WithRawArray.Properties(
    array = valueOrNull(array),
)

@Generated
fun TemplateScope.withRawArrayRefs(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Any>>? = null,
) = WithRawArray.Properties(
    array = array,
)

@Generated
fun WithRawArray.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Any>? = null,
): WithRawArray = WithRawArray(
    WithRawArray.Properties(
        array = valueOrNull(array) ?: properties.array,
    )
)

@Generated
fun WithRawArray.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Any>>? = null,
): WithRawArray = WithRawArray(
    WithRawArray.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun WithRawArray.evaluate(
    `use named arguments`: Guard = Guard.instance,
    array: ExpressionProperty<List<Any>>? = null,
): WithRawArray = WithRawArray(
    WithRawArray.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun Component<WithRawArray>.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Any>? = null,
): Component<WithRawArray> = Component(
    template = template,
    properties = WithRawArray.Properties(
        array = valueOrNull(array),
    ).mergeWith(properties)
)

@Generated
fun Component<WithRawArray>.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Any>>? = null,
): Component<WithRawArray> = Component(
    template = template,
    properties = WithRawArray.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
fun Component<WithRawArray>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    array: ExpressionProperty<List<Any>>? = null,
): Component<WithRawArray> = Component(
    template = template,
    properties = WithRawArray.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithRawArray>.plus(additive: WithRawArray.Properties): Component<WithRawArray> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithRawArray.asList() = listOf(this)
