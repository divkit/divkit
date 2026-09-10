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
 * An animated gradient applied to text glyphs. The nested static gradient moves indefinitely at a linear speed; when `duration` is `0` or global animations are disabled, it remains visible without movement.
 * 
 * Can be created using the method [animatedTextGradient].
 * 
 * Required parameters: `type, gradient`.
 */
@Generated
@ExposedCopyVisibility
data class AnimatedTextGradient internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TextGradient {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "animated")
    )

    operator fun plus(additive: Properties): AnimatedTextGradient = AnimatedTextGradient(
        Properties(
            duration = additive.duration ?: properties.duration,
            gradient = additive.gradient ?: properties.gradient,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
         * Default value: `1600`.
         */
        val duration: Property<Int>?,
        /**
         * Static linear or radial gradient moved by the animation.
         */
        val gradient: Property<StaticTextGradient>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("gradient", gradient)
            return result
        }
    }
}

/**
 * @param duration Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
 * @param gradient Static linear or radial gradient moved by the animation.
 */
@Generated
fun DivScope.animatedTextGradient(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    gradient: StaticTextGradient? = null,
): AnimatedTextGradient = AnimatedTextGradient(
    AnimatedTextGradient.Properties(
        duration = valueOrNull(duration),
        gradient = valueOrNull(gradient),
    )
)

/**
 * @param duration Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
 * @param gradient Static linear or radial gradient moved by the animation.
 */
@Generated
fun DivScope.animatedTextGradientProps(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    gradient: StaticTextGradient? = null,
) = AnimatedTextGradient.Properties(
    duration = valueOrNull(duration),
    gradient = valueOrNull(gradient),
)

/**
 * @param duration Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
 * @param gradient Static linear or radial gradient moved by the animation.
 */
@Generated
fun TemplateScope.animatedTextGradientRefs(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    gradient: ReferenceProperty<StaticTextGradient>? = null,
) = AnimatedTextGradient.Properties(
    duration = duration,
    gradient = gradient,
)

/**
 * @param duration Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
 * @param gradient Static linear or radial gradient moved by the animation.
 */
@Generated
fun AnimatedTextGradient.override(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    gradient: StaticTextGradient? = null,
): AnimatedTextGradient = AnimatedTextGradient(
    AnimatedTextGradient.Properties(
        duration = valueOrNull(duration) ?: properties.duration,
        gradient = valueOrNull(gradient) ?: properties.gradient,
    )
)

/**
 * @param duration Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
 * @param gradient Static linear or radial gradient moved by the animation.
 */
@Generated
fun AnimatedTextGradient.defer(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    gradient: ReferenceProperty<StaticTextGradient>? = null,
): AnimatedTextGradient = AnimatedTextGradient(
    AnimatedTextGradient.Properties(
        duration = duration ?: properties.duration,
        gradient = gradient ?: properties.gradient,
    )
)

/**
 * @param duration Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
 * @param gradient Static linear or radial gradient moved by the animation.
 */
@Generated
fun AnimatedTextGradient.modify(
    `use named arguments`: Guard = Guard.instance,
    duration: Property<Int>? = null,
    gradient: Property<StaticTextGradient>? = null,
): AnimatedTextGradient = AnimatedTextGradient(
    AnimatedTextGradient.Properties(
        duration = duration ?: properties.duration,
        gradient = gradient ?: properties.gradient,
    )
)

/**
 * @param duration Duration of one linear animation cycle in milliseconds. A value of `0` keeps the gradient visible without movement.
 */
@Generated
fun AnimatedTextGradient.evaluate(
    `use named arguments`: Guard = Guard.instance,
    duration: ExpressionProperty<Int>? = null,
): AnimatedTextGradient = AnimatedTextGradient(
    AnimatedTextGradient.Properties(
        duration = duration ?: properties.duration,
        gradient = properties.gradient,
    )
)

@Generated
fun AnimatedTextGradient.asList() = listOf(this)
