@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * A point with fixed coordinates.
 * 
 * Can be created using the method [point].
 * 
 * Required parameters: `y, x`.
 */
@Generated
@JsonSerialize(using = PointSerializer::class)
@ExposedCopyVisibility
data class Point internal constructor(
    val properties: Properties,
) {
    operator fun plus(additive: Properties): Point = Point(
        Properties(
            x = additive.x ?: properties.x,
            y = additive.y ?: properties.y,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * `X` coordinate.
         */
        val x: Property<Dimension>?,
        /**
         * `Y` coordinate.
         */
        val y: Property<Dimension>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("x", x)
            result.tryPutProperty("y", y)
            return result
        }
    }
}

/**
 * @param x `X` coordinate.
 * @param y `Y` coordinate.
 */
@Generated
fun DivScope.point(
    `use named arguments`: Guard = Guard.instance,
    x: Dimension? = null,
    y: Dimension? = null,
): Point = Point(
    Point.Properties(
        x = valueOrNull(x),
        y = valueOrNull(y),
    )
)

/**
 * @param x `X` coordinate.
 * @param y `Y` coordinate.
 */
@Generated
fun DivScope.pointProps(
    `use named arguments`: Guard = Guard.instance,
    x: Dimension? = null,
    y: Dimension? = null,
) = Point.Properties(
    x = valueOrNull(x),
    y = valueOrNull(y),
)

/**
 * @param x `X` coordinate.
 * @param y `Y` coordinate.
 */
@Generated
fun TemplateScope.pointRefs(
    `use named arguments`: Guard = Guard.instance,
    x: ReferenceProperty<Dimension>? = null,
    y: ReferenceProperty<Dimension>? = null,
) = Point.Properties(
    x = x,
    y = y,
)

/**
 * @param x `X` coordinate.
 * @param y `Y` coordinate.
 */
@Generated
fun Point.override(
    `use named arguments`: Guard = Guard.instance,
    x: Dimension? = null,
    y: Dimension? = null,
): Point = Point(
    Point.Properties(
        x = valueOrNull(x) ?: properties.x,
        y = valueOrNull(y) ?: properties.y,
    )
)

/**
 * @param x `X` coordinate.
 * @param y `Y` coordinate.
 */
@Generated
fun Point.defer(
    `use named arguments`: Guard = Guard.instance,
    x: ReferenceProperty<Dimension>? = null,
    y: ReferenceProperty<Dimension>? = null,
): Point = Point(
    Point.Properties(
        x = x ?: properties.x,
        y = y ?: properties.y,
    )
)

/**
 * @param x `X` coordinate.
 * @param y `Y` coordinate.
 */
@Generated
fun Point.modify(
    `use named arguments`: Guard = Guard.instance,
    x: Property<Dimension>? = null,
    y: Property<Dimension>? = null,
): Point = Point(
    Point.Properties(
        x = x ?: properties.x,
        y = y ?: properties.y,
    )
)

@Generated
fun Point.asList() = listOf(this)
