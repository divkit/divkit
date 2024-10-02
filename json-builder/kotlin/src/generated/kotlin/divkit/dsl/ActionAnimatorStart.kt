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
 * Starts specified animator
 * 
 * Can be created using the method [actionAnimatorStart].
 * 
 * Required parameters: `type, animator_id`.
 */
@Generated
class ActionAnimatorStart internal constructor(
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

    class Properties internal constructor(
        /**
         * The identifier of the animator being started.
         */
        val animatorId: Property<String>?,
        /**
         * Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
         */
        val direction: Property<AnimationDirection>?,
        /**
         * Animation duration in milliseconds.
         */
        val duration: Property<Long>?,
        /**
         * Overrides value that will be set at the end of animation.
         */
        val endValue: Property<TypedValue>?,
        /**
         * Interpolation function.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * The number of times the animation will repeat before it finishes.
         */
        val repeatCount: Property<Count>?,
        /**
         * Animation start delay in milliseconds.
         */
        val startDelay: Property<Int>?,
        /**
         * Overrides value that will be set at the start of animation.
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
 * @param animatorId The identifier of the animator being started.
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides value that will be set at the end of animation.
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes.
 * @param startDelay Animation start delay in milliseconds.
 * @param startValue Overrides value that will be set at the start of animation.
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
 * @param animatorId The identifier of the animator being started.
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides value that will be set at the end of animation.
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes.
 * @param startDelay Animation start delay in milliseconds.
 * @param startValue Overrides value that will be set at the start of animation.
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
 * @param animatorId The identifier of the animator being started.
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides value that will be set at the end of animation.
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes.
 * @param startDelay Animation start delay in milliseconds.
 * @param startValue Overrides value that will be set at the start of animation.
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
 * @param animatorId The identifier of the animator being started.
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides value that will be set at the end of animation.
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes.
 * @param startDelay Animation start delay in milliseconds.
 * @param startValue Overrides value that will be set at the start of animation.
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
 * @param animatorId The identifier of the animator being started.
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue Overrides value that will be set at the end of animation.
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes.
 * @param startDelay Animation start delay in milliseconds.
 * @param startValue Overrides value that will be set at the start of animation.
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
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Interpolation function.
 * @param startDelay Animation start delay in milliseconds.
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
