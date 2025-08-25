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
 * Color animator.
 * 
 * Can be created using the method [colorAnimator].
 * 
 * Required parameters: `variable_name, type, id, end_value, duration`.
 */
@Generated
@ExposedCopyVisibility
data class ColorAnimator internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Animator {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "color_animator")
    )

    operator fun plus(additive: Properties): ColorAnimator = ColorAnimator(
        Properties(
            cancelActions = additive.cancelActions ?: properties.cancelActions,
            direction = additive.direction ?: properties.direction,
            duration = additive.duration ?: properties.duration,
            endActions = additive.endActions ?: properties.endActions,
            endValue = additive.endValue ?: properties.endValue,
            id = additive.id ?: properties.id,
            interpolator = additive.interpolator ?: properties.interpolator,
            repeatCount = additive.repeatCount ?: properties.repeatCount,
            startDelay = additive.startDelay ?: properties.startDelay,
            startValue = additive.startValue ?: properties.startValue,
            variableName = additive.variableName ?: properties.variableName,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
         */
        val cancelActions: Property<List<Action>>?,
        /**
         * Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
         * Default value: `normal`.
         */
        val direction: Property<AnimationDirection>?,
        /**
         * Animation duration in milliseconds.
         */
        val duration: Property<Long>?,
        /**
         * Actions when the animation is completed.
         */
        val endActions: Property<List<Action>>?,
        /**
         * The value the variable will have when the animation ends.
         */
        val endValue: Property<Color>?,
        /**
         * Animator ID.
         */
        val id: Property<String>?,
        /**
         * Animated value interpolation function.
         * Default value: `linear`.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
         * Default value: `{"type": "fixed", "value": 1}`.
         */
        val repeatCount: Property<Count>?,
        /**
         * Delay before the animation is launched in milliseconds.
         * Default value: `0`.
         */
        val startDelay: Property<Int>?,
        /**
         * The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
         */
        val startValue: Property<Color>?,
        /**
         * Name of the variable being animated.
         */
        val variableName: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("cancel_actions", cancelActions)
            result.tryPutProperty("direction", direction)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("end_actions", endActions)
            result.tryPutProperty("end_value", endValue)
            result.tryPutProperty("id", id)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("repeat_count", repeatCount)
            result.tryPutProperty("start_delay", startDelay)
            result.tryPutProperty("start_value", startValue)
            result.tryPutProperty("variable_name", variableName)
            return result
        }
    }
}

