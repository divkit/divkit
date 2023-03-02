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
 * Drawable of a simple geometric shape.
 * 
 * Can be created using the method [shapeDrawable].
 * 
 * Required properties: `type, shape, color`.
 */
@Generated
class ShapeDrawable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Drawable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "shape_drawable")
    )

    operator fun plus(additive: Properties): ShapeDrawable = ShapeDrawable(
        Properties(
            color = additive.color ?: properties.color,
            shape = additive.shape ?: properties.shape,
            stroke = additive.stroke ?: properties.stroke,
        )
    )

    class Properties internal constructor(
        /**
         * Fill color.
         */
        val color: Property<Color>?,
        /**
         * Shape.
         */
        val shape: Property<Shape>?,
        /**
         * Stroke style.
         */
        val stroke: Property<Stroke>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("color", color)
            result.tryPutProperty("shape", shape)
            result.tryPutProperty("stroke", stroke)
            return result
        }
    }
}

/**
 * @param color Fill color.
 * @param shape Shape.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.shapeDrawable(
    `use named arguments`: Guard = Guard.instance,
    color: Color,
    shape: Shape,
    stroke: Stroke? = null,
): ShapeDrawable = ShapeDrawable(
    ShapeDrawable.Properties(
        color = valueOrNull(color),
        shape = valueOrNull(shape),
        stroke = valueOrNull(stroke),
    )
)

/**
 * @param color Fill color.
 * @param shape Shape.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.shapeDrawableProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    shape: Shape? = null,
    stroke: Stroke? = null,
) = ShapeDrawable.Properties(
    color = valueOrNull(color),
    shape = valueOrNull(shape),
    stroke = valueOrNull(stroke),
)

/**
 * @param color Fill color.
 * @param shape Shape.
 * @param stroke Stroke style.
 */
@Generated
fun TemplateScope.shapeDrawableRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    shape: ReferenceProperty<Shape>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
) = ShapeDrawable.Properties(
    color = color,
    shape = shape,
    stroke = stroke,
)

/**
 * @param color Fill color.
 * @param shape Shape.
 * @param stroke Stroke style.
 */
@Generated
fun ShapeDrawable.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    shape: Shape? = null,
    stroke: Stroke? = null,
): ShapeDrawable = ShapeDrawable(
    ShapeDrawable.Properties(
        color = valueOrNull(color) ?: properties.color,
        shape = valueOrNull(shape) ?: properties.shape,
        stroke = valueOrNull(stroke) ?: properties.stroke,
    )
)

/**
 * @param color Fill color.
 * @param shape Shape.
 * @param stroke Stroke style.
 */
@Generated
fun ShapeDrawable.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    shape: ReferenceProperty<Shape>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
): ShapeDrawable = ShapeDrawable(
    ShapeDrawable.Properties(
        color = color ?: properties.color,
        shape = shape ?: properties.shape,
        stroke = stroke ?: properties.stroke,
    )
)

/**
 * @param color Fill color.
 */
@Generated
fun ShapeDrawable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
): ShapeDrawable = ShapeDrawable(
    ShapeDrawable.Properties(
        color = color ?: properties.color,
        shape = properties.shape,
        stroke = properties.stroke,
    )
)

@Generated
fun ShapeDrawable.asList() = listOf(this)
