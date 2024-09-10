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
 * Can be created using the method [animatorBase].
 * 
 * Required parameters: `variable_name, id, duration`.
 */
@Generated
class AnimatorBase internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): AnimatorBase = AnimatorBase(
        Properties(
            cancelActions = additive.cancelActions ?: properties.cancelActions,
            direction = additive.direction ?: properties.direction,
            duration = additive.duration ?: properties.duration,
            endActions = additive.endActions ?: properties.endActions,
            id = additive.id ?: properties.id,
            interpolator = additive.interpolator ?: properties.interpolator,
            repeatCount = additive.repeatCount ?: properties.repeatCount,
            startDelay = additive.startDelay ?: properties.startDelay,
            variableName = additive.variableName ?: properties.variableName,
        )
    )

    class Properties internal constructor(
        /**
         * Actions performed when the animator is cancelled. For example, when an action with `animator_stop` type is received
         */
        val cancelActions: Property<List<Action>>?,
        /**
         * Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
         * Default value: `normal`.
         */
        val direction: Property<AnimationDirection>?,
        /**
         * Animation duration in milliseconds.
         */
        val duration: Property<Long>?,
        /**
         * Actions performed when the animator completes animation.
         */
        val endActions: Property<List<Action>>?,
        /**
         * Animator identificator
         */
        val id: Property<String>?,
        /**
         * Interpolation function.
         * Default value: `linear`.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
         * Default value: `1`.
         */
        val repeatCount: Property<Int>?,
        /**
         * Animation start delay in milliseconds.
         * Default value: `0`.
         */
        val startDelay: Property<Int>?,
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
            result.tryPutProperty("id", id)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("repeat_count", repeatCount)
            result.tryPutProperty("start_delay", startDelay)
            result.tryPutProperty("variable_name", variableName)
            return result
        }
    }
}

/**
 * @param cancelActions Actions performed when the animator is cancelled. For example, when an action with `animator_stop` type is received
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions performed when the animator completes animation.
 * @param id Animator identificator
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
 * @param startDelay Animation start delay in milliseconds.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun DivScope.animatorBase(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: List<Action>? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endActions: List<Action>? = null,
    id: String? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Int? = null,
    startDelay: Int? = null,
    variableName: String? = null,
): AnimatorBase = AnimatorBase(
    AnimatorBase.Properties(
        cancelActions = valueOrNull(cancelActions),
        direction = valueOrNull(direction),
        duration = valueOrNull(duration),
        endActions = valueOrNull(endActions),
        id = valueOrNull(id),
        interpolator = valueOrNull(interpolator),
        repeatCount = valueOrNull(repeatCount),
        startDelay = valueOrNull(startDelay),
        variableName = valueOrNull(variableName),
    )
)

/**
 * @param cancelActions Actions performed when the animator is cancelled. For example, when an action with `animator_stop` type is received
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions performed when the animator completes animation.
 * @param id Animator identificator
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
 * @param startDelay Animation start delay in milliseconds.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun DivScope.animatorBaseProps(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: List<Action>? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endActions: List<Action>? = null,
    id: String? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Int? = null,
    startDelay: Int? = null,
    variableName: String? = null,
) = AnimatorBase.Properties(
    cancelActions = valueOrNull(cancelActions),
    direction = valueOrNull(direction),
    duration = valueOrNull(duration),
    endActions = valueOrNull(endActions),
    id = valueOrNull(id),
    interpolator = valueOrNull(interpolator),
    repeatCount = valueOrNull(repeatCount),
    startDelay = valueOrNull(startDelay),
    variableName = valueOrNull(variableName),
)

/**
 * @param cancelActions Actions performed when the animator is cancelled. For example, when an action with `animator_stop` type is received
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions performed when the animator completes animation.
 * @param id Animator identificator
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
 * @param startDelay Animation start delay in milliseconds.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun TemplateScope.animatorBaseRefs(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: ReferenceProperty<List<Action>>? = null,
    direction: ReferenceProperty<AnimationDirection>? = null,
    duration: ReferenceProperty<Long>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    id: ReferenceProperty<String>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    repeatCount: ReferenceProperty<Int>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    variableName: ReferenceProperty<String>? = null,
) = AnimatorBase.Properties(
    cancelActions = cancelActions,
    direction = direction,
    duration = duration,
    endActions = endActions,
    id = id,
    interpolator = interpolator,
    repeatCount = repeatCount,
    startDelay = startDelay,
    variableName = variableName,
)

/**
 * @param cancelActions Actions performed when the animator is cancelled. For example, when an action with `animator_stop` type is received
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions performed when the animator completes animation.
 * @param id Animator identificator
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
 * @param startDelay Animation start delay in milliseconds.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun AnimatorBase.override(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: List<Action>? = null,
    direction: AnimationDirection? = null,
    duration: Long? = null,
    endActions: List<Action>? = null,
    id: String? = null,
    interpolator: AnimationInterpolator? = null,
    repeatCount: Int? = null,
    startDelay: Int? = null,
    variableName: String? = null,
): AnimatorBase = AnimatorBase(
    AnimatorBase.Properties(
        cancelActions = valueOrNull(cancelActions) ?: properties.cancelActions,
        direction = valueOrNull(direction) ?: properties.direction,
        duration = valueOrNull(duration) ?: properties.duration,
        endActions = valueOrNull(endActions) ?: properties.endActions,
        id = valueOrNull(id) ?: properties.id,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        repeatCount = valueOrNull(repeatCount) ?: properties.repeatCount,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

/**
 * @param cancelActions Actions performed when the animator is cancelled. For example, when an action with `animator_stop` type is received
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param endActions Actions performed when the animator completes animation.
 * @param id Animator identificator
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
 * @param startDelay Animation start delay in milliseconds.
 * @param variableName Name of the variable being animated.
 */