/**
 * @param cancelActions Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions when the animation is completed.
 * @param endValue The value the variable will have when the animation ends.
 * @param id Animator ID.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun DivScope.colorAnimator(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: List<Action>? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endActions: List<Action>? = null,
    endValue: Color? = null,
    id: String? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Count? = null,
    startDelay: Int? = null,
    startValue: Color? = null,
    variableName: String? = null,
): ColorAnimator = ColorAnimator(
    ColorAnimator.Properties(
        cancelActions = valueOrNull(cancelActions),
        direction = valueOrNull(direction),
        duration = valueOrNull(duration),
        endActions = valueOrNull(endActions),
        endValue = valueOrNull(endValue),
        id = valueOrNull(id),
        interpolator = valueOrNull(interpolator),
        repeatCount = valueOrNull(repeatCount),
        startDelay = valueOrNull(startDelay),
        startValue = valueOrNull(startValue),
        variableName = valueOrNull(variableName),
    )
)

/**
 * @param cancelActions Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions when the animation is completed.
 * @param endValue The value the variable will have when the animation ends.
 * @param id Animator ID.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun DivScope.colorAnimatorProps(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: List<Action>? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endActions: List<Action>? = null,
    endValue: Color? = null,
    id: String? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Count? = null,
    startDelay: Int? = null,
    startValue: Color? = null,
    variableName: String? = null,
) = ColorAnimator.Properties(
    cancelActions = valueOrNull(cancelActions),
    direction = valueOrNull(direction),
    duration = valueOrNull(duration),
    endActions = valueOrNull(endActions),
    endValue = valueOrNull(endValue),
    id = valueOrNull(id),
    interpolator = valueOrNull(interpolator),
    repeatCount = valueOrNull(repeatCount),
    startDelay = valueOrNull(startDelay),
    startValue = valueOrNull(startValue),
    variableName = valueOrNull(variableName),
)

/**
 * @param cancelActions Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions when the animation is completed.
 * @param endValue The value the variable will have when the animation ends.
 * @param id Animator ID.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun TemplateScope.colorAnimatorRefs(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: ReferenceProperty<List<Action>>? = null,
    direction: ReferenceProperty<AnimationDirection>? = null,
    duration: ReferenceProperty<Long>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    endValue: ReferenceProperty<Color>? = null,
    id: ReferenceProperty<String>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    repeatCount: ReferenceProperty<Count>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    startValue: ReferenceProperty<Color>? = null,
    variableName: ReferenceProperty<String>? = null,
) = ColorAnimator.Properties(
    cancelActions = cancelActions,
    direction = direction,
    duration = duration,
    endActions = endActions,
    endValue = endValue,
    id = id,
    interpolator = interpolator,
    repeatCount = repeatCount,
    startDelay = startDelay,
    startValue = startValue,
    variableName = variableName,
)

/**
 * @param cancelActions Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions when the animation is completed.
 * @param endValue The value the variable will have when the animation ends.
 * @param id Animator ID.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun ColorAnimator.override(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: List<Action>? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endActions: List<Action>? = null,
    endValue: Color? = null,
    id: String? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Count? = null,
    startDelay: Int? = null,
    startValue: Color? = null,
    variableName: String? = null,
): ColorAnimator = ColorAnimator(
    ColorAnimator.Properties(
        cancelActions = valueOrNull(cancelActions) ?: properties.cancelActions,
        direction = valueOrNull(direction) ?: properties.direction,
        duration = valueOrNull(duration) ?: properties.duration,
        endActions = valueOrNull(endActions) ?: properties.endActions,
        endValue = valueOrNull(endValue) ?: properties.endValue,
        id = valueOrNull(id) ?: properties.id,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        repeatCount = valueOrNull(repeatCount) ?: properties.repeatCount,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
        startValue = valueOrNull(startValue) ?: properties.startValue,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

/**
 * @param cancelActions Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions when the animation is completed.
 * @param endValue The value the variable will have when the animation ends.
 * @param id Animator ID.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun ColorAnimator.defer(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: ReferenceProperty<List<Action>>? = null,
    direction: ReferenceProperty<AnimationDirection>? = null,
    duration: ReferenceProperty<Long>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    endValue: ReferenceProperty<Color>? = null,
    id: ReferenceProperty<String>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    repeatCount: ReferenceProperty<Count>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    startValue: ReferenceProperty<Color>? = null,
    variableName: ReferenceProperty<String>? = null,
): ColorAnimator = ColorAnimator(
    ColorAnimator.Properties(
        cancelActions = cancelActions ?: properties.cancelActions,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endActions = endActions ?: properties.endActions,
        endValue = endValue ?: properties.endValue,
        id = id ?: properties.id,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = repeatCount ?: properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        startValue = startValue ?: properties.startValue,
        variableName = variableName ?: properties.variableName,
    )
)

/**
 * @param cancelActions Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received.
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions when the animation is completed.
 * @param endValue The value the variable will have when the animation ends.
 * @param id Animator ID.
 * @param interpolator Animated value interpolation function.
 * @param repeatCount Number of times the animation will repeat before stopping. A value of `0` enables infinite looping.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun ColorAnimator.modify(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: Property<List<Action>>? = null,
    direction: Property<AnimationDirection>? = null,
    duration: Property<Long>? = null,
    endActions: Property<List<Action>>? = null,
    endValue: Property<Color>? = null,
    id: Property<String>? = null,
    interpolator: Property<AnimationInterpolator>? = null,
    repeatCount: Property<Count>? = null,
    startDelay: Property<Int>? = null,
    startValue: Property<Color>? = null,
    variableName: Property<String>? = null,
): ColorAnimator = ColorAnimator(
    ColorAnimator.Properties(
        cancelActions = cancelActions ?: properties.cancelActions,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endActions = endActions ?: properties.endActions,
        endValue = endValue ?: properties.endValue,
        id = id ?: properties.id,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = repeatCount ?: properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        startValue = startValue ?: properties.startValue,
        variableName = variableName ?: properties.variableName,
    )
)

/**
 * @param direction Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endValue The value the variable will have when the animation ends.
 * @param interpolator Animated value interpolation function.
 * @param startDelay Delay before the animation is launched in milliseconds.
 * @param startValue The value the variable will have when the animation starts. If the property isn't specified, the current value of the variable will be used.
 */
@Generated
fun ColorAnimator.evaluate(
    `use named arguments`: Guard = Guard.instance,
    direction: ExpressionProperty<AnimationDirection>? = null,
    duration: ExpressionProperty<Long>? = null,
    endValue: ExpressionProperty<Color>? = null,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    startDelay: ExpressionProperty<Int>? = null,
    startValue: ExpressionProperty<Color>? = null,
): ColorAnimator = ColorAnimator(
    ColorAnimator.Properties(
        cancelActions = properties.cancelActions,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endActions = properties.endActions,
        endValue = endValue ?: properties.endValue,
        id = properties.id,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        startValue = startValue ?: properties.startValue,
        variableName = properties.variableName,
    )
)

@Generated
fun ColorAnimator.asList() = listOf(this)
