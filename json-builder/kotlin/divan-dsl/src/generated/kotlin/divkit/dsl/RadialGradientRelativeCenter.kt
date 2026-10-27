@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Location of the central point of the gradient relative to the element borders.
 * 
 * Can be created using the method [radialGradientRelativeCenter].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = RadialGradientRelativeCenterSerializer::class)
@ExposedCopyVisibility
data class RadialGradientRelativeCenter internal constructor(
    val properties: Properties,
) : RadialGradientCenter {
    operator fun plus(additive: Properties): RadialGradientRelativeCenter = RadialGradientRelativeCenter(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Coordinate value in the range "0...1".
         */
        val value: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param value Coordinate value in the range "0...1".
 */
@Generated
fun DivScope.radialGradientRelativeCenter(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): RadialGradientRelativeCenter = RadialGradientRelativeCenter(
    RadialGradientRelativeCenter.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Coordinate value in the range "0...1".
 */
@Generated
fun DivScope.radialGradientRelativeCenterProps(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
) = RadialGradientRelativeCenter.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Coordinate value in the range "0...1".
 */
@Generated
fun TemplateScope.radialGradientRelativeCenterRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
) = RadialGradientRelativeCenter.Properties(
    value = value,
)

/**
 * @param value Coordinate value in the range "0...1".
 */
@Generated
fun RadialGradientRelativeCenter.override(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): RadialGradientRelativeCenter = RadialGradientRelativeCenter(
    RadialGradientRelativeCenter.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Coordinate value in the range "0...1".
 */
@Generated
fun RadialGradientRelativeCenter.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
): RadialGradientRelativeCenter = RadialGradientRelativeCenter(
    RadialGradientRelativeCenter.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Coordinate value in the range "0...1".
 */
@Generated
fun RadialGradientRelativeCenter.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Double>? = null,
): RadialGradientRelativeCenter = RadialGradientRelativeCenter(
    RadialGradientRelativeCenter.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Coordinate value in the range "0...1".
 */
@Generated
fun RadialGradientRelativeCenter.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Double>? = null,
): RadialGradientRelativeCenter = RadialGradientRelativeCenter(
    RadialGradientRelativeCenter.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun RadialGradientRelativeCenter.asList() = listOf(this)
