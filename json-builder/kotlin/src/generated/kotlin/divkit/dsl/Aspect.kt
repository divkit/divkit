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
 * Fixed aspect ratio. The element's height is calculated based on the width, ignoring the `height` value.
 * 
 * Can be created using the method [aspect].
 * 
 * Required parameters: `ratio`.
 */
@Generated
data class Aspect internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Aspect = Aspect(
        Properties(
            ratio = additive.ratio ?: properties.ratio,
        )
    )

    data class Properties internal constructor(
        /**
         * `height = width / ratio`.
         */
        val ratio: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("ratio", ratio)
            return result
        }
    }
}

/**
 * @param ratio `height = width / ratio`.
 */
@Generated
fun DivScope.aspect(
    `use named arguments`: Guard = Guard.instance,
    ratio: Double? = null,
): Aspect = Aspect(
    Aspect.Properties(
        ratio = valueOrNull(ratio),
    )
)

/**
 * @param ratio `height = width / ratio`.
 */
@Generated
fun DivScope.aspectProps(
    `use named arguments`: Guard = Guard.instance,
    ratio: Double? = null,
) = Aspect.Properties(
    ratio = valueOrNull(ratio),
)

/**
 * @param ratio `height = width / ratio`.
 */
@Generated
fun TemplateScope.aspectRefs(
    `use named arguments`: Guard = Guard.instance,
    ratio: ReferenceProperty<Double>? = null,
) = Aspect.Properties(
    ratio = ratio,
)

/**
 * @param ratio `height = width / ratio`.
 */
@Generated
fun Aspect.override(
    `use named arguments`: Guard = Guard.instance,
    ratio: Double? = null,
): Aspect = Aspect(
    Aspect.Properties(
        ratio = valueOrNull(ratio) ?: properties.ratio,
    )
)

/**
 * @param ratio `height = width / ratio`.
 */
@Generated
fun Aspect.defer(
    `use named arguments`: Guard = Guard.instance,
    ratio: ReferenceProperty<Double>? = null,
): Aspect = Aspect(
    Aspect.Properties(
        ratio = ratio ?: properties.ratio,
    )
)

/**
 * @param ratio `height = width / ratio`.
 */
@Generated
fun Aspect.modify(
    `use named arguments`: Guard = Guard.instance,
    ratio: Property<Double>? = null,
): Aspect = Aspect(
    Aspect.Properties(
        ratio = ratio ?: properties.ratio,
    )
)

/**
 * @param ratio `height = width / ratio`.
 */
@Generated
fun Aspect.evaluate(
    `use named arguments`: Guard = Guard.instance,
    ratio: ExpressionProperty<Double>? = null,
): Aspect = Aspect(
    Aspect.Properties(
        ratio = ratio ?: properties.ratio,
    )
)

@Generated
fun Aspect.asList() = listOf(this)
