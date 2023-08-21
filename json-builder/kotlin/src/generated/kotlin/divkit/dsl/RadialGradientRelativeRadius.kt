@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Relative radius of the gradient transition.
 * 
 * Can be created using the method [radialGradientRelativeRadius].
 * 
 * Required parameters: `value, type`.
 */
@Generated
class RadialGradientRelativeRadius internal constructor(
    @JsonIgnore
    val properties: Properties,
) : RadialGradientRadius {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "relative")
    )

    operator fun plus(additive: Properties): RadialGradientRelativeRadius = RadialGradientRelativeRadius(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    class Properties internal constructor(
        /**
         * Type of the relative radius of the gradient transition.
         */
        val value: Property<Value>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }

    /**
     * Type of the relative radius of the gradient transition.
     * 
     * Possible values: [nearest_corner, farthest_corner, nearest_side, farthest_side].
     */
    @Generated
    sealed interface Value
}

/**
 * @param value Type of the relative radius of the gradient transition.
 */
@Generated
fun DivScope.radialGradientRelativeRadius(
    `use named arguments`: Guard = Guard.instance,
    value: RadialGradientRelativeRadius.Value? = null,
): RadialGradientRelativeRadius = RadialGradientRelativeRadius(
    RadialGradientRelativeRadius.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Type of the relative radius of the gradient transition.
 */
@Generated
fun DivScope.radialGradientRelativeRadiusProps(
    `use named arguments`: Guard = Guard.instance,
    value: RadialGradientRelativeRadius.Value? = null,
) = RadialGradientRelativeRadius.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Type of the relative radius of the gradient transition.
 */
@Generated
fun TemplateScope.radialGradientRelativeRadiusRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<RadialGradientRelativeRadius.Value>? = null,
) = RadialGradientRelativeRadius.Properties(
    value = value,
)

/**
 * @param value Type of the relative radius of the gradient transition.
 */
@Generated
fun RadialGradientRelativeRadius.override(
    `use named arguments`: Guard = Guard.instance,
    value: RadialGradientRelativeRadius.Value? = null,
): RadialGradientRelativeRadius = RadialGradientRelativeRadius(
    RadialGradientRelativeRadius.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Type of the relative radius of the gradient transition.
 */
@Generated
fun RadialGradientRelativeRadius.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<RadialGradientRelativeRadius.Value>? = null,
): RadialGradientRelativeRadius = RadialGradientRelativeRadius(
    RadialGradientRelativeRadius.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Type of the relative radius of the gradient transition.
 */
@Generated
fun RadialGradientRelativeRadius.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<RadialGradientRelativeRadius.Value>? = null,
): RadialGradientRelativeRadius = RadialGradientRelativeRadius(
    RadialGradientRelativeRadius.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun RadialGradientRelativeRadius.asList() = listOf(this)

@Generated
fun RadialGradientRelativeRadius.Value.asList() = listOf(this)
