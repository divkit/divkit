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
 * Slide animation.
 * 
 * Can be created using the method [slideTransition].
 * 
 * Required properties: `type`.
 */
@Generated
class SlideTransition internal constructor(
    @JsonIgnore
    val properties: Properties,
) : AppearanceTransition {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "slide")
    )

    operator fun plus(additive: Properties): SlideTransition = SlideTransition(
        Properties(
            distance = additive.distance ?: properties.distance,
            duration = additive.duration ?: properties.duration,
            edge = additive.edge ?: properties.edge,
            interpolator = additive.interpolator ?: properties.interpolator,
            startDelay = additive.startDelay ?: properties.startDelay,
        )
    )

    class Properties internal constructor(
        /**
         * A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used.
         */
        val distance: Property<Dimension>?,
        /**
         * Animation duration in milliseconds.
         * Default value: `200`.
         */
        val duration: Property<Int>?,
        /**
         * Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>
         * Default value: `bottom`.
         */
        val edge: Property<Edge>?,
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
            result.tryPutProperty("distance", distance)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("edge", edge)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("start_delay", startDelay)
            return result
        }
    }

    /**
     * Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>
     * 
     * Possible values: [left, top, right, bottom].
     */
    @Generated
    sealed interface Edge

    fun Edge.asList() = listOf(this)
}

/**
 * @param distance A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used.
 * @param duration Animation duration in milliseconds.
 * @param edge Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.slideTransition(
    `use named arguments`: Guard = Guard.instance,
    distance: Dimension? = null,
    duration: Int? = null,
    edge: SlideTransition.Edge? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
): SlideTransition = SlideTransition(
    SlideTransition.Properties(
        distance = valueOrNull(distance),
        duration = valueOrNull(duration),
        edge = valueOrNull(edge),
        interpolator = valueOrNull(interpolator),
        startDelay = valueOrNull(startDelay),
    )
)

/**
 * @param distance A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used.
 * @param duration Animation duration in milliseconds.
 * @param edge Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun DivScope.slideTransitionProps(
    `use named arguments`: Guard = Guard.instance,
    distance: Dimension? = null,
    duration: Int? = null,
    edge: SlideTransition.Edge? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
) = SlideTransition.Properties(
    distance = valueOrNull(distance),
    duration = valueOrNull(duration),
    edge = valueOrNull(edge),
    interpolator = valueOrNull(interpolator),
    startDelay = valueOrNull(startDelay),
)

/**
 * @param distance A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used.
 * @param duration Animation duration in milliseconds.
 * @param edge Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun TemplateScope.slideTransitionRefs(
    `use named arguments`: Guard = Guard.instance,
    distance: ReferenceProperty<Dimension>? = null,
    duration: ReferenceProperty<Int>? = null,
    edge: ReferenceProperty<SlideTransition.Edge>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    startDelay: ReferenceProperty<Int>? = null,
) = SlideTransition.Properties(
    distance = distance,
    duration = duration,
    edge = edge,
    interpolator = interpolator,
    startDelay = startDelay,
)

/**
 * @param distance A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used.
 * @param duration Animation duration in milliseconds.
 * @param edge Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun SlideTransition.override(
    `use named arguments`: Guard = Guard.instance,
    distance: Dimension? = null,
    duration: Int? = null,
    edge: SlideTransition.Edge? = null,
    interpolator: AnimationInterpolator? = null,
    startDelay: Int? = null,
): SlideTransition = SlideTransition(
    SlideTransition.Properties(
        distance = valueOrNull(distance) ?: properties.distance,
        duration = valueOrNull(duration) ?: properties.duration,
        edge = valueOrNull(edge) ?: properties.edge,
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        startDelay = valueOrNull(startDelay) ?: properties.startDelay,
    )
)

/**
 * @param distance A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used.
 * @param duration Animation duration in milliseconds.
 * @param edge Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>
 * @param interpolator Transition speed nature.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun SlideTransition.defer(
    `use named arguments`: Guard = Guard.instance,
    distance: ReferenceProperty<Dimension>? = null,
    duration: ReferenceProperty<Int>? = null,
    edge: ReferenceProperty<SlideTransition.Edge>? = null,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    startDelay: ReferenceProperty<Int>? = null,
): SlideTransition = SlideTransition(
    SlideTransition.Properties(
        distance = distance ?: properties.distance,
        duration = duration ?: properties.duration,
        edge = edge ?: properties.edge,
        interpolator = interpolator ?: properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

/**
 * @param duration Animation duration in milliseconds.
 * @param startDelay Delay in milliseconds before animation starts.
 */
@Generated
fun SlideTransition.evaluate(
    `use named arguments`: Guard = Guard.instance,
    duration: ExpressionProperty<Int>? = null,
    startDelay: ExpressionProperty<Int>? = null,
): SlideTransition = SlideTransition(
    SlideTransition.Properties(
        distance = properties.distance,
        duration = duration ?: properties.duration,
        edge = properties.edge,
        interpolator = properties.interpolator,
        startDelay = startDelay ?: properties.startDelay,
    )
)

@Generated
fun SlideTransition.asList() = listOf(this)
