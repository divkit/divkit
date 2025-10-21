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
 * Sets corner rounding.
 * 
 * Can be created using the method [cornersRadius].
 */
@Generated
@ExposedCopyVisibility
data class CornersRadius internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): CornersRadius = CornersRadius(
        Properties(
            bottomLeft = additive.bottomLeft ?: properties.bottomLeft,
            bottomRight = additive.bottomRight ?: properties.bottomRight,
            topLeft = additive.topLeft ?: properties.topLeft,
            topRight = additive.topRight ?: properties.topRight,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
         */
        val bottomLeft: Property<Int>?,
        /**
         * Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
         */
        val bottomRight: Property<Int>?,
        /**
         * Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
         */
        val topLeft: Property<Int>?,
        /**
         * Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
         */
        val topRight: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("bottom-left", bottomLeft)
            result.tryPutProperty("bottom-right", bottomRight)
            result.tryPutProperty("top-left", topLeft)
            result.tryPutProperty("top-right", topRight)
            return result
        }
    }
}

/**
 * @param bottomLeft Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
 * @param bottomRight Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
 * @param topLeft Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
 * @param topRight Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
 */
@Generated
fun DivScope.cornersRadius(
    `use named arguments`: Guard = Guard.instance,
    bottomLeft: Int? = null,
    bottomRight: Int? = null,
    topLeft: Int? = null,
    topRight: Int? = null,
): CornersRadius = CornersRadius(
    CornersRadius.Properties(
        bottomLeft = valueOrNull(bottomLeft),
        bottomRight = valueOrNull(bottomRight),
        topLeft = valueOrNull(topLeft),
        topRight = valueOrNull(topRight),
    )
)

/**
 * @param bottomLeft Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
 * @param bottomRight Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
 * @param topLeft Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
 * @param topRight Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
 */
@Generated
fun DivScope.cornersRadiusProps(
    `use named arguments`: Guard = Guard.instance,
    bottomLeft: Int? = null,
    bottomRight: Int? = null,
    topLeft: Int? = null,
    topRight: Int? = null,
) = CornersRadius.Properties(
    bottomLeft = valueOrNull(bottomLeft),
    bottomRight = valueOrNull(bottomRight),
    topLeft = valueOrNull(topLeft),
    topRight = valueOrNull(topRight),
)

/**
 * @param bottomLeft Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
 * @param bottomRight Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
 * @param topLeft Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
 * @param topRight Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
 */
@Generated
fun TemplateScope.cornersRadiusRefs(
    `use named arguments`: Guard = Guard.instance,
    bottomLeft: ReferenceProperty<Int>? = null,
    bottomRight: ReferenceProperty<Int>? = null,
    topLeft: ReferenceProperty<Int>? = null,
    topRight: ReferenceProperty<Int>? = null,
) = CornersRadius.Properties(
    bottomLeft = bottomLeft,
    bottomRight = bottomRight,
    topLeft = topLeft,
    topRight = topRight,
)

/**
 * @param bottomLeft Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
 * @param bottomRight Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
 * @param topLeft Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
 * @param topRight Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
 */
@Generated
fun CornersRadius.override(
    `use named arguments`: Guard = Guard.instance,
    bottomLeft: Int? = null,
    bottomRight: Int? = null,
    topLeft: Int? = null,
    topRight: Int? = null,
): CornersRadius = CornersRadius(
    CornersRadius.Properties(
        bottomLeft = valueOrNull(bottomLeft) ?: properties.bottomLeft,
        bottomRight = valueOrNull(bottomRight) ?: properties.bottomRight,
        topLeft = valueOrNull(topLeft) ?: properties.topLeft,
        topRight = valueOrNull(topRight) ?: properties.topRight,
    )
)

/**
 * @param bottomLeft Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
 * @param bottomRight Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
 * @param topLeft Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
 * @param topRight Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
 */
@Generated
fun CornersRadius.defer(
    `use named arguments`: Guard = Guard.instance,
    bottomLeft: ReferenceProperty<Int>? = null,
    bottomRight: ReferenceProperty<Int>? = null,
    topLeft: ReferenceProperty<Int>? = null,
    topRight: ReferenceProperty<Int>? = null,
): CornersRadius = CornersRadius(
    CornersRadius.Properties(
        bottomLeft = bottomLeft ?: properties.bottomLeft,
        bottomRight = bottomRight ?: properties.bottomRight,
        topLeft = topLeft ?: properties.topLeft,
        topRight = topRight ?: properties.topRight,
    )
)

/**
 * @param bottomLeft Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
 * @param bottomRight Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
 * @param topLeft Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
 * @param topRight Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
 */
@Generated
fun CornersRadius.modify(
    `use named arguments`: Guard = Guard.instance,
    bottomLeft: Property<Int>? = null,
    bottomRight: Property<Int>? = null,
    topLeft: Property<Int>? = null,
    topRight: Property<Int>? = null,
): CornersRadius = CornersRadius(
    CornersRadius.Properties(
        bottomLeft = bottomLeft ?: properties.bottomLeft,
        bottomRight = bottomRight ?: properties.bottomRight,
        topLeft = topLeft ?: properties.topLeft,
        topRight = topRight ?: properties.topRight,
    )
)

/**
 * @param bottomLeft Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
 * @param bottomRight Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
 * @param topLeft Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
 * @param topRight Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
 */
@Generated
fun CornersRadius.evaluate(
    `use named arguments`: Guard = Guard.instance,
    bottomLeft: ExpressionProperty<Int>? = null,
    bottomRight: ExpressionProperty<Int>? = null,
    topLeft: ExpressionProperty<Int>? = null,
    topRight: ExpressionProperty<Int>? = null,
): CornersRadius = CornersRadius(
    CornersRadius.Properties(
        bottomLeft = bottomLeft ?: properties.bottomLeft,
        bottomRight = bottomRight ?: properties.bottomRight,
        topLeft = topLeft ?: properties.topLeft,
        topRight = topRight ?: properties.topRight,
    )
)

@Generated
fun CornersRadius.asList() = listOf(this)
