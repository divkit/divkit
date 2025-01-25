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
 * Circle.
 * 
 * Can be created using the method [circleShape].
 * 
 * Required parameters: `type`.
 */
@Generated
data class CircleShape internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Shape {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "circle")
    )

    operator fun plus(additive: Properties): CircleShape = CircleShape(
        Properties(
            backgroundColor = additive.backgroundColor ?: properties.backgroundColor,
            radius = additive.radius ?: properties.radius,
            stroke = additive.stroke ?: properties.stroke,
        )
    )

    data class Properties internal constructor(
        /**
         * Fill color.
         */
        val backgroundColor: Property<Color>?,
        /**
         * Radius.
         * Default value: `{"type":"fixed","value":10}`.
         */
        val radius: Property<FixedSize>?,
        /**
         * Stroke style.
         */
        val stroke: Property<Stroke>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("background_color", backgroundColor)
            result.tryPutProperty("radius", radius)
            result.tryPutProperty("stroke", stroke)
            return result
        }
    }
}

/**
 * @param backgroundColor Fill color.
 * @param radius Radius.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.circleShape(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: Color? = null,
    radius: FixedSize? = null,
    stroke: Stroke? = null,
): CircleShape = CircleShape(
    CircleShape.Properties(
        backgroundColor = valueOrNull(backgroundColor),
        radius = valueOrNull(radius),
        stroke = valueOrNull(stroke),
    )
)

/**
 * @param backgroundColor Fill color.
 * @param radius Radius.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.circleShapeProps(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: Color? = null,
    radius: FixedSize? = null,
    stroke: Stroke? = null,
) = CircleShape.Properties(
    backgroundColor = valueOrNull(backgroundColor),
    radius = valueOrNull(radius),
    stroke = valueOrNull(stroke),
)

/**
 * @param backgroundColor Fill color.
 * @param radius Radius.
 * @param stroke Stroke style.
 */
@Generated
fun TemplateScope.circleShapeRefs(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: ReferenceProperty<Color>? = null,
    radius: ReferenceProperty<FixedSize>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
) = CircleShape.Properties(
    backgroundColor = backgroundColor,
    radius = radius,
    stroke = stroke,
)

/**
 * @param backgroundColor Fill color.
 * @param radius Radius.
 * @param stroke Stroke style.
 */
@Generated
fun CircleShape.override(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: Color? = null,
    radius: FixedSize? = null,
    stroke: Stroke? = null,
): CircleShape = CircleShape(
    CircleShape.Properties(
        backgroundColor = valueOrNull(backgroundColor) ?: properties.backgroundColor,
        radius = valueOrNull(radius) ?: properties.radius,
        stroke = valueOrNull(stroke) ?: properties.stroke,
    )
)

/**
 * @param backgroundColor Fill color.
 * @param radius Radius.
 * @param stroke Stroke style.
 */
@Generated
fun CircleShape.defer(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: ReferenceProperty<Color>? = null,
    radius: ReferenceProperty<FixedSize>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
): CircleShape = CircleShape(
    CircleShape.Properties(
        backgroundColor = backgroundColor ?: properties.backgroundColor,
        radius = radius ?: properties.radius,
        stroke = stroke ?: properties.stroke,
    )
)

/**
 * @param backgroundColor Fill color.
 */
@Generated
fun CircleShape.evaluate(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: ExpressionProperty<Color>? = null,
): CircleShape = CircleShape(
    CircleShape.Properties(
        backgroundColor = backgroundColor ?: properties.backgroundColor,
        radius = properties.radius,
        stroke = properties.stroke,
    )
)

@Generated
fun CircleShape.asList() = listOf(this)
