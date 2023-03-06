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
 * Gaussian image blur.
 * 
 * Can be created using the method [blur].
 * 
 * Required properties: `type, radius`.
 */
@Generated
class Blur internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Filter {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "blur")
    )

    operator fun plus(additive: Properties): Blur = Blur(
        Properties(
            radius = additive.radius ?: properties.radius,
        )
    )

    class Properties internal constructor(
        /**
         * Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
         */
        val radius: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("radius", radius)
            return result
        }
    }
}

/**
 * @param radius Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
 */
@Generated
fun DivScope.blur(
    `use named arguments`: Guard = Guard.instance,
    radius: Int? = null,
): Blur = Blur(
    Blur.Properties(
        radius = valueOrNull(radius),
    )
)

/**
 * @param radius Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
 */
@Generated
fun DivScope.blurProps(
    `use named arguments`: Guard = Guard.instance,
    radius: Int? = null,
) = Blur.Properties(
    radius = valueOrNull(radius),
)

/**
 * @param radius Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
 */
@Generated
fun TemplateScope.blurRefs(
    `use named arguments`: Guard = Guard.instance,
    radius: ReferenceProperty<Int>? = null,
) = Blur.Properties(
    radius = radius,
)

/**
 * @param radius Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
 */
@Generated
fun Blur.override(
    `use named arguments`: Guard = Guard.instance,
    radius: Int? = null,
): Blur = Blur(
    Blur.Properties(
        radius = valueOrNull(radius) ?: properties.radius,
    )
)

/**
 * @param radius Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
 */
@Generated
fun Blur.defer(
    `use named arguments`: Guard = Guard.instance,
    radius: ReferenceProperty<Int>? = null,
): Blur = Blur(
    Blur.Properties(
        radius = radius ?: properties.radius,
    )
)

/**
 * @param radius Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
 */
@Generated
fun Blur.evaluate(
    `use named arguments`: Guard = Guard.instance,
    radius: ExpressionProperty<Int>? = null,
): Blur = Blur(
    Blur.Properties(
        radius = radius ?: properties.radius,
    )
)

@Generated
fun Blur.asList() = listOf(this)
