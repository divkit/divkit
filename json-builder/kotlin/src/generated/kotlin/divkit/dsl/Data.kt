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
 * Root structure.
 * 
 * Can be created using the method [data].
 * 
 * Required properties: `states, log_id`.
 */
@Generated
class Data internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    class Properties internal constructor(
        /**
         * Logging ID.
         */
        val logId: Property<String>?,
        /**
         * A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
         */
        val states: Property<List<State>>?,
        /**
         * List of timers.
         */
        val timers: Property<List<Timer>>?,
        /**
         * Events that trigger transition animations.
         * Default value: `none`.
         */
        @Deprecated("Marked as deprecated in json schema")
        val transitionAnimationSelector: Property<TransitionSelector>?,
        /**
         * Triggers for changing variables.
         */
        val variableTriggers: Property<List<Trigger>>?,
        /**
         * Declaration of variables that can be used in an element.
         */
        val variables: Property<List<Variable>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("log_id", logId)
            result.tryPutProperty("states", states)
            result.tryPutProperty("timers", timers)
            result.tryPutProperty("transition_animation_selector", transitionAnimationSelector)
            result.tryPutProperty("variable_triggers", variableTriggers)
            result.tryPutProperty("variables", variables)
            return result
        }
    }

    /**
     * Can be created using the method [dataState].
     * 
     * Required properties: `state_id, div`.
     */
    @Generated
    class State internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): State = State(
            Properties(
                div = additive.div ?: properties.div,
                stateId = additive.stateId ?: properties.stateId,
            )
        )

        class Properties internal constructor(
            /**
             * Contents.
             */
            val div: Property<Div>?,
            /**
             * State ID.
             */
            val stateId: Property<Int>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("div", div)
                result.tryPutProperty("state_id", stateId)
                return result
            }
        }
    }

}

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param timers List of timers.
 * @param transitionAnimationSelector Events that trigger transition animations.
 * @param variableTriggers Triggers for changing variables.
 * @param variables Declaration of variables that can be used in an element.
 */
