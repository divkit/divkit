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
 * Required properties: `type, colors`.
 */
@Generated
class RadialGradient internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TextGradient, Background {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "radial_gradient")
    )

    operator fun plus(additive: Properties): RadialGradient = RadialGradient(
        Properties(
            centerX = additive.centerX ?: properties.centerX,
            centerY = additive.centerY ?: properties.centerY,
            colors = additive.colors ?: properties.colors,
            radius = additive.radius ?: properties.radius,
        )
    )

    class Properties internal constructor(
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
         * Colors. Gradient points are located at an equal distance from each other.
         */
        val colors: Property<List<Color>>?,
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
            result.tryPutProperty("colors", colors)
            result.tryPutProperty("radius", radius)
            return result
        }
    }
}

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun DivScope.radialGradient(
    `use named arguments`: Guard = Guard.instance,
    centerX: RadialGradientCenter? = null,
    centerY: RadialGradientCenter? = null,
    colors: List<Color>,
    radius: RadialGradientRadius? = null,
): RadialGradient = RadialGradient(
    RadialGradient.Properties(
        centerX = valueOrNull(centerX),
        centerY = valueOrNull(centerY),
        colors = valueOrNull(colors),
        radius = valueOrNull(radius),
    )
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun DivScope.radialGradientProps(
    `use named arguments`: Guard = Guard.instance,
    centerX: RadialGradientCenter? = null,
    centerY: RadialGradientCenter? = null,
    colors: List<Color>? = null,
    radius: RadialGradientRadius? = null,
) = RadialGradient.Properties(
    centerX = valueOrNull(centerX),
    centerY = valueOrNull(centerY),
    colors = valueOrNull(colors),
    radius = valueOrNull(radius),
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun TemplateScope.radialGradientRefs(
    `use named arguments`: Guard = Guard.instance,
    centerX: ReferenceProperty<RadialGradientCenter>? = null,
    centerY: ReferenceProperty<RadialGradientCenter>? = null,
    colors: ReferenceProperty<List<Color>>? = null,
    radius: ReferenceProperty<RadialGradientRadius>? = null,
) = RadialGradient.Properties(
    centerX = centerX,
    centerY = centerY,
    colors = colors,
    radius = radius,
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun RadialGradient.override(
    `use named arguments`: Guard = Guard.instance,
    centerX: RadialGradientCenter? = null,
    centerY: RadialGradientCenter? = null,
    colors: List<Color>? = null,
    radius: RadialGradientRadius? = null,
): RadialGradient = RadialGradient(
    RadialGradient.Properties(
        centerX = valueOrNull(centerX) ?: properties.centerX,
        centerY = valueOrNull(centerY) ?: properties.centerY,
        colors = valueOrNull(colors) ?: properties.colors,
        radius = valueOrNull(radius) ?: properties.radius,
    )
)

/**
 * @param centerX Shift of the central point of the gradient relative to the left edge along the X axis.
 * @param centerY Shift of the central point of the gradient relative to the top edge along the Y axis.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 * @param radius Radius of the gradient transition.
 */
@Generated
fun RadialGradient.defer(
    `use named arguments`: Guard = Guard.instance,
    centerX: ReferenceProperty<RadialGradientCenter>? = null,
    centerY: ReferenceProperty<RadialGradientCenter>? = null,
    colors: ReferenceProperty<List<Color>>? = null,
    radius: ReferenceProperty<RadialGradientRadius>? = null,
): RadialGradient = RadialGradient(
    RadialGradient.Properties(
        centerX = centerX ?: properties.centerX,
        centerY = centerY ?: properties.centerY,
        colors = colors ?: properties.colors,
        radius = radius ?: properties.radius,
    )
)

@Generated
fun RadialGradient.asList() = listOf(this)
