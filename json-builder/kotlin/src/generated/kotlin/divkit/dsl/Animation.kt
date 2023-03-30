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
 * Element animation parameters.
 * 
 * Can be created using the method [animation].
 * 
 * Required properties: `name`.
 */
@Generated
class Animation internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Animation = Animation(
        Properties(
            duration = additive.duration ?: properties.duration,
            endValue = additive.endValue ?: properties.endValue,
            interpolator = additive.interpolator ?: properties.interpolator,
            items = additive.items ?: properties.items,
            name = additive.name ?: properties.name,
            repeat = additive.repeat ?: properties.repeat,
            startDelay = additive.startDelay ?: properties.startDelay,
            startValue = additive.startValue ?: properties.startValue,
        )
    )

    class Properties internal constructor(
        /**
         * Animation duration in milliseconds.
         * Default value: `300`.
         */
        val duration: Property<Int>?,
        /**
         * Final value of an animation.
         */
        val endValue: Property<Double>?,
        /**
         * Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
         * Default value: `spring`.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * Animation elements.
         */
        val items: Property<List<Animation>>?,
        /**
         * Animation type.
         */
        val name: Property<Name>?,
        /**
         * Number of animation repetitions.
         * Default value: `{ "type": "infinity" }`.
         */
        val repeat: Property<Count>?,
        /**
         * Delay in milliseconds before animation starts.
         * Default value: `0`.
         */
        val startDelay: Property<Int>?,
        /**
         * Starting value of an animation.
         */
        val startValue: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("end_value", endValue)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("items", items)
            result.tryPutProperty("name", name)
            result.tryPutProperty("repeat", repeat)
            result.tryPutProperty("start_delay", startDelay)
            result.tryPutProperty("start_value", startValue)
            return result
        }
    }

    /**
     * Animation type.
     * 
     * Possible values: [fade, translate, scale, native, set, no_animation].
     */
    @Generated
    sealed interface Name
}

/**
 * @param duration Animation duration in milliseconds.
 * @param endValue Final value of an animation.
 * @param interpolator Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param items Animation elements.
 * @param name Animation type.
 * @param repeat Number of animation repetitions.
 * @param startDelay Delay in milliseconds before animation starts.
 * @param startValue Starting value of an animation.
 */
