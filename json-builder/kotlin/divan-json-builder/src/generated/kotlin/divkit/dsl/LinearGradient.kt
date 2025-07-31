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
 * Linear gradient.
 * 
 * Can be created using the method [linearGradient].
 * 
 * Required parameters: `type`.
 */
@Generated
data class LinearGradient internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Background, TextGradient {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "gradient")
    )

    operator fun plus(additive: Properties): LinearGradient = LinearGradient(
        Properties(
            angle = additive.angle ?: properties.angle,
            colorMap = additive.colorMap ?: properties.colorMap,
            colors = additive.colors ?: properties.colors,
        )
    )

    data class Properties internal constructor(
        /**
         * Angle of gradient direction.
         * Default value: `0`.
         */
        val angle: Property<Int>?,
        /**
         * Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
         */
        val colorMap: Property<List<ColorPoint>>?,
        /**
         * Colors. Gradient points are located at an equal distance from each other.
         */
        val colors: Property<List<ArrayElement<Color>>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("angle", angle)
            result.tryPutProperty("color_map", colorMap)
            result.tryPutProperty("colors", colors)
            return result
        }
    }

    /**
     * Describes color at particular gradient position.
     * 
     * Can be created using the method [linearGradientColorPoint].
     * 
     * Required parameters: `position, color`.
     */
    @Generated
    data class ColorPoint internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): ColorPoint = ColorPoint(
            Properties(
                color = additive.color ?: properties.color,
                position = additive.position ?: properties.position,
            )
        )

        data class Properties internal constructor(
            /**
             * Gradient color corresponding to gradient point.
             */
            val color: Property<Color>?,
            /**
             * The position of the gradient point.
             */
            val position: Property<Double>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("color", color)
                result.tryPutProperty("position", position)
                return result
            }
        }
    }

}

/**
 * @param angle Angle of gradient direction.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun DivScope.linearGradient(
    `use named arguments`: Guard = Guard.instance,
    angle: Int? = null,
    colorMap: List<LinearGradient.ColorPoint>? = null,
    colors: List<ArrayElement<Color>>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = valueOrNull(angle),
        colorMap = valueOrNull(colorMap),
        colors = valueOrNull(colors),
    )
)

/**
 * @param angle Angle of gradient direction.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun DivScope.linearGradientProps(
    `use named arguments`: Guard = Guard.instance,
    angle: Int? = null,
    colorMap: List<LinearGradient.ColorPoint>? = null,
    colors: List<ArrayElement<Color>>? = null,
) = LinearGradient.Properties(
    angle = valueOrNull(angle),
    colorMap = valueOrNull(colorMap),
    colors = valueOrNull(colors),
)

/**
 * @param angle Angle of gradient direction.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun TemplateScope.linearGradientRefs(
    `use named arguments`: Guard = Guard.instance,
    angle: ReferenceProperty<Int>? = null,
    colorMap: ReferenceProperty<List<LinearGradient.ColorPoint>>? = null,
    colors: ReferenceProperty<List<ArrayElement<Color>>>? = null,
) = LinearGradient.Properties(
    angle = angle,
    colorMap = colorMap,
    colors = colors,
)

/**
 * @param angle Angle of gradient direction.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun LinearGradient.override(
    `use named arguments`: Guard = Guard.instance,
    angle: Int? = null,
    colorMap: List<LinearGradient.ColorPoint>? = null,
    colors: List<ArrayElement<Color>>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = valueOrNull(angle) ?: properties.angle,
        colorMap = valueOrNull(colorMap) ?: properties.colorMap,
        colors = valueOrNull(colors) ?: properties.colors,
    )
)

/**
 * @param angle Angle of gradient direction.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun LinearGradient.defer(
    `use named arguments`: Guard = Guard.instance,
    angle: ReferenceProperty<Int>? = null,
    colorMap: ReferenceProperty<List<LinearGradient.ColorPoint>>? = null,
    colors: ReferenceProperty<List<ArrayElement<Color>>>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = angle ?: properties.angle,
        colorMap = colorMap ?: properties.colorMap,
        colors = colors ?: properties.colors,
    )
)

/**
 * @param angle Angle of gradient direction.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun LinearGradient.modify(
    `use named arguments`: Guard = Guard.instance,
    angle: Property<Int>? = null,
    colorMap: Property<List<LinearGradient.ColorPoint>>? = null,
    colors: Property<List<ArrayElement<Color>>>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = angle ?: properties.angle,
        colorMap = colorMap ?: properties.colorMap,
        colors = colors ?: properties.colors,
    )
)

/**
 * @param angle Angle of gradient direction.
 */
@Generated
fun LinearGradient.evaluate(
    `use named arguments`: Guard = Guard.instance,
    angle: ExpressionProperty<Int>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = angle ?: properties.angle,
        colorMap = properties.colorMap,
        colors = properties.colors,
    )
)

@Generated
fun LinearGradient.asList() = listOf(this)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun DivScope.linearGradientColorPoint(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    position: Double? = null,
): LinearGradient.ColorPoint = LinearGradient.ColorPoint(
    LinearGradient.ColorPoint.Properties(
        color = valueOrNull(color),
        position = valueOrNull(position),
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun DivScope.linearGradientColorPointProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    position: Double? = null,
) = LinearGradient.ColorPoint.Properties(
    color = valueOrNull(color),
    position = valueOrNull(position),
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun TemplateScope.linearGradientColorPointRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    position: ReferenceProperty<Double>? = null,
) = LinearGradient.ColorPoint.Properties(
    color = color,
    position = position,
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun LinearGradient.ColorPoint.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    position: Double? = null,
): LinearGradient.ColorPoint = LinearGradient.ColorPoint(
    LinearGradient.ColorPoint.Properties(
        color = valueOrNull(color) ?: properties.color,
        position = valueOrNull(position) ?: properties.position,
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun LinearGradient.ColorPoint.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    position: ReferenceProperty<Double>? = null,
): LinearGradient.ColorPoint = LinearGradient.ColorPoint(
    LinearGradient.ColorPoint.Properties(
        color = color ?: properties.color,
        position = position ?: properties.position,
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun LinearGradient.ColorPoint.modify(
    `use named arguments`: Guard = Guard.instance,
    color: Property<Color>? = null,
    position: Property<Double>? = null,
): LinearGradient.ColorPoint = LinearGradient.ColorPoint(
    LinearGradient.ColorPoint.Properties(
        color = color ?: properties.color,
        position = position ?: properties.position,
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun LinearGradient.ColorPoint.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
    position: ExpressionProperty<Double>? = null,
): LinearGradient.ColorPoint = LinearGradient.ColorPoint(
    LinearGradient.ColorPoint.Properties(
        color = color ?: properties.color,
        position = position ?: properties.position,
    )
)

@Generated
fun LinearGradient.ColorPoint.asList() = listOf(this)
