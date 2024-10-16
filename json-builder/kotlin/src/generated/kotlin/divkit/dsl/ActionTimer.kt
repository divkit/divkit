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
 * Controls the timer.
 * 
 * Can be created using the method [actionTimer].
 * 
 * Required parameters: `type, id, action`.
 */
@Generated
data class ActionTimer internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "timer")
    )

    operator fun plus(additive: Properties): ActionTimer = ActionTimer(
        Properties(
            action = additive.action ?: properties.action,
            id = additive.id ?: properties.id,
        )
    )

    data class Properties internal constructor(
        /**
         * Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
         */
        val action: Property<Action>?,
        /**
         * Timer ID.
         */
        val id: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("action", action)
            result.tryPutProperty("id", id)
            return result
        }
    }

    /**
     * Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
     * 
     * Possible values: [start], [stop], [pause], [resume], [cancel], [reset].
     */
    @Generated
    sealed interface Action
}

/**
 * @param action Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
 * @param id Timer ID.
 */
@Generated
fun DivScope.actionTimer(
    `use named arguments`: Guard = Guard.instance,
    action: ActionTimer.Action? = null,
    id: String? = null,
): ActionTimer = ActionTimer(
    ActionTimer.Properties(
        action = valueOrNull(action),
        id = valueOrNull(id),
    )
)

/**
 * @param action Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
 * @param id Timer ID.
 */
@Generated
fun DivScope.actionTimerProps(
    `use named arguments`: Guard = Guard.instance,
    action: ActionTimer.Action? = null,
    id: String? = null,
) = ActionTimer.Properties(
    action = valueOrNull(action),
    id = valueOrNull(id),
)

/**
 * @param action Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
 * @param id Timer ID.
 */
@Generated
fun TemplateScope.actionTimerRefs(
    `use named arguments`: Guard = Guard.instance,
    action: ReferenceProperty<ActionTimer.Action>? = null,
    id: ReferenceProperty<String>? = null,
) = ActionTimer.Properties(
    action = action,
    id = id,
)

/**
 * @param action Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
 * @param id Timer ID.
 */
@Generated
fun ActionTimer.override(
    `use named arguments`: Guard = Guard.instance,
    action: ActionTimer.Action? = null,
    id: String? = null,
): ActionTimer = ActionTimer(
    ActionTimer.Properties(
        action = valueOrNull(action) ?: properties.action,
        id = valueOrNull(id) ?: properties.id,
    )
)

/**
 * @param action Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
 * @param id Timer ID.
 */
@Generated
fun ActionTimer.defer(
    `use named arguments`: Guard = Guard.instance,
    action: ReferenceProperty<ActionTimer.Action>? = null,
    id: ReferenceProperty<String>? = null,
): ActionTimer = ActionTimer(
    ActionTimer.Properties(
        action = action ?: properties.action,
        id = id ?: properties.id,
    )
)

/**
 * @param action Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>
 * @param id Timer ID.
 */
@Generated
fun ActionTimer.evaluate(
    `use named arguments`: Guard = Guard.instance,
    action: ExpressionProperty<ActionTimer.Action>? = null,
    id: ExpressionProperty<String>? = null,
): ActionTimer = ActionTimer(
    ActionTimer.Properties(
        action = action ?: properties.action,
        id = id ?: properties.id,
    )
)

@Generated
fun ActionTimer.asList() = listOf(this)

@Generated
fun ActionTimer.Action.asList() = listOf(this)
