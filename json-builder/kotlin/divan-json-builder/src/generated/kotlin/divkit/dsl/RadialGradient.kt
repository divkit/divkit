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
 * Radial gradient.
 * 
 * Can be created using the method [radialGradient].
 * 
 * Required parameters: `type`.
 */
@Generated
data class RadialGradient internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Background, TextGradient {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "radial_gradient")
    )

    operator fun plus(additive: Properties): RadialGradient = RadialGradient(
        Properties(
            centerX = additive.centerX ?: properties.centerX,
            centerY = additive.centerY ?: properties.centerY,
            colorMap = additive.colorMap ?: properties.colorMap,
            colors = additive.colors ?: properties.colors,
            radius = additive.radius ?: properties.radius,
        )
    )

    data class Properties internal constructor(
        /**
         * Shift of the central point of the gradient relative to the left edge along the X axis.
         * Default value: `{"type": "relative", "value": 0.5 }`.
         */
        val centerX: Property<RadialGradientCenter>?,
        /**
         * Shift of the central point of the gradient relative to the top edge along the Y axis.
         * Default value: `{"type": "relative", "value": 0.5 }`.
         */
        val centerY: Property<RadialGradientCenter>?,
        /**
         * Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
         */
        val colorMap: Property<List<ColorPoint>>?,
        /**
         * Colors. Gradient points are located at an equal distance from each other.
         */
        val colors: Property<List<ArrayElement<Color>>>?,
        /**
         * Radius of the gradient transition.
         * Default value: `{"type": "relative", "value": "farthest_corner" }`.
         */
        val radius: Property<RadialGradientRadius>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("center_x", centerX)
            result.tryPutProperty("center_y", centerY)
            result.tryPutProperty("color_map", colorMap)
            result.tryPutProperty("colors", colors)
            result.tryPutProperty("radius", radius)
            return result
        }
    }

    /**
     * Describes color at particular gradient position.
     * 
     * Can be created using the method [radialGradientColorPoint].
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
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun DivScope.radialGradient(
    `use named arguments`: Guard = Guard.instance,
    centerX: RadialGradientCenter? = null,
    centerY: RadialGradientCenter? = null,
    colorMap: List<RadialGradient.ColorPoint>? = null,
    colors: List<ArrayElement<Color>>? = null,
    radius: RadialGradientRadius? = null,
): RadialGradient = RadialGradient(
    RadialGradient.Properties(
        centerX = valueOrNull(centerX),
        centerY = valueOrNull(centerY),
        colorMap = valueOrNull(colorMap),
        colors = valueOrNull(colors),
        radius = valueOrNull(radius),
    )
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun DivScope.radialGradientProps(
    `use named arguments`: Guard = Guard.instance,
    centerX: RadialGradientCenter? = null,
    centerY: RadialGradientCenter? = null,
    colorMap: List<RadialGradient.ColorPoint>? = null,
    colors: List<ArrayElement<Color>>? = null,
    radius: RadialGradientRadius? = null,
) = RadialGradient.Properties(
    centerX = valueOrNull(centerX),
    centerY = valueOrNull(centerY),
    colorMap = valueOrNull(colorMap),
    colors = valueOrNull(colors),
    radius = valueOrNull(radius),
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun TemplateScope.radialGradientRefs(
    `use named arguments`: Guard = Guard.instance,
    centerX: ReferenceProperty<RadialGradientCenter>? = null,
    centerY: ReferenceProperty<RadialGradientCenter>? = null,
    colorMap: ReferenceProperty<List<RadialGradient.ColorPoint>>? = null,
    colors: ReferenceProperty<List<ArrayElement<Color>>>? = null,
    radius: ReferenceProperty<RadialGradientRadius>? = null,
) = RadialGradient.Properties(
    centerX = centerX,
    centerY = centerY,
    colorMap = colorMap,
    colors = colors,
    radius = radius,
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun RadialGradient.override(
    `use named arguments`: Guard = Guard.instance,
    centerX: RadialGradientCenter? = null,
    centerY: RadialGradientCenter? = null,
    colorMap: List<RadialGradient.ColorPoint>? = null,
    colors: List<ArrayElement<Color>>? = null,
    radius: RadialGradientRadius? = null,
): RadialGradient = RadialGradient(
    RadialGradient.Properties(
        centerX = valueOrNull(centerX) ?: properties.centerX,
        centerY = valueOrNull(centerY) ?: properties.centerY,
        colorMap = valueOrNull(colorMap) ?: properties.colorMap,
        colors = valueOrNull(colors) ?: properties.colors,
        radius = valueOrNull(radius) ?: properties.radius,
    )
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun RadialGradient.defer(
    `use named arguments`: Guard = Guard.instance,
    centerX: ReferenceProperty<RadialGradientCenter>? = null,
    centerY: ReferenceProperty<RadialGradientCenter>? = null,
    colorMap: ReferenceProperty<List<RadialGradient.ColorPoint>>? = null,
    colors: ReferenceProperty<List<ArrayElement<Color>>>? = null,
    radius: ReferenceProperty<RadialGradientRadius>? = null,
): RadialGradient = RadialGradient(
    RadialGradient.Properties(
        centerX = centerX ?: properties.centerX,
        centerY = centerY ?: properties.centerY,
        colorMap = colorMap ?: properties.colorMap,
        colors = colors ?: properties.colors,
        radius = radius ?: properties.radius,
    )
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colorMap Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun RadialGradient.modify(
    `use named arguments`: Guard = Guard.instance,
    centerX: Property<RadialGradientCenter>? = null,
    centerY: Property<RadialGradientCenter>? = null,
    colorMap: Property<List<RadialGradient.ColorPoint>>? = null,
    colors: Property<List<ArrayElement<Color>>>? = null,
    radius: Property<RadialGradientRadius>? = null,
): RadialGradient = RadialGradient(
    RadialGradient.Properties(
        centerX = centerX ?: properties.centerX,
        centerY = centerY ?: properties.centerY,
        colorMap = colorMap ?: properties.colorMap,
        colors = colors ?: properties.colors,
        radius = radius ?: properties.radius,
    )
)

@Generated
fun RadialGradient.asList() = listOf(this)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun DivScope.radialGradientColorPoint(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    position: Double? = null,
): RadialGradient.ColorPoint = RadialGradient.ColorPoint(
    RadialGradient.ColorPoint.Properties(
        color = valueOrNull(color),
        position = valueOrNull(position),
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun DivScope.radialGradientColorPointProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    position: Double? = null,
) = RadialGradient.ColorPoint.Properties(
    color = valueOrNull(color),
    position = valueOrNull(position),
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun TemplateScope.radialGradientColorPointRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    position: ReferenceProperty<Double>? = null,
) = RadialGradient.ColorPoint.Properties(
    color = color,
    position = position,
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun RadialGradient.ColorPoint.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    position: Double? = null,
): RadialGradient.ColorPoint = RadialGradient.ColorPoint(
    RadialGradient.ColorPoint.Properties(
        color = valueOrNull(color) ?: properties.color,
        position = valueOrNull(position) ?: properties.position,
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun RadialGradient.ColorPoint.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    position: ReferenceProperty<Double>? = null,
): RadialGradient.ColorPoint = RadialGradient.ColorPoint(
    RadialGradient.ColorPoint.Properties(
        color = color ?: properties.color,
        position = position ?: properties.position,
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun RadialGradient.ColorPoint.modify(
    `use named arguments`: Guard = Guard.instance,
    color: Property<Color>? = null,
    position: Property<Double>? = null,
): RadialGradient.ColorPoint = RadialGradient.ColorPoint(
    RadialGradient.ColorPoint.Properties(
        color = color ?: properties.color,
        position = position ?: properties.position,
    )
)

/**
 * @param color Gradient color corresponding to gradient point.
 * @param position The position of the gradient point.
 */
@Generated
fun RadialGradient.ColorPoint.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
    position: ExpressionProperty<Double>? = null,
): RadialGradient.ColorPoint = RadialGradient.ColorPoint(
    RadialGradient.ColorPoint.Properties(
        color = color ?: properties.color,
        position = position ?: properties.position,
    )
)

@Generated
fun RadialGradient.ColorPoint.asList() = listOf(this)
