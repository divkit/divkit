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
 * Element shadow.
 * 
 * Can be created using the method [shadow].
 * 
 * Required parameters: `offset`.
 */
@Generated
class Shadow internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Shadow = Shadow(
        Properties(
            alpha = additive.alpha ?: properties.alpha,
            blur = additive.blur ?: properties.blur,
            color = additive.color ?: properties.color,
            offset = additive.offset ?: properties.offset,
        )
    )

    class Properties internal constructor(
        /**
         * Shadow transparency.
         * Default value: `0.19`.
         */
        val alpha: Property<Double>?,
        /**
         * Blur intensity.
         * Default value: `2`.
         */
        val blur: Property<Int>?,
        /**
         * Shadow color.
         * Default value: `#000000`.
         */
        val color: Property<Color>?,
        /**
         * Shadow offset.
         */
        val offset: Property<Point>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("blur", blur)
            result.tryPutProperty("color", color)
            result.tryPutProperty("offset", offset)
            return result
        }
    }
}

/**
 * @param alpha Shadow transparency.
 * @param blur Blur intensity.
 * @param color Shadow color.
 * @param offset Shadow offset.
 */
@Generated
fun DivScope.shadow(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    blur: Int? = null,
    color: Color? = null,
    offset: Point? = null,
): Shadow = Shadow(
    Shadow.Properties(
        alpha = valueOrNull(alpha),
        blur = valueOrNull(blur),
        color = valueOrNull(color),
        offset = valueOrNull(offset),
    )
)

/**
 * @param alpha Shadow transparency.
 * @param blur Blur intensity.
 * @param color Shadow color.
 * @param offset Shadow offset.
 */
@Generated
fun DivScope.shadowProps(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    blur: Int? = null,
    color: Color? = null,
    offset: Point? = null,
) = Shadow.Properties(
    alpha = valueOrNull(alpha),
    blur = valueOrNull(blur),
    color = valueOrNull(color),
    offset = valueOrNull(offset),
)

/**
 * @param alpha Shadow transparency.
 * @param blur Blur intensity.
 * @param color Shadow color.
 * @param offset Shadow offset.
 */
@Generated
fun TemplateScope.shadowRefs(
    `use named arguments`: Guard = Guard.instance,
    alpha: ReferenceProperty<Double>? = null,
    blur: ReferenceProperty<Int>? = null,
    color: ReferenceProperty<Color>? = null,
    offset: ReferenceProperty<Point>? = null,
) = Shadow.Properties(
    alpha = alpha,
    blur = blur,
    color = color,
    offset = offset,
)

/**
 * @param alpha Shadow transparency.
 * @param blur Blur intensity.
 * @param color Shadow color.
 * @param offset Shadow offset.
 */
@Generated
fun Shadow.override(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    blur: Int? = null,
    color: Color? = null,
    offset: Point? = null,
): Shadow = Shadow(
    Shadow.Properties(
        alpha = valueOrNull(alpha) ?: properties.alpha,
        blur = valueOrNull(blur) ?: properties.blur,
        color = valueOrNull(color) ?: properties.color,
        offset = valueOrNull(offset) ?: properties.offset,
    )
)

/**
 * @param alpha Shadow transparency.
 * @param blur Blur intensity.
 * @param color Shadow color.
 * @param offset Shadow offset.
 */
@Generated
fun Shadow.defer(
    `use named arguments`: Guard = Guard.instance,
    alpha: ReferenceProperty<Double>? = null,
    blur: ReferenceProperty<Int>? = null,
    color: ReferenceProperty<Color>? = null,
    offset: ReferenceProperty<Point>? = null,
): Shadow = Shadow(
    Shadow.Properties(
        alpha = alpha ?: properties.alpha,
        blur = blur ?: properties.blur,
        color = color ?: properties.color,
        offset = offset ?: properties.offset,
    )
)

/**
 * @param alpha Shadow transparency.
 * @param blur Blur intensity.
 * @param color Shadow color.
 */
@Generated
fun Shadow.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alpha: ExpressionProperty<Double>? = null,
    blur: ExpressionProperty<Int>? = null,
    color: ExpressionProperty<Color>? = null,
): Shadow = Shadow(
    Shadow.Properties(
        alpha = alpha ?: properties.alpha,
        blur = blur ?: properties.blur,
        color = color ?: properties.color,
        offset = properties.offset,
    )
)

@Generated
fun Shadow.asList() = listOf(this)
