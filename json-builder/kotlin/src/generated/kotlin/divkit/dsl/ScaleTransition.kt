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
 * Scale animation.
 * 
 * Can be created using the method [scaleTransition].
 * 
 * Required parameters: `type`.
 */
@Generated
data class ScaleTransition internal constructor(
    @JsonIgnore
    val properties: Properties,
) : AppearanceTransition {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "scale")
    )

    operator fun plus(additive: Properties): ScaleTransition = ScaleTransition(
        Properties(
            duration = additive.duration ?: properties.duration,
            interpolator = additive.interpolator ?: properties.interpolator,
            pivotX = additive.pivotX ?: properties.pivotX,
            pivotY = additive.pivotY ?: properties.pivotY,
            scale = additive.scale ?: properties.scale,
            startDelay = additive.startDelay ?: properties.startDelay,
        )
    )

    data class Properties internal constructor(
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
         * Relative coordinate `X` of the point that won't change its position in case of scaling.
         * Default value: `0.5`.
         */
        val pivotX: Property<Double>?,
        /**
         * Relative coordinate `Y` of the point that won't change its position in case of scaling.
         * Default value: `0.5`.
         */
        val pivotY: Property<Double>?,
        /**
         * Value of the scale  from which the element starts appearing or at which it finishes disappearing.
         * Default value: `0.0`.
         */
        val scale: Property<Double>?,
        /**
         * Delay in milliseconds before animation starts.
         * Default value: `0`.
         */
        val startDelay: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("pivot_x", pivotX)
            result.tryPutProperty("pivot_y", pivotY)
            result.tryPutProperty("scale", scale)
            result.tryPutProperty("start_delay", startDelay)
            return result
        }
    }
}

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param pivotX Relative coordinate `X` of the point that won't change its position in case of scaling.
 * @param pivotY Relative coordinate `Y` of the point that won't change its position in case of scaling.
 * @param scale Value of the scale  from which the element starts appearing or at which it finishes disappearing.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.scaleTransition(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    pivotX: Double? = null,
    pivotY: Double? = null,
    scale: Double? = null,
    startDelay: Int? = null,
): ScaleTransition = ScaleTransition(
    ScaleTransition.Properties(
        duration = valueOrNull(duration),
        interpolator = valueOrNull(interpolator),
        pivotX = valueOrNull(pivotX),
        pivotY = valueOrNull(pivotY),
        scale = valueOrNull(scale),
        startDelay = valueOrNull(startDelay),
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param pivotX Relative coordinate `X` of the point that won't change its position in case of scaling.
 * @param pivotY Relative coordinate `Y` of the point that won't change its position in case of scaling.
 * @param scale Value of the scale  from which the element starts appearing or at which it finishes disappearing.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.scaleTransitionProps(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    pivotX: Double? = null,
    pivotY: Double? = null,
    scale: Double? = null,
    startDelay: Int? = null,
) = ScaleTransition.Properties(
    duration = valueOrNull(duration),
    interpolator = valueOrNull(interpolator),
    pivotX = valueOrNull(pivotX),
    pivotY = valueOrNull(pivotY),
    scale = valueOrNull(scale),
    startDelay = valueOrNull(startDelay),
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param pivotX Relative coordinate `X` of the point that won't change its position in case of scaling.
 * @param pivotY Relative coordinate `Y` of the point that won't change its position in case of scaling.
 * @param scale Value of the scale  from which the element starts appearing or at which it finishes disappearing.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun TemplateScope.scaleTransitionRefs(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    pivotX: ReferenceProperty<Double>? = null,
    pivotY: ReferenceProperty<Double>? = null,
    scale: ReferenceProperty<Double>? = null,
    startDelay: ReferenceProperty<Int>? = null,
) = ScaleTransition.Properties(
    duration = duration,
    interpolator = interpolator,
    pivotX = pivotX,
    pivotY = pivotY,
    scale = scale,
    startDelay = startDelay,
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param pivotX Relative coordinate `X` of the point that won't change its position in case of scaling.
 * @param pivotY Relative coordinate `Y` of the point that won't change its position in case of scaling.
 * @param scale Value of the scale  from which the element starts appearing or at which it finishes disappearing.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun ScaleTransition.override(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    pivotX: Double? = null,
    pivotY: Double? = null,
    scale: Double? = null,
    startDelay: Int? = null,
): ScaleTransition = ScaleTransition(
    ScaleTransition.Properties(
        duration = valueOrNull(duration) ?: properties.duration,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        pivotX = valueOrNull(pivotX) ?: properties.pivotX,
        pivotY = valueOrNull(pivotY) ?: properties.pivotY,
        scale = valueOrNull(scale) ?: properties.scale,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param pivotX Relative coordinate `X` of the point that won't change its position in case of scaling.
 * @param pivotY Relative coordinate `Y` of the point that won't change its position in case of scaling.
 * @param scale Value of the scale  from which the element starts appearing or at which it finishes disappearing.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun ScaleTransition.defer(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    pivotX: ReferenceProperty<Double>? = null,
    pivotY: ReferenceProperty<Double>? = null,
    scale: ReferenceProperty<Double>? = null,
    startDelay: ReferenceProperty<Int>? = null,
): ScaleTransition = ScaleTransition(
    ScaleTransition.Properties(
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        pivotX = pivotX ?: properties.pivotX,
        pivotY = pivotY ?: properties.pivotY,
        scale = scale ?: properties.scale,
        startDelay = startDelay ?: properties.startDelay,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param pivotX Relative coordinate `X` of the point that won't change its position in case of scaling.
 * @param pivotY Relative coordinate `Y` of the point that won't change its position in case of scaling.
 * @param scale Value of the scale  from which the element starts appearing or at which it finishes disappearing.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun ScaleTransition.evaluate(
    `use named arguments`: Guard = Guard.instance,
    duration: ExpressionProperty<Int>? = null,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    pivotX: ExpressionProperty<Double>? = null,
    pivotY: ExpressionProperty<Double>? = null,
    scale: ExpressionProperty<Double>? = null,
    startDelay: ExpressionProperty<Int>? = null,
): ScaleTransition = ScaleTransition(
    ScaleTransition.Properties(
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        pivotX = pivotX ?: properties.pivotX,
        pivotY = pivotY ?: properties.pivotY,
        scale = scale ?: properties.scale,
        startDelay = startDelay ?: properties.startDelay,
    )
)

@Generated
fun ScaleTransition.asList() = listOf(this)
