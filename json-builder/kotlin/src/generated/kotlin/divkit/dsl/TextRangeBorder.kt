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
 * Character range border.
 * 
 * Can be created using the method [textRangeBorder].
 */
@Generated
data class TextRangeBorder internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): TextRangeBorder = TextRangeBorder(
        Properties(
            cornerRadius = additive.cornerRadius ?: properties.cornerRadius,
            stroke = additive.stroke ?: properties.stroke,
        )
    )

    data class Properties internal constructor(
        /**
         * One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
         */
        val cornerRadius: Property<Int>?,
        /**
         * Stroke style.
         */
        val stroke: Property<Stroke>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("corner_radius", cornerRadius)
            result.tryPutProperty("stroke", stroke)
            return result
        }
    }
}

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.textRangeBorder(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: Int? = null,
    stroke: Stroke? = null,
): TextRangeBorder = TextRangeBorder(
    TextRangeBorder.Properties(
        cornerRadius = valueOrNull(cornerRadius),
        stroke = valueOrNull(stroke),
    )
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param stroke Stroke style.
 */
@Generated
fun DivScope.textRangeBorderProps(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: Int? = null,
    stroke: Stroke? = null,
) = TextRangeBorder.Properties(
    cornerRadius = valueOrNull(cornerRadius),
    stroke = valueOrNull(stroke),
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param stroke Stroke style.
 */
@Generated
fun TemplateScope.textRangeBorderRefs(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: ReferenceProperty<Int>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
) = TextRangeBorder.Properties(
    cornerRadius = cornerRadius,
    stroke = stroke,
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param stroke Stroke style.
 */
@Generated
fun TextRangeBorder.override(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: Int? = null,
    stroke: Stroke? = null,
): TextRangeBorder = TextRangeBorder(
    TextRangeBorder.Properties(
        cornerRadius = valueOrNull(cornerRadius) ?: properties.cornerRadius,
        stroke = valueOrNull(stroke) ?: properties.stroke,
    )
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 * @param stroke Stroke style.
 */
@Generated
fun TextRangeBorder.defer(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: ReferenceProperty<Int>? = null,
    stroke: ReferenceProperty<Stroke>? = null,
): TextRangeBorder = TextRangeBorder(
    TextRangeBorder.Properties(
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        stroke = stroke ?: properties.stroke,
    )
)

/**
 * @param cornerRadius One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
 */
@Generated
fun TextRangeBorder.evaluate(
    `use named arguments`: Guard = Guard.instance,
    cornerRadius: ExpressionProperty<Int>? = null,
): TextRangeBorder = TextRangeBorder(
    TextRangeBorder.Properties(
        cornerRadius = cornerRadius ?: properties.cornerRadius,
        stroke = properties.stroke,
    )
)

@Generated
fun TextRangeBorder.asList() = listOf(this)
