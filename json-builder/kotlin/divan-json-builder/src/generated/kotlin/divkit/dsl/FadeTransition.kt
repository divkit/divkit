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
 * Transparency animation.
 * 
 * Can be created using the method [fadeTransition].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class FadeTransition internal constructor(
    @JsonIgnore
    val properties: Properties,
) : AppearanceTransition {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "fade")
    )

    operator fun plus(additive: Properties): FadeTransition = FadeTransition(
        Properties(
            alpha = additive.alpha ?: properties.alpha,
            duration = additive.duration ?: properties.duration,
            interpolator = additive.interpolator ?: properties.interpolator,
            startDelay = additive.startDelay ?: properties.startDelay,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
         * Default value: `0.0`.
         */
        val alpha: Property<Double>?,
        /**
         * Animation duration in milliseconds.
         * Default value: `200`.
         */
        val duration: Property<Int>?,
        /**
         * Transition speed nature.
         * Default value: `ease_in_out`.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * Delay in milliseconds before animation starts.
         * Default value: `0`.
         */
        val startDelay: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("start_delay", startDelay)
            return result
        }
    }
}

/**
 * @param alpha Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.fadeTransition(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
): FadeTransition = FadeTransition(
    FadeTransition.Properties(
        alpha = valueOrNull(alpha),
        duration = valueOrNull(duration),
        interpolator = valueOrNull(interpolator),
        startDelay = valueOrNull(startDelay),
    )
)

/**
 * @param alpha Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.fadeTransitionProps(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
) = FadeTransition.Properties(
    alpha = valueOrNull(alpha),
    duration = valueOrNull(duration),
    interpolator = valueOrNull(interpolator),
    startDelay = valueOrNull(startDelay),
)

/**
 * @param alpha Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun TemplateScope.fadeTransitionRefs(
    `use named arguments`: Guard = Guard.instance,
    alpha: ReferenceProperty<Double>? = null,
    duration: ReferenceProperty<Int>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    startDelay: ReferenceProperty<Int>? = null,
) = FadeTransition.Properties(
    alpha = alpha,
    duration = duration,
    interpolator = interpolator,
    startDelay = startDelay,
)

/**
 * @param alpha Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun FadeTransition.override(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
): FadeTransition = FadeTransition(
    FadeTransition.Properties(
        alpha = valueOrNull(alpha) ?: properties.alpha,
        duration = valueOrNull(duration) ?: properties.duration,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
    )
)

/**
 * @param alpha Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun FadeTransition.defer(
    `use named arguments`: Guard = Guard.instance,
    alpha: ReferenceProperty<Double>? = null,
    duration: ReferenceProperty<Int>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    startDelay: ReferenceProperty<Int>? = null,
): FadeTransition = FadeTransition(
    FadeTransition.Properties(
        alpha = alpha ?: properties.alpha,
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

/**
 * @param alpha Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun FadeTransition.modify(
    `use named arguments`: Guard = Guard.instance,
    alpha: Property<Double>? = null,
    duration: Property<Int>? = null,
    interpolator: Property<AnimationInterpolator>? = null,
    startDelay: Property<Int>? = null,
): FadeTransition = FadeTransition(
    FadeTransition.Properties(
        alpha = alpha ?: properties.alpha,
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

/**
 * @param alpha Value of the alpha channel which the element starts appearing from or at which it finishes disappearing.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun FadeTransition.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alpha: ExpressionProperty<Double>? = null,
    duration: ExpressionProperty<Int>? = null,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    startDelay: ExpressionProperty<Int>? = null,
): FadeTransition = FadeTransition(
    FadeTransition.Properties(
        alpha = alpha ?: properties.alpha,
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

@Generated
fun FadeTransition.asList() = listOf(this)
