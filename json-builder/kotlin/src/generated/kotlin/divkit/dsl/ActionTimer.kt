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
         * Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
         */
        val action: Property<Action>?,
        /**
         * Timer identifier.
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
     * Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
     * 
     * Possible values: [start], [stop], [pause], [resume], [cancel], [reset].
     */
    @Generated
    sealed interface Action
}

/**
 * @param action Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
 * @param id Timer identifier.
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
 * @param action Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
 * @param id Timer identifier.
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
 * @param action Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
 * @param id Timer identifier.
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
 * @param action Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
 * @param id Timer identifier.
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
 * @param action Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
 * @param id Timer identifier.
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
 * @param action Defines timer action:<li>`start`- starts the timer when stopped, does onStart action;</li><li>`stop`- stops timer, resets the time, does `onEnd` action;</li><li>`pause`- pause timer, preserves current time;</li><li>`resume`- starts timer from paused state, restores saved time;</li><li>`cancel`- stops timer, resets its state, does onInterrupt action;</li><li>`reset`- cancels timer and starts it again.</li>
 * @param id Timer identifier.
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