@Generated
fun DivScope.data(
    `use named arguments`: Guard = Guard.instance,
    logId: String? = null,
    states: List<Data.State>? = null,
    timers: List<Timer>? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
): Data = Data(
    Data.Properties(
        logId = valueOrNull(logId),
        states = valueOrNull(states),
        timers = valueOrNull(timers),
        transitionAnimationSelector = valueOrNull(transitionAnimationSelector),
        variableTriggers = valueOrNull(variableTriggers),
        variables = valueOrNull(variables),
    )
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param timers List of timers.
 * @param transitionAnimationSelector Events that trigger transition animations.
 * @param variableTriggers Triggers for changing variables.
 * @param variables Declaration of variables that can be used in an element.
 */
@Generated
fun DivScope.dataProps(
    `use named arguments`: Guard = Guard.instance,
    logId: String? = null,
    states: List<Data.State>? = null,
    timers: List<Timer>? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
) = Data.Properties(
    logId = valueOrNull(logId),
    states = valueOrNull(states),
    timers = valueOrNull(timers),
    transitionAnimationSelector = valueOrNull(transitionAnimationSelector),
    variableTriggers = valueOrNull(variableTriggers),
    variables = valueOrNull(variables),
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param timers List of timers.
 * @param transitionAnimationSelector Events that trigger transition animations.
 * @param variableTriggers Triggers for changing variables.
 * @param variables Declaration of variables that can be used in an element.
 */
@Generated
fun TemplateScope.dataRefs(
    `use named arguments`: Guard = Guard.instance,
    logId: ReferenceProperty<String>? = null,
    states: ReferenceProperty<List<Data.State>>? = null,
    timers: ReferenceProperty<List<Timer>>? = null,
    transitionAnimationSelector: ReferenceProperty<TransitionSelector>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
) = Data.Properties(
    logId = logId,
    states = states,
    timers = timers,
    transitionAnimationSelector = transitionAnimationSelector,
    variableTriggers = variableTriggers,
    variables = variables,
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param timers List of timers.
 * @param transitionAnimationSelector Events that trigger transition animations.
 * @param variableTriggers Triggers for changing variables.
 * @param variables Declaration of variables that can be used in an element.
 */
@Generated
fun Data.override(
    `use named arguments`: Guard = Guard.instance,
    logId: String? = null,
    states: List<Data.State>? = null,
    timers: List<Timer>? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
): Data = Data(
    Data.Properties(
        logId = valueOrNull(logId) ?: properties.logId,
        states = valueOrNull(states) ?: properties.states,
        timers = valueOrNull(timers) ?: properties.timers,
        transitionAnimationSelector = valueOrNull(transitionAnimationSelector) ?: properties.transitionAnimationSelector,
        variableTriggers = valueOrNull(variableTriggers) ?: properties.variableTriggers,
        variables = valueOrNull(variables) ?: properties.variables,
    )
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param timers List of timers.
 * @param transitionAnimationSelector Events that trigger transition animations.
 * @param variableTriggers Triggers for changing variables.
 * @param variables Declaration of variables that can be used in an element.
 */
@Generated
fun Data.defer(
    `use named arguments`: Guard = Guard.instance,
    logId: ReferenceProperty<String>? = null,
    states: ReferenceProperty<List<Data.State>>? = null,
    timers: ReferenceProperty<List<Timer>>? = null,
    transitionAnimationSelector: ReferenceProperty<TransitionSelector>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
): Data = Data(
    Data.Properties(
        logId = logId ?: properties.logId,
        states = states ?: properties.states,
        timers = timers ?: properties.timers,
        transitionAnimationSelector = transitionAnimationSelector ?: properties.transitionAnimationSelector,
        variableTriggers = variableTriggers ?: properties.variableTriggers,
        variables = variables ?: properties.variables,
    )
)

/**
 * @param transitionAnimationSelector Events that trigger transition animations.
 */
@Generated
fun Data.evaluate(
    `use named arguments`: Guard = Guard.instance,
    transitionAnimationSelector: ExpressionProperty<TransitionSelector>? = null,
): Data = Data(
    Data.Properties(
        logId = properties.logId,
        states = properties.states,
        timers = properties.timers,
        transitionAnimationSelector = transitionAnimationSelector ?: properties.transitionAnimationSelector,
        variableTriggers = properties.variableTriggers,
        variables = properties.variables,
    )
)

@Generated
fun Data.asList() = listOf(this)

/**
 * @param div Contents.
 * @param stateId State ID.
 */
@Generated
fun DivScope.dataState(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    stateId: Int? = null,
): Data.State = Data.State(
    Data.State.Properties(
        div = valueOrNull(div),
        stateId = valueOrNull(stateId),
    )
)

/**
 * @param div Contents.
 * @param stateId State ID.
 */
@Generated
fun DivScope.dataStateProps(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    stateId: Int? = null,
) = Data.State.Properties(
    div = valueOrNull(div),
    stateId = valueOrNull(stateId),
)

/**
 * @param div Contents.
 * @param stateId State ID.
 */
@Generated
fun TemplateScope.dataStateRefs(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    stateId: ReferenceProperty<Int>? = null,
) = Data.State.Properties(
    div = div,
    stateId = stateId,
)

/**
 * @param div Contents.
 * @param stateId State ID.
 */
@Generated
fun Data.State.override(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    stateId: Int? = null,
): Data.State = Data.State(
    Data.State.Properties(
        div = valueOrNull(div) ?: properties.div,
        stateId = valueOrNull(stateId) ?: properties.stateId,
    )
)

/**
 * @param div Contents.
 * @param stateId State ID.
 */
@Generated
fun Data.State.defer(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    stateId: ReferenceProperty<Int>? = null,
): Data.State = Data.State(
    Data.State.Properties(
        div = div ?: properties.div,
        stateId = stateId ?: properties.stateId,
    )
)

@Generated
fun Data.State.asList() = listOf(this)
