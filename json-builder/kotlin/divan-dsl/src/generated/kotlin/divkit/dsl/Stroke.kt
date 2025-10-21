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
 * Stroke.
 * 
 * Can be created using the method [stroke].
 * 
 * Required parameters: `color`.
 */
@Generated
@ExposedCopyVisibility
data class Stroke internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Stroke = Stroke(
        Properties(
            color = additive.color ?: properties.color,
            style = additive.style ?: properties.style,
            unit = additive.unit ?: properties.unit,
            width = additive.width ?: properties.width,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Stroke color.
         */
        val color: Property<Color>?,
        /**
         * Stroke style. Supported for border stroke only.
         * Default value: `{"type": "solid"}`.
         */
        val style: Property<StrokeStyle>?,
        /**
         * Default value: `dp`.
         */
        val unit: Property<SizeUnit>?,
        /**
         * Stroke width.
         * Default value: `1`.
         */
        val width: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("color", color)
            result.tryPutProperty("style", style)
            result.tryPutProperty("unit", unit)
            result.tryPutProperty("width", width)
            return result
        }
    }
}

/**
 * @param color Stroke color.
 * @param style Stroke style. Supported for border stroke only.
 * @param width Stroke width.
 */
@Generated
fun DivScope.stroke(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    style: StrokeStyle? = null,
    unit: SizeUnit? = null,
    width: Double? = null,
): Stroke = Stroke(
    Stroke.Properties(
        color = valueOrNull(color),
        style = valueOrNull(style),
        unit = valueOrNull(unit),
        width = valueOrNull(width),
    )
)

/**
 * @param color Stroke color.
 * @param style Stroke style. Supported for border stroke only.
 * @param width Stroke width.
 */
@Generated
fun DivScope.strokeProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    style: StrokeStyle? = null,
    unit: SizeUnit? = null,
    width: Double? = null,
) = Stroke.Properties(
    color = valueOrNull(color),
    style = valueOrNull(style),
    unit = valueOrNull(unit),
    width = valueOrNull(width),
)

/**
 * @param color Stroke color.
 * @param style Stroke style. Supported for border stroke only.
 * @param width Stroke width.
 */
@Generated
fun TemplateScope.strokeRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    style: ReferenceProperty<StrokeStyle>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
    width: ReferenceProperty<Double>? = null,
) = Stroke.Properties(
    color = color,
    style = style,
    unit = unit,
    width = width,
)

/**
 * @param color Stroke color.
 * @param style Stroke style. Supported for border stroke only.
 * @param width Stroke width.
 */
@Generated
fun Stroke.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    style: StrokeStyle? = null,
    unit: SizeUnit? = null,
    width: Double? = null,
): Stroke = Stroke(
    Stroke.Properties(
        color = valueOrNull(color) ?: properties.color,
        style = valueOrNull(style) ?: properties.style,
        unit = valueOrNull(unit) ?: properties.unit,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param color Stroke color.
 * @param style Stroke style. Supported for border stroke only.
 * @param width Stroke width.
 */
@Generated
fun Stroke.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    style: ReferenceProperty<StrokeStyle>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
    width: ReferenceProperty<Double>? = null,
): Stroke = Stroke(
    Stroke.Properties(
        color = color ?: properties.color,
        style = style ?: properties.style,
        unit = unit ?: properties.unit,
        width = width ?: properties.width,
    )
)

/**
 * @param color Stroke color.
 * @param style Stroke style. Supported for border stroke only.
 * @param width Stroke width.
 */
@Generated
fun Stroke.modify(
    `use named arguments`: Guard = Guard.instance,
    color: Property<Color>? = null,
    style: Property<StrokeStyle>? = null,
    unit: Property<SizeUnit>? = null,
    width: Property<Double>? = null,
): Stroke = Stroke(
    Stroke.Properties(
        color = color ?: properties.color,
        style = style ?: properties.style,
        unit = unit ?: properties.unit,
        width = width ?: properties.width,
    )
)

/**
 * @param color Stroke color.
 * @param width Stroke width.
 */
@Generated
fun Stroke.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
    unit: ExpressionProperty<SizeUnit>? = null,
    width: ExpressionProperty<Double>? = null,
): Stroke = Stroke(
    Stroke.Properties(
        color = color ?: properties.color,
        style = properties.style,
        unit = unit ?: properties.unit,
        width = width ?: properties.width,
    )
)

@Generated
fun Stroke.asList() = listOf(this)
