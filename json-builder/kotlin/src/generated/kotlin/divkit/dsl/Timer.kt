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
 * Timer.
 * 
 * Can be created using the method [timer].
 * 
 * Required properties: `id`.
 */
@Generated
class Timer internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Timer = Timer(
        Properties(
            duration = additive.duration ?: properties.duration,
            endActions = additive.endActions ?: properties.endActions,
            id = additive.id ?: properties.id,
            tickActions = additive.tickActions ?: properties.tickActions,
            tickInterval = additive.tickInterval ?: properties.tickInterval,
            valueVariable = additive.valueVariable ?: properties.valueVariable,
        )
    )

    class Properties internal constructor(
        /**
         * Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
         * Default value: `0`.
         */
        val duration: Property<Int>?,
        /**
         * Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received.
         */
        val endActions: Property<List<Action>>?,
        /**
         * Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
         */
        val id: Property<String>?,
        /**
         * Actions that are performed on each count of the timer.
         */
        val tickActions: Property<List<Action>>?,
        /**
         * Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
         */
        val tickInterval: Property<Int>?,
        /**
         * Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command.
         */
        val valueVariable: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("duration", duration)
            result.tryPutProperty("end_actions", endActions)
            result.tryPutProperty("id", id)
            result.tryPutProperty("tick_actions", tickActions)
            result.tryPutProperty("tick_interval", tickInterval)
            result.tryPutProperty("value_variable", valueVariable)
            return result
        }
    }
}

/**
 * @param duration Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
 * @param endActions Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received.
 * @param id Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
 * @param tickActions Actions that are performed on each count of the timer.
 * @param tickInterval Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
 * @param valueVariable Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command.
 */
@Generated
fun DivScope.timer(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    endActions: List<Action>? = null,
    id: String? = null,
    tickActions: List<Action>? = null,
    tickInterval: Int? = null,
    valueVariable: String? = null,
): Timer = Timer(
    Timer.Properties(
        duration = valueOrNull(duration),
        endActions = valueOrNull(endActions),
        id = valueOrNull(id),
        tickActions = valueOrNull(tickActions),
        tickInterval = valueOrNull(tickInterval),
        valueVariable = valueOrNull(valueVariable),
    )
)

/**
 * @param duration Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
 * @param endActions Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received.
 * @param id Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
 * @param tickActions Actions that are performed on each count of the timer.
 * @param tickInterval Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
 * @param valueVariable Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command.
 */
@Generated
fun DivScope.timerProps(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    endActions: List<Action>? = null,
    id: String? = null,
    tickActions: List<Action>? = null,
    tickInterval: Int? = null,
    valueVariable: String? = null,
) = Timer.Properties(
    duration = valueOrNull(duration),
    endActions = valueOrNull(endActions),
    id = valueOrNull(id),
    tickActions = valueOrNull(tickActions),
    tickInterval = valueOrNull(tickInterval),
    valueVariable = valueOrNull(valueVariable),
)

/**
 * @param duration Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
 * @param endActions Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received.
 * @param id Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
 * @param tickActions Actions that are performed on each count of the timer.
 * @param tickInterval Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
 * @param valueVariable Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command.
 */
@Generated
fun TemplateScope.timerRefs(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    id: ReferenceProperty<String>? = null,
    tickActions: ReferenceProperty<List<Action>>? = null,
    tickInterval: ReferenceProperty<Int>? = null,
    valueVariable: ReferenceProperty<String>? = null,
) = Timer.Properties(
    duration = duration,
    endActions = endActions,
    id = id,
    tickActions = tickActions,
    tickInterval = tickInterval,
    valueVariable = valueVariable,
)

/**
 * @param duration Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
 * @param endActions Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received.
 * @param id Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
 * @param tickActions Actions that are performed on each count of the timer.
 * @param tickInterval Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
 * @param valueVariable Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command.
 */
@Generated
fun Timer.override(
    `use named arguments`: Guard = Guard.instance,
    duration: Int? = null,
    endActions: List<Action>? = null,
    id: String? = null,
    tickActions: List<Action>? = null,
    tickInterval: Int? = null,
    valueVariable: String? = null,
): Timer = Timer(
    Timer.Properties(
        duration = valueOrNull(duration) ?: properties.duration,
        endActions = valueOrNull(endActions) ?: properties.endActions,
        id = valueOrNull(id) ?: properties.id,
        tickActions = valueOrNull(tickActions) ?: properties.tickActions,
        tickInterval = valueOrNull(tickInterval) ?: properties.tickInterval,
        valueVariable = valueOrNull(valueVariable) ?: properties.valueVariable,
    )
)

/**
 * @param duration Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
 * @param endActions Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received.
 * @param id Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
 * @param tickActions Actions that are performed on each count of the timer.
 * @param tickInterval Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
 * @param valueVariable Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command.
 */
@Generated
fun Timer.defer(
    `use named arguments`: Guard = Guard.instance,
    duration: ReferenceProperty<Int>? = null,
    endActions: ReferenceProperty<List<Action>>? = null,
    id: ReferenceProperty<String>? = null,
    tickActions: ReferenceProperty<List<Action>>? = null,
    tickInterval: ReferenceProperty<Int>? = null,
    valueVariable: ReferenceProperty<String>? = null,
): Timer = Timer(
    Timer.Properties(
        duration = duration ?: properties.duration,
        endActions = endActions ?: properties.endActions,
        id = id ?: properties.id,
        tickActions = tickActions ?: properties.tickActions,
        tickInterval = tickInterval ?: properties.tickInterval,
        valueVariable = valueVariable ?: properties.valueVariable,
    )
)

/**
 * @param duration Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
 * @param tickInterval Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
 */
@Generated
fun Timer.evaluate(
    `use named arguments`: Guard = Guard.instance,
    duration: ExpressionProperty<Int>? = null,
    tickInterval: ExpressionProperty<Int>? = null,
): Timer = Timer(
    Timer.Properties(
        duration = duration ?: properties.duration,
        endActions = properties.endActions,
        id = properties.id,
        tickActions = properties.tickActions,
        tickInterval = tickInterval ?: properties.tickInterval,
        valueVariable = properties.valueVariable,
    )
)

@Generated
fun Timer.asList() = listOf(this)
