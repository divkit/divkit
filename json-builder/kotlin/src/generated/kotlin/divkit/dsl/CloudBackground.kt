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
 * Cloud-style text background. Rows have a rectangular background in the specified color with rounded corners.
 * 
 * Can be created using the method [cloudBackground].
 * 
 * Required parameters: `type, corner_radius, color`.
 */
@Generated
data class CloudBackground internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TextRangeBackground {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "cloud")
    )

    operator fun plus(additive: Properties): CloudBackground = CloudBackground(
        Properties(
            color = additive.color ?: properties.color,
            cornerRadius = additive.cornerRadius ?: properties.cornerRadius,
            paddings = additive.paddings ?: properties.paddings,
        )
    )

    data class Properties internal constructor(
        /**
         * Fill color.
         */
        val color: Property<Color>?,
        /**
         * Corner rounding radius.
         */
        val cornerRadius: Property<Int>?,
        /**
         * Margins between the row border and background border.
         */
        val paddings: Property<EdgeInsets>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("color", color)
            result.tryPutProperty("corner_radius", cornerRadius)
            result.tryPutProperty("paddings", paddings)
            return result
        }
    }
}

/**
 * @param color Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param paddings Margins between the row border and background border.
 */
@Generated
fun DivScope.cloudBackground(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    cornerRadius: Int? = null,
    paddings: EdgeInsets? = null,
): CloudBackground = CloudBackground(
    CloudBackground.Properties(
        color = valueOrNull(color),
        cornerRadius = valueOrNull(cornerRadius),
        paddings = valueOrNull(paddings),
    )
)

/**
 * @param color Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param paddings Margins between the row border and background border.
 */
@Generated
fun DivScope.cloudBackgroundProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    cornerRadius: Int? = null,
    paddings: EdgeInsets? = null,
) = CloudBackground.Properties(
    color = valueOrNull(color),
    cornerRadius = valueOrNull(cornerRadius),
    paddings = valueOrNull(paddings),
)

/**
 * @param color Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param paddings Margins between the row border and background border.
 */
@Generated
fun TemplateScope.cloudBackgroundRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    cornerRadius: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
) = CloudBackground.Properties(
    color = color,
    cornerRadius = cornerRadius,
    paddings = paddings,
)

/**
 * @param color Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param paddings Margins between the row border and background border.
 */
@Generated
fun CloudBackground.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    cornerRadius: Int? = null,
    paddings: EdgeInsets? = null,
): CloudBackground = CloudBackground(
    CloudBackground.Properties(
        color = valueOrNull(color) ?: properties.color,
        cornerRadius = valueOrNull(cornerRadius) ?: properties.cornerRadius,
        paddings = valueOrNull(paddings) ?: properties.paddings,
    )
)

/**
 * @param color Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param paddings Margins between the row border and background border.
 */
@Generated
fun CloudBackground.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    cornerRadius: ReferenceProperty<Int>? = null,
    paddings: ReferenceProperty<EdgeInsets>? = null,
): CloudBackground = CloudBackground(
    CloudBackground.Properties(
        color = color ?: properties.color,
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        paddings = paddings ?: properties.paddings,
    )
)

/**
 * @param color Fill color.
 * @param cornerRadius Corner rounding radius.
 */
@Generated
fun CloudBackground.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
    cornerRadius: ExpressionProperty<Int>? = null,
): CloudBackground = CloudBackground(
    CloudBackground.Properties(
        color = color ?: properties.color,
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        paddings = properties.paddings,
    )
)

@Generated
fun CloudBackground.asList() = listOf(this)