@Generated
fun DivScope.animation(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    endValue: Double? = null,
    interpolator: AnimationInterpolator? = null,
    items: List<Animation>? = null,
    name: Animation.Name? = null,
    repeat: Count? = null,
    startDelay: Int? = null,
    startValue: Double? = null,
): Animation = Animation(
    Animation.Properties(
        duration = valueOrNull(duration),
        endValue = valueOrNull(endValue),
        interpolator = valueOrNull(interpolator),
        items = valueOrNull(items),
        name = valueOrNull(name),
        repeat = valueOrNull(repeat),
        startDelay = valueOrNull(startDelay),
        startValue = valueOrNull(startValue),
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param endValue Final value of an animation.
 * @param interpolator Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param items Animation elements.
 * @param name Animation type.
 * @param repeat Number of animation repetitions.
 * @param startDelay Delay in milliseconds before animation starts.
 * @param startValue Starting value of an animation.
 */
@Generated
fun DivScope.animationProps(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    endValue: Double? = null,
    interpolator: AnimationInterpolator? = null,
    items: List<Animation>? = null,
    name: Animation.Name? = null,
    repeat: Count? = null,
    startDelay: Int? = null,
    startValue: Double? = null,
) = Animation.Properties(
    duration = valueOrNull(duration),
    endValue = valueOrNull(endValue),
    interpolator = valueOrNull(interpolator),
    items = valueOrNull(items),
    name = valueOrNull(name),
    repeat = valueOrNull(repeat),
    startDelay = valueOrNull(startDelay),
    startValue = valueOrNull(startValue),
)

/**
 * @param duration Animation duration in milliseconds.
 * @param endValue Final value of an animation.
 * @param interpolator Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param items Animation elements.
 * @param name Animation type.
 * @param repeat Number of animation repetitions.
 * @param startDelay Delay in milliseconds before animation starts.
 * @param startValue Starting value of an animation.
 */
@Generated
fun TemplateScope.animationRefs(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    endValue: ReferenceProperty<Double>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    items: ReferenceProperty<List<Animation>>? = null,
    name: ReferenceProperty<Animation.Name>? = null,
    repeat: ReferenceProperty<Count>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    startValue: ReferenceProperty<Double>? = null,
) = Animation.Properties(
    duration = duration,
    endValue = endValue,
    interpolator = interpolator,
    items = items,
    name = name,
    repeat = repeat,
    startDelay = startDelay,
    startValue = startValue,
)

/**
 * @param duration Animation duration in milliseconds.
 * @param endValue Final value of an animation.
 * @param interpolator Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param items Animation elements.
 * @param name Animation type.
 * @param repeat Number of animation repetitions.
 * @param startDelay Delay in milliseconds before animation starts.
 * @param startValue Starting value of an animation.
 */
@Generated
fun Animation.override(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    endValue: Double? = null,
    interpolator: AnimationInterpolator? = null,
    items: List<Animation>? = null,
    name: Animation.Name? = null,
    repeat: Count? = null,
    startDelay: Int? = null,
    startValue: Double? = null,
): Animation = Animation(
    Animation.Properties(
        duration = valueOrNull(duration) ?: properties.duration,
        endValue = valueOrNull(endValue) ?: properties.endValue,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        items = valueOrNull(items) ?: properties.items,
        name = valueOrNull(name) ?: properties.name,
        repeat = valueOrNull(repeat) ?: properties.repeat,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
        startValue = valueOrNull(startValue) ?: properties.startValue,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param endValue Final value of an animation.
 * @param interpolator Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param items Animation elements.
 * @param name Animation type.
 * @param repeat Number of animation repetitions.
 * @param startDelay Delay in milliseconds before animation starts.
 * @param startValue Starting value of an animation.
 */
@Generated
fun Animation.defer(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    endValue: ReferenceProperty<Double>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    items: ReferenceProperty<List<Animation>>? = null,
    name: ReferenceProperty<Animation.Name>? = null,
    repeat: ReferenceProperty<Count>? = null,
    startDelay: ReferenceProperty<Int>? = null,
    startValue: ReferenceProperty<Double>? = null,
): Animation = Animation(
    Animation.Properties(
        duration = duration ?: properties.duration,
        endValue = endValue ?: properties.endValue,
        interpolator = interpolator ?: properties.interpolator,
        items = items ?: properties.items,
        name = name ?: properties.name,
        repeat = repeat ?: properties.repeat,
        startDelay = startDelay ?: properties.startDelay,
        startValue = startValue ?: properties.startValue,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param endValue Final value of an animation.
 * @param interpolator Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param name Animation type.
 * @param startDelay Delay in milliseconds before animation starts.
 * @param startValue Starting value of an animation.
 */
@Generated
fun Animation.evaluate(
    `use named arguments`: Guard = Guard.instance,
    duration: ExpressionProperty<Int>? = null,
    endValue: ExpressionProperty<Double>? = null,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    name: ExpressionProperty<Animation.Name>? = null,
    startDelay: ExpressionProperty<Int>? = null,
    startValue: ExpressionProperty<Double>? = null,
): Animation = Animation(
    Animation.Properties(
        duration = duration ?: properties.duration,
        endValue = endValue ?: properties.endValue,
        interpolator = interpolator ?: properties.interpolator,
        items = properties.items,
        name = name ?: properties.name,
        repeat = properties.repeat,
        startDelay = startDelay ?: properties.startDelay,
        startValue = startValue ?: properties.startValue,
    )
)

@Generated
fun Animation.asList() = listOf(this)

@Generated
fun Animation.Name.asList() = listOf(this)
