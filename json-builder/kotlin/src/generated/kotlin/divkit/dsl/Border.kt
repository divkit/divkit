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
 * Stroke around the element.
 * 
 * Can be created using the method [border].
 */
@Generated
class Border internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Border = Border(
        Properties(
            cornerRadius = additive.cornerRadius ?: properties.cornerRadius,
            cornersRadius = additive.cornersRadius ?: properties.cornersRadius,
            hasShadow = additive.hasShadow ?: properties.hasShadow,
            shadow = additive.shadow ?: properties.shadow,
            stroke = additive.stroke ?: properties.stroke,
        )
    )

    class Properties internal constructor(
        /**
         * One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
         */
        val cornerRadius: Property<Int>?,
        /**
         * Multiple radii of element and stroke corner rounding.
         */
        val cornersRadius: Property<CornersRadius>?,
        /**
         * Adding shadow.
         * Default value: `false`.
         */
        val hasShadow: Property<Boolean>?,
        /**
         * Parameters of the shadow applied to the element stroke.
         */
        val shadow: Property<Shadow>?,
        /**
         * Stroke style.
         */
        val stroke: Property<Stroke>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("corner_radius", cornerRadius)
            result.tryPutProperty("corners_radius", cornersRadius)
            result.tryPutProperty("has_shadow", hasShadow)
            result.tryPutProperty("shadow", shadow)
            result.tryPutProperty("stroke", stroke)
            return result
        }
    }
}

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param cornersRadius Multiple radii of element and stroke corner rounding.
 * @param hasShadow Adding shadow.
 * @param shadow Parameters of the shadow applied to the element stroke.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.border(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: Int? = null,
    cornersRadius: CornersRadius? = null,
    hasShadow: Boolean? = null,
    shadow: Shadow? = null,
    stroke: Stroke? = null,
): Border = Border(
    Border.Properties(
        cornerRadius = valueOrNull(cornerRadius),
        cornersRadius = valueOrNull(cornersRadius),
        hasShadow = valueOrNull(hasShadow),
        shadow = valueOrNull(shadow),
        stroke = valueOrNull(stroke),
    )
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param cornersRadius Multiple radii of element and stroke corner rounding.
 * @param hasShadow Adding shadow.
 * @param shadow Parameters of the shadow applied to the element stroke.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.borderProps(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: Int? = null,
    cornersRadius: CornersRadius? = null,
    hasShadow: Boolean? = null,
    shadow: Shadow? = null,
    stroke: Stroke? = null,
) = Border.Properties(
    cornerRadius = valueOrNull(cornerRadius),
    cornersRadius = valueOrNull(cornersRadius),
    hasShadow = valueOrNull(hasShadow),
    shadow = valueOrNull(shadow),
    stroke = valueOrNull(stroke),
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param cornersRadius Multiple radii of element and stroke corner rounding.
 * @param hasShadow Adding shadow.
 * @param shadow Parameters of the shadow applied to the element stroke.
 * @param stroke Stroke style.
 */
@Generated
fun TemplateScope.borderRefs(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: ReferenceProperty<Int>? = null,
    cornersRadius: ReferenceProperty<CornersRadius>? = null,
    hasShadow: ReferenceProperty<Boolean>? = null,
    shadow: ReferenceProperty<Shadow>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
) = Border.Properties(
    cornerRadius = cornerRadius,
    cornersRadius = cornersRadius,
    hasShadow = hasShadow,
    shadow = shadow,
    stroke = stroke,
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param cornersRadius Multiple radii of element and stroke corner rounding.
 * @param hasShadow Adding shadow.
 * @param shadow Parameters of the shadow applied to the element stroke.
 * @param stroke Stroke style.
 */
@Generated
fun Border.override(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: Int? = null,
    cornersRadius: CornersRadius? = null,
    hasShadow: Boolean? = null,
    shadow: Shadow? = null,
    stroke: Stroke? = null,
): Border = Border(
    Border.Properties(
        cornerRadius = valueOrNull(cornerRadius) ?: properties.cornerRadius,
        cornersRadius = valueOrNull(cornersRadius) ?: properties.cornersRadius,
        hasShadow = valueOrNull(hasShadow) ?: properties.hasShadow,
        shadow = valueOrNull(shadow) ?: properties.shadow,
        stroke = valueOrNull(stroke) ?: properties.stroke,
    )
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param cornersRadius Multiple radii of element and stroke corner rounding.
 * @param hasShadow Adding shadow.
 * @param shadow Parameters of the shadow applied to the element stroke.
 * @param stroke Stroke style.
 */
@Generated
fun Border.defer(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: ReferenceProperty<Int>? = null,
    cornersRadius: ReferenceProperty<CornersRadius>? = null,
    hasShadow: ReferenceProperty<Boolean>? = null,
    shadow: ReferenceProperty<Shadow>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
): Border = Border(
    Border.Properties(
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        cornersRadius = cornersRadius ?: properties.cornersRadius,
        hasShadow = hasShadow ?: properties.hasShadow,
        shadow = shadow ?: properties.shadow,
        stroke = stroke ?: properties.stroke,
    )
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param hasShadow Adding shadow.
 */
@Generated
fun Border.evaluate(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: ExpressionProperty<Int>? = null,
    hasShadow: ExpressionProperty<Boolean>? = null,
): Border = Border(
    Border.Properties(
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        cornersRadius = properties.cornersRadius,
        hasShadow = hasShadow ?: properties.hasShadow,
        shadow = properties.shadow,
        stroke = properties.stroke,
    )
)

@Generated
fun Border.asList() = listOf(this)