@Generated
fun AnimatorBase.defer(
    `use named arguments`: Guard = Guard.instance,
    cancelActions: ReferenceProperty<List<Action>>? = null,
    direction: ReferenceProperty<AnimationDirection>? = null,
    duration: ReferenceProperty<Long>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    id: ReferenceProperty<String>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    repeatCount: ReferenceProperty<Int>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    variableName: ReferenceProperty<String>? = null,
): AnimatorBase = AnimatorBase(
    AnimatorBase.Properties(
        cancelActions = cancelActions ?: properties.cancelActions,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endActions = endActions ?: properties.endActions,
        id = id ?: properties.id,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = repeatCount ?: properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        variableName = variableName ?: properties.variableName,
    )
)

/**
 * @param direction Animation direction. This property sets whether an animation should play forward, backward, or alternate back and forth between playing the sequence forward and backward.
 * @param duration Animation duration in milliseconds.
 * @param interpolator Interpolation function.
 * @param repeatCount The number of times the animation will repeat before it finishes. `0` enables infinite repeats.
 * @param startDelay Animation start delay in milliseconds.
 */
@Generated
fun AnimatorBase.evaluate(
    `use named arguments`: Guard = Guard.instance,
    direction: ExpressionProperty<AnimationDirection>? = null,
    duration: ExpressionProperty<Long>? = null,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    repeatCount: ExpressionProperty<Int>? = null,
    startDelay: ExpressionProperty<Int>? = null,
): AnimatorBase = AnimatorBase(
    AnimatorBase.Properties(
        cancelActions = properties.cancelActions,
        direction = direction ?: properties.direction,
        duration = duration ?: properties.duration,
        endActions = properties.endActions,
        id = properties.id,
        interpolator = interpolator ?: properties.interpolator,
        repeatCount = repeatCount ?: properties.repeatCount,
        startDelay = startDelay ?: properties.startDelay,
        variableName = properties.variableName,
    )
)

@Generated
fun AnimatorBase.asList() = listOf(this)
