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
 * Launches the specified animator.
 * 
 * Can be created using the method [actionAnimatorStart].
 * 
 * Required parameters: `type, animator_id`.
 */
@Generated
data class ActionAnimatorStart internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "animator_start")
    )

    operator fun plus(additive: Properties): ActionAnimatorStart = ActionAnimatorStart(
        Properties(
            animatorId = additive.animatorId ?: properties.animatorId,
            direction = additive.direction ?: properties.direction,
            duration = additive.duration ?: properties.duration,
            endValue = additive.endValue ?: properties.endValue,
            interpolator = additive.interpolator ?: properties.interpolator,
            repeatCount = additive.repeatCount ?: properties.repeatCount,
            startDelay = additive.startDelay ?: properties.startDelay,
            startValue = additive.startValue ?: properties.startValue,
        )
    )

    data class Properties internal constructor(
        /**
         * ID of the animator launched.
         */
        val animatorId: Property<String>?,
        /**
         * Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
         */
        val direction: Property<AnimationDirection>?,
        /**
         * Animation duration in milliseconds.
         */
        val duration: Property<Long>?,
        /**
         * Overrides the value that will be set after the animation finishes.
         */
        val endValue: Property<TypedValue>?,
        /**
         * Animated value interpolation function.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
         */
        val repeatCount: Property<Count>?,
        /**
         * Delay before the animation is launched in milliseconds.
         */
        val startDelay: Property<Int>?,
        /**
         * Overrides the value that will be set before the animation begins.
         */
        val startValue: Property<TypedValue>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("animator_id", animatorId)
            result.tryPutProperty("direction", direction)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("end_value", endValue)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("repeat_count", repeatCount)
            result.tryPutProperty("start_delay", startDelay)
            result.tryPutProperty("start_value", startValue)
            return result
        }
    }
}

/**
 * @param animatorId ID of the animator launched.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides the value that will be set after the animation finishes.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue Overrides the value that will be set before the animation begins.
 */
@Generated
fun DivScope.actionAnimatorStart(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endValue: TypedValue? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Count? = null,
    startDelay: Int? = null,
    startValue: TypedValue? = null,
): ActionAnimatorStart = ActionAnimatorStart(
    ActionAnimatorStart.Properties(
        animatorId = valueOrNull(animatorId),
        direction = valueOrNull(direction),
        duration = valueOrNull(duration),
        endValue = valueOrNull(endValue),
        interpolator = valueOrNull(interpolator),
        repeatCount = valueOrNull(repeatCount),
        startDelay = valueOrNull(startDelay),
        startValue = valueOrNull(startValue),
    )
)

/**
 * @param animatorId ID of the animator launched.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides the value that will be set after the animation finishes.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue Overrides the value that will be set before the animation begins.
 */
@Generated
fun DivScope.actionAnimatorStartProps(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endValue: TypedValue? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Count? = null,
    startDelay: Int? = null,
    startValue: TypedValue? = null,
) = ActionAnimatorStart.Properties(
    animatorId = valueOrNull(animatorId),
    direction = valueOrNull(direction),
    duration = valueOrNull(duration),
    endValue = valueOrNull(endValue),
    interpolator = valueOrNull(interpolator),
    repeatCount = valueOrNull(repeatCount),
    startDelay = valueOrNull(startDelay),
    startValue = valueOrNull(startValue),
)

/**
 * @param animatorId ID of the animator launched.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides the value that will be set after the animation finishes.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue Overrides the value that will be set before the animation begins.
 */
@Generated
fun TemplateScope.actionAnimatorStartRefs(
    `use named arguments`: Guard = Guard.instance,
    animatorId: ReferenceProperty<String>? = null,
    direction: ReferenceProperty<AnimationDirection>? = null,
    duration: ReferenceProperty<Long>? = null,
    endValue: ReferenceProperty<TypedValue>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    repeatCount: ReferenceProperty<Count>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    startValue: ReferenceProperty<TypedValue>? = null,
) = ActionAnimatorStart.Properties(
    animatorId = animatorId,
    direction = direction,
    duration = duration,
    endValue = endValue,
    interpolator = interpolator,
    repeatCount = repeatCount,
    startDelay = startDelay,
    startValue = startValue,
)

/**
 * @param animatorId ID of the animator launched.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides the value that will be set after the animation finishes.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue Overrides the value that will be set before the animation begins.
 */
@Generated
fun ActionAnimatorStart.override(
    `use named arguments`: Guard = Guard.instance,
    animatorId: String? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endValue: TypedValue? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Count? = null,
    startDelay: Int? = null,
    startValue: TypedValue? = null,
): ActionAnimatorStart = ActionAnimatorStart(
    ActionAnimatorStart.Properties(
        animatorId = valueOrNull(animatorId) ?: properties.animatorId,
        direction = valueOrNull(direction) ?: properties.direction,
        duration = valueOrNull(duration) ?: properties.duration,
        endValue = valueOrNull(endValue) ?: properties.endValue,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        repeatCount = valueOrNull(repeatCount) ?: properties.repeatCount,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
        startValue = valueOrNull(startValue) ?: properties.startValue,
    )
)

/**
 * @param animatorId ID of the animator launched.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides the value that will be set after the animation finishes.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue Overrides the value that will be set before the animation begins.
 */
@Generated
fun ActionAnimatorStart.defer(
    `use named arguments`: Guard = Guard.instance,
    animatorId: ReferenceProperty<String>? = null,
    direction: ReferenceProperty<AnimationDirection>? = null,
    duration: ReferenceProperty<Long>? = null,
    endValue: ReferenceProperty<TypedValue>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    repeatCount: ReferenceProperty<Count>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    startValue: ReferenceProperty<TypedValue>? = null,
): ActionAnimatorStart = ActionAnimatorStart(
    ActionAnimatorStart.Properties(
        animatorId = animatorId ?: properties.animatorId,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endValue = endValue ?: properties.endValue,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = repeatCount ?: properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        startValue = startValue ?: properties.startValue,
    )
)

/**
 * @param animatorId ID of the animator launched.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides the value that will be set after the animation finishes.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue Overrides the value that will be set before the animation begins.
 */
@Generated
fun ActionAnimatorStart.modify(
    `use named arguments`: Guard = Guard.instance,
    animatorId: Property<String>? = null,
    direction: Property<AnimationDirection>? = null,
    duration: Property<Long>? = null,
    endValue: Property<TypedValue>? = null,
    interpolator: Property<AnimationInterpolator>? = null,
    repeatCount: Property<Count>? = null,
    startDelay: Property<Int>? = null,
    startValue: Property<TypedValue>? = null,
): ActionAnimatorStart = ActionAnimatorStart(
    ActionAnimatorStart.Properties(
        animatorId = animatorId ?: properties.animatorId,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endValue = endValue ?: properties.endValue,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = repeatCount ?: properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        startValue = startValue ?: properties.startValue,
    )
)

/**
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Animated value interpolation function.
 * @param startDelay Delay before the animation is launched in milliseconds.
 */
@Generated
fun ActionAnimatorStart.evaluate(
    `use named arguments`: Guard = Guard.instance,
    direction: ExpressionProperty<AnimationDirection>? = null,
    duration: ExpressionProperty<Long>? = null,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    startDelay: ExpressionProperty<Int>? = null,
): ActionAnimatorStart = ActionAnimatorStart(
    ActionAnimatorStart.Properties(
        animatorId = properties.animatorId,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endValue = properties.endValue,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        startValue = properties.startValue,
    )
)

@Generated
fun ActionAnimatorStart.asList() = listOf(this)
