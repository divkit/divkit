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
 * Element position and size change animation.
 * 
 * Can be created using the method [changeBoundsTransition].
 * 
 * Required parameters: `type`.
 */
@Generated
data class ChangeBoundsTransition internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ChangeTransition {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "change_bounds")
    )

    operator fun plus(additive: Properties): ChangeBoundsTransition = ChangeBoundsTransition(
        Properties(
            duration = additive.duration ?: properties.duration,
            interpolator = additive.interpolator ?: properties.interpolator,
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
            result.tryPutProperty("start_delay", startDelay)
            return result
        }
    }
}

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.changeBoundsTransition(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
): ChangeBoundsTransition = ChangeBoundsTransition(
    ChangeBoundsTransition.Properties(
        duration = valueOrNull(duration),
        interpolator = valueOrNull(interpolator),
        startDelay = valueOrNull(startDelay),
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.changeBoundsTransitionProps(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
) = ChangeBoundsTransition.Properties(
    duration = valueOrNull(duration),
    interpolator = valueOrNull(interpolator),
    startDelay = valueOrNull(startDelay),
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun TemplateScope.changeBoundsTransitionRefs(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    startDelay: ReferenceProperty<Int>? = null,
) = ChangeBoundsTransition.Properties(
    duration = duration,
    interpolator = interpolator,
    startDelay = startDelay,
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun ChangeBoundsTransition.override(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
): ChangeBoundsTransition = ChangeBoundsTransition(
    ChangeBoundsTransition.Properties(
        duration = valueOrNull(duration) ?: properties.duration,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun ChangeBoundsTransition.defer(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    startDelay: ReferenceProperty<Int>? = null,
): ChangeBoundsTransition = ChangeBoundsTransition(
    ChangeBoundsTransition.Properties(
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun ChangeBoundsTransition.modify(
    `use named arguments`: Guard = Guard.instance,
    duration: Property<Int>? = null,
    interpolator: Property<AnimationInterpolator>? = null,
    startDelay: Property<Int>? = null,
): ChangeBoundsTransition = ChangeBoundsTransition(
    ChangeBoundsTransition.Properties(
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun ChangeBoundsTransition.evaluate(
    `use named arguments`: Guard = Guard.instance,
    duration: ExpressionProperty<Int>? = null,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    startDelay: ExpressionProperty<Int>? = null,
): ChangeBoundsTransition = ChangeBoundsTransition(
    ChangeBoundsTransition.Properties(
        duration = duration ?: properties.duration,
        interpolator = interpolator ?: properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

@Generated
fun ChangeBoundsTransition.asList() = listOf(this)
