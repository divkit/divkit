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
 * Linear gradient.
 * 
 * Can be created using the method [linearGradient].
 * 
 * Required parameters: `type, colors`.
 */
@Generated
data class LinearGradient internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Background, TextGradient {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "gradient")
    )

    operator fun plus(additive: Properties): LinearGradient = LinearGradient(
        Properties(
            angle = additive.angle ?: properties.angle,
            colors = additive.colors ?: properties.colors,
        )
    )

    data class Properties internal constructor(
        /**
         * Angle of gradient direction.
         * Default value: `0`.
         */
        val angle: Property<Int>?,
        /**
         * Colors. Gradient points are located at an equal distance from each other.
         */
        val colors: Property<List<ArrayElement<Color>>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("angle", angle)
            result.tryPutProperty("colors", colors)
            return result
        }
    }
}

/**
 * @param angle Angle of gradient direction.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun DivScope.linearGradient(
    `use named arguments`: Guard = Guard.instance,
    angle: Int? = null,
    colors: List<ArrayElement<Color>>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = valueOrNull(angle),
        colors = valueOrNull(colors),
    )
)

/**
 * @param angle Angle of gradient direction.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun DivScope.linearGradientProps(
    `use named arguments`: Guard = Guard.instance,
    angle: Int? = null,
    colors: List<ArrayElement<Color>>? = null,
) = LinearGradient.Properties(
    angle = valueOrNull(angle),
    colors = valueOrNull(colors),
)

/**
 * @param angle Angle of gradient direction.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun TemplateScope.linearGradientRefs(
    `use named arguments`: Guard = Guard.instance,
    angle: ReferenceProperty<Int>? = null,
    colors: ReferenceProperty<List<ArrayElement<Color>>>? = null,
) = LinearGradient.Properties(
    angle = angle,
    colors = colors,
)

/**
 * @param angle Angle of gradient direction.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun LinearGradient.override(
    `use named arguments`: Guard = Guard.instance,
    angle: Int? = null,
    colors: List<ArrayElement<Color>>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = valueOrNull(angle) ?: properties.angle,
        colors = valueOrNull(colors) ?: properties.colors,
    )
)

/**
 * @param angle Angle of gradient direction.
 * @param colors Colors. Gradient points are located at an equal distance from each other.
 */
@Generated
fun LinearGradient.defer(
    `use named arguments`: Guard = Guard.instance,
    angle: ReferenceProperty<Int>? = null,
    colors: ReferenceProperty<List<ArrayElement<Color>>>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = angle ?: properties.angle,
        colors = colors ?: properties.colors,
    )
)

/**
 * @param angle Angle of gradient direction.
 */
@Generated
fun LinearGradient.evaluate(
    `use named arguments`: Guard = Guard.instance,
    angle: ExpressionProperty<Int>? = null,
): LinearGradient = LinearGradient(
    LinearGradient.Properties(
        angle = angle ?: properties.angle,
        colors = properties.colors,
    )
)

@Generated
fun LinearGradient.asList() = listOf(this)
