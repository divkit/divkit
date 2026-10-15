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
 * Scrolls to a position or switches to the container element specified by the `destination` parameter.
 * 
 * Can be created using the method [actionScrollTo].
 * 
 * Required parameters: `type, id, destination`.
 */
@Generated
@ExposedCopyVisibility
data class ActionScrollTo internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "scroll_to")
    )

    operator fun plus(additive: Properties): ActionScrollTo = ActionScrollTo(
        Properties(
            animated = additive.animated ?: properties.animated,
            destination = additive.destination ?: properties.destination,
            duration = additive.duration ?: properties.duration,
            id = additive.id ?: properties.id,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Enables scrolling animation.
         * Default value: `true`.
         */
        val animated: Property<Boolean>?,
        /**
         * Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`item_id`: Scroll to the element with identifier provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>
         */
        val destination: Property<ActionScrollDestination>?,
        /**
         * Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
         */
        val duration: Property<Int>?,
        /**
         * ID of the element where the action should be performed.
         */
        val id: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("animated", animated)
            result.tryPutProperty("destination", destination)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("id", id)
            return result
        }
    }
}

/**
 * @param animated Enables scrolling animation.
 * @param destination Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`item_id`: Scroll to the element with identifier provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>
 * @param duration Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
 * @param id ID of the element where the action should be performed.
 */
@Generated
fun DivScope.actionScrollTo(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    destination: ActionScrollDestination? = null,
    duration: Int? = null,
    id: String? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = valueOrNull(animated),
        destination = valueOrNull(destination),
        duration = valueOrNull(duration),
        id = valueOrNull(id),
    )
)

/**
 * @param animated Enables scrolling animation.
 * @param destination Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`item_id`: Scroll to the element with identifier provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>
 * @param duration Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
 * @param id ID of the element where the action should be performed.
 */
@Generated
fun DivScope.actionScrollToProps(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    destination: ActionScrollDestination? = null,
    duration: Int? = null,
    id: String? = null,
) = ActionScrollTo.Properties(
    animated = valueOrNull(animated),
    destination = valueOrNull(destination),
    duration = valueOrNull(duration),
    id = valueOrNull(id),
)

/**
 * @param animated Enables scrolling animation.
 * @param destination Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`item_id`: Scroll to the element with identifier provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>
 * @param duration Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
 * @param id ID of the element where the action should be performed.
 */
@Generated
fun TemplateScope.actionScrollToRefs(
    `use named arguments`: Guard = Guard.instance,
    animated: ReferenceProperty<Boolean>? = null,
    destination: ReferenceProperty<ActionScrollDestination>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
) = ActionScrollTo.Properties(
    animated = animated,
    destination = destination,
    duration = duration,
    id = id,
)

/**
 * @param animated Enables scrolling animation.
 * @param destination Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`item_id`: Scroll to the element with identifier provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>
 * @param duration Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
 * @param id ID of the element where the action should be performed.
 */
@Generated
fun ActionScrollTo.override(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    destination: ActionScrollDestination? = null,
    duration: Int? = null,
    id: String? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = valueOrNull(animated) ?: properties.animated,
        destination = valueOrNull(destination) ?: properties.destination,
        duration = valueOrNull(duration) ?: properties.duration,
        id = valueOrNull(id) ?: properties.id,
    )
)

/**
 * @param animated Enables scrolling animation.
 * @param destination Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`item_id`: Scroll to the element with identifier provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>
 * @param duration Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
 * @param id ID of the element where the action should be performed.
 */
@Generated
fun ActionScrollTo.defer(
    `use named arguments`: Guard = Guard.instance,
    animated: ReferenceProperty<Boolean>? = null,
    destination: ReferenceProperty<ActionScrollDestination>? = null,
    duration: ReferenceProperty<Int>? = null,
    id: ReferenceProperty<String>? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = animated ?: properties.animated,
        destination = destination ?: properties.destination,
        duration = duration ?: properties.duration,
        id = id ?: properties.id,
    )
)

/**
 * @param animated Enables scrolling animation.
 * @param destination Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`item_id`: Scroll to the element with identifier provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>
 * @param duration Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
 * @param id ID of the element where the action should be performed.
 */
@Generated
fun ActionScrollTo.modify(
    `use named arguments`: Guard = Guard.instance,
    animated: Property<Boolean>? = null,
    destination: Property<ActionScrollDestination>? = null,
    duration: Property<Int>? = null,
    id: Property<String>? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = animated ?: properties.animated,
        destination = destination ?: properties.destination,
        duration = duration ?: properties.duration,
        id = id ?: properties.id,
    )
)

/**
 * @param animated Enables scrolling animation.
 * @param duration Scrolling animation duration in milliseconds. If omitted, the default platform scrolling behavior is used. A value of `0` means instant scrolling. Ignored when `animated` is `false` or animations are globally disabled.
 * @param id ID of the element where the action should be performed.
 */
@Generated
fun ActionScrollTo.evaluate(
    `use named arguments`: Guard = Guard.instance,
    animated: ExpressionProperty<Boolean>? = null,
    duration: ExpressionProperty<Int>? = null,
    id: ExpressionProperty<String>? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = animated ?: properties.animated,
        destination = properties.destination,
        duration = duration ?: properties.duration,
        id = id ?: properties.id,
    )
)

@Generated
fun ActionScrollTo.asList() = listOf(this)
