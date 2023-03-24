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
 * A rectangle with rounded corners.
 * 
 * Can be created using the method [roundedRectangleShape].
 * 
 * Required parameters: `type`.
 */
@Generated
class RoundedRectangleShape internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Shape {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "rounded_rectangle")
    )

    operator fun plus(additive: Properties): RoundedRectangleShape = RoundedRectangleShape(
        Properties(
            backgroundColor = additive.backgroundColor ?: properties.backgroundColor,
            cornerRadius = additive.cornerRadius ?: properties.cornerRadius,
            itemHeight = additive.itemHeight ?: properties.itemHeight,
            itemWidth = additive.itemWidth ?: properties.itemWidth,
            stroke = additive.stroke ?: properties.stroke,
        )
    )

    class Properties internal constructor(
        /**
         * Fill color.
         */
        val backgroundColor: Property<Color>?,
        /**
         * Corner rounding radius.
         * Default value: `{"type":"fixed","value":5}`.
         */
        val cornerRadius: Property<FixedSize>?,
        /**
         * Height.
         * Default value: `{"type":"fixed","value":10}`.
         */
        val itemHeight: Property<FixedSize>?,
        /**
         * Width.
         * Default value: `{"type":"fixed","value":10}`.
         */
        val itemWidth: Property<FixedSize>?,
        /**
         * Stroke style.
         */
        val stroke: Property<Stroke>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("background_color", backgroundColor)
            result.tryPutProperty("corner_radius", cornerRadius)
            result.tryPutProperty("item_height", itemHeight)
            result.tryPutProperty("item_width", itemWidth)
            result.tryPutProperty("stroke", stroke)
            return result
        }
    }
}

/**
 * @param backgroundColor Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param itemHeight Height.
 * @param itemWidth Width.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.roundedRectangleShape(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: Color? = null,
    cornerRadius: FixedSize? = null,
    itemHeight: FixedSize? = null,
    itemWidth: FixedSize? = null,
    stroke: Stroke? = null,
): RoundedRectangleShape = RoundedRectangleShape(
    RoundedRectangleShape.Properties(
        backgroundColor = valueOrNull(backgroundColor),
        cornerRadius = valueOrNull(cornerRadius),
        itemHeight = valueOrNull(itemHeight),
        itemWidth = valueOrNull(itemWidth),
        stroke = valueOrNull(stroke),
    )
)

/**
 * @param backgroundColor Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param itemHeight Height.
 * @param itemWidth Width.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.roundedRectangleShapeProps(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: Color? = null,
    cornerRadius: FixedSize? = null,
    itemHeight: FixedSize? = null,
    itemWidth: FixedSize? = null,
    stroke: Stroke? = null,
) = RoundedRectangleShape.Properties(
    backgroundColor = valueOrNull(backgroundColor),
    cornerRadius = valueOrNull(cornerRadius),
    itemHeight = valueOrNull(itemHeight),
    itemWidth = valueOrNull(itemWidth),
    stroke = valueOrNull(stroke),
)

/**
 * @param backgroundColor Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param itemHeight Height.
 * @param itemWidth Width.
 * @param stroke Stroke style.
 */
@Generated
fun TemplateScope.roundedRectangleShapeRefs(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: ReferenceProperty<Color>? = null,
    cornerRadius: ReferenceProperty<FixedSize>? = null,
    itemHeight: ReferenceProperty<FixedSize>? = null,
    itemWidth: ReferenceProperty<FixedSize>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
) = RoundedRectangleShape.Properties(
    backgroundColor = backgroundColor,
    cornerRadius = cornerRadius,
    itemHeight = itemHeight,
    itemWidth = itemWidth,
    stroke = stroke,
)

/**
 * @param backgroundColor Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param itemHeight Height.
 * @param itemWidth Width.
 * @param stroke Stroke style.
 */
@Generated
fun RoundedRectangleShape.override(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: Color? = null,
    cornerRadius: FixedSize? = null,
    itemHeight: FixedSize? = null,
    itemWidth: FixedSize? = null,
    stroke: Stroke? = null,
): RoundedRectangleShape = RoundedRectangleShape(
    RoundedRectangleShape.Properties(
        backgroundColor = valueOrNull(backgroundColor) ?: properties.backgroundColor,
        cornerRadius = valueOrNull(cornerRadius) ?: properties.cornerRadius,
        itemHeight = valueOrNull(itemHeight) ?: properties.itemHeight,
        itemWidth = valueOrNull(itemWidth) ?: properties.itemWidth,
        stroke = valueOrNull(stroke) ?: properties.stroke,
    )
)

/**
 * @param backgroundColor Fill color.
 * @param cornerRadius Corner rounding radius.
 * @param itemHeight Height.
 * @param itemWidth Width.
 * @param stroke Stroke style.
 */
@Generated
fun RoundedRectangleShape.defer(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: ReferenceProperty<Color>? = null,
    cornerRadius: ReferenceProperty<FixedSize>? = null,
    itemHeight: ReferenceProperty<FixedSize>? = null,
    itemWidth: ReferenceProperty<FixedSize>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
): RoundedRectangleShape = RoundedRectangleShape(
    RoundedRectangleShape.Properties(
        backgroundColor = backgroundColor ?: properties.backgroundColor,
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        itemHeight = itemHeight ?: properties.itemHeight,
        itemWidth = itemWidth ?: properties.itemWidth,
        stroke = stroke ?: properties.stroke,
    )
)

/**
 * @param backgroundColor Fill color.
 */
@Generated
fun RoundedRectangleShape.evaluate(
    `use named arguments`: Guard = Guard.instance,
    backgroundColor: ExpressionProperty<Color>? = null,
): RoundedRectangleShape = RoundedRectangleShape(
    RoundedRectangleShape.Properties(
        backgroundColor = backgroundColor ?: properties.backgroundColor,
        cornerRadius = properties.cornerRadius,
        itemHeight = properties.itemHeight,
        itemWidth = properties.itemWidth,
        stroke = properties.stroke,
    )
)

@Generated
fun RoundedRectangleShape.asList() = listOf(this)
