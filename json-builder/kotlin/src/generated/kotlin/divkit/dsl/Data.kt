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
 * Required parameters: `states, log_id`.
 */
@Generated
data class Data internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    data class Properties internal constructor(
        /**
         * Logging ID.
         */
        val logId: Property<String>?,
        /**
         * A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
         */
        val states: Property<List<State>>?,
        /**
         * User functions.
         */
        val functions: Property<List<Function>>?,
        /**
         * List of timers.
         */
        val timers: Property<List<Timer>>?,
        /**
         * Events that trigger transition animations.
         * Default value: `none`.
         */
        @Deprecated("Marked as deprecated in the JSON schema ")
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
            result.tryPutProperty("functions", functions)
            result.tryPutProperty("timers", timers)
            result.tryPutProperty("transition_animation_selector", transitionAnimationSelector)
            result.tryPutProperty("variable_triggers", variableTriggers)
            result.tryPutProperty("variables", variables)
            return result
        }
    }

    /**
     * Can be created using the method [root].
     * 
     * Required parameters: `state_id, div`.
     */
    @Generated
    data class State internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): State = State(
            Properties(
                stateId = additive.stateId ?: properties.stateId,
                div = additive.div ?: properties.div,
            )
        )

        data class Properties internal constructor(
            /**
             * State ID.
             */
            val stateId: Property<Int>?,
            /**
             * Contents.
             */
            val div: Property<Div>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("state_id", stateId)
                result.tryPutProperty("div", div)
                return result
            }
        }
    }

}

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param functions User functions.
 * @param timers List of timers.
 * @param transitionAnimationSelector Events that trigger transition animations.
 * @param variableTriggers Triggers for changing variables.
 * @param variables Declaration of variables that can be used in an element.
 */
@Generated
fun DivScope.data(
    logId: String,
    states: List<Data.State>,
    `use named arguments`: Guard = Guard.instance,
    functions: List<Function>? = null,
    timers: List<Timer>? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
): Data = Data(
    Data.Properties(
        logId = valueOrNull(logId),
        states = valueOrNull(states),
        functions = valueOrNull(functions),
        timers = valueOrNull(timers),
        transitionAnimationSelector = valueOrNull(transitionAnimationSelector),
        variableTriggers = valueOrNull(variableTriggers),
        variables = valueOrNull(variables),
    )
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param functions User functions.
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
    functions: List<Function>? = null,
    timers: List<Timer>? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
) = Data.Properties(
    logId = valueOrNull(logId),
    states = valueOrNull(states),
    functions = valueOrNull(functions),
    timers = valueOrNull(timers),
    transitionAnimationSelector = valueOrNull(transitionAnimationSelector),
    variableTriggers = valueOrNull(variableTriggers),
    variables = valueOrNull(variables),
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param functions User functions.
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
    functions: ReferenceProperty<List<Function>>? = null,
    timers: ReferenceProperty<List<Timer>>? = null,
    transitionAnimationSelector: ReferenceProperty<TransitionSelector>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
) = Data.Properties(
    logId = logId,
    states = states,
    functions = functions,
    timers = timers,
    transitionAnimationSelector = transitionAnimationSelector,
    variableTriggers = variableTriggers,
    variables = variables,
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param functions User functions.
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
    functions: List<Function>? = null,
    timers: List<Timer>? = null,
    transitionAnimationSelector: TransitionSelector? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
): Data = Data(
    Data.Properties(
        logId = valueOrNull(logId) ?: properties.logId,
        states = valueOrNull(states) ?: properties.states,
        functions = valueOrNull(functions) ?: properties.functions,
        timers = valueOrNull(timers) ?: properties.timers,
        transitionAnimationSelector = valueOrNull(transitionAnimationSelector) ?: properties.transitionAnimationSelector,
        variableTriggers = valueOrNull(variableTriggers) ?: properties.variableTriggers,
        variables = valueOrNull(variables) ?: properties.variables,
    )
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param functions User functions.
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
    functions: ReferenceProperty<List<Function>>? = null,
    timers: ReferenceProperty<List<Timer>>? = null,
    transitionAnimationSelector: ReferenceProperty<TransitionSelector>? = null,
    variableTriggers: ReferenceProperty<List<Trigger>>? = null,
    variables: ReferenceProperty<List<Variable>>? = null,
): Data = Data(
    Data.Properties(
        logId = logId ?: properties.logId,
        states = states ?: properties.states,
        functions = functions ?: properties.functions,
        timers = timers ?: properties.timers,
        transitionAnimationSelector = transitionAnimationSelector ?: properties.transitionAnimationSelector,
        variableTriggers = variableTriggers ?: properties.variableTriggers,
        variables = variables ?: properties.variables,
    )
)

/**
 * @param logId Logging ID.
 * @param states A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
 * @param functions User functions.
 * @param timers List of timers.
 * @param transitionAnimationSelector Events that trigger transition animations.
 * @param variableTriggers Triggers for changing variables.
 * @param variables Declaration of variables that can be used in an element.
 */
@Generated
fun Data.modify(
    `use named arguments`: Guard = Guard.instance,
    logId: Property<String>? = null,
    states: Property<List<Data.State>>? = null,
    functions: Property<List<Function>>? = null,
    timers: Property<List<Timer>>? = null,
    transitionAnimationSelector: Property<TransitionSelector>? = null,
    variableTriggers: Property<List<Trigger>>? = null,
    variables: Property<List<Variable>>? = null,
): Data = Data(
    Data.Properties(
        logId = logId ?: properties.logId,
        states = states ?: properties.states,
        functions = functions ?: properties.functions,
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
        functions = properties.functions,
        timers = properties.timers,
        transitionAnimationSelector = transitionAnimationSelector ?: properties.transitionAnimationSelector,
        variableTriggers = properties.variableTriggers,
        variables = properties.variables,
    )
)

@Generated
fun Data.asList() = listOf(this)

/**
 * @param stateId State ID.
 * @param div Contents.
 */
@Generated
fun DivScope.root(
    stateId: Int,
    div: Div,
): Data.State = Data.State(
    Data.State.Properties(
        stateId = valueOrNull(stateId),
        div = valueOrNull(div),
    )
)

/**
 * @param stateId State ID.
 * @param div Contents.
 */
@Generated
fun DivScope.rootProps(
    `use named arguments`: Guard = Guard.instance,
    stateId: Int? = null,
    div: Div? = null,
) = Data.State.Properties(
    stateId = valueOrNull(stateId),
    div = valueOrNull(div),
)

/**
 * @param stateId State ID.
 * @param div Contents.
 */
@Generated
fun TemplateScope.rootRefs(
    `use named arguments`: Guard = Guard.instance,
    stateId: ReferenceProperty<Int>? = null,
    div: ReferenceProperty<Div>? = null,
) = Data.State.Properties(
    stateId = stateId,
    div = div,
)

/**
 * @param stateId State ID.
 * @param div Contents.
 */
@Generated
fun Data.State.override(
    `use named arguments`: Guard = Guard.instance,
    stateId: Int? = null,
    div: Div? = null,
): Data.State = Data.State(
    Data.State.Properties(
        stateId = valueOrNull(stateId) ?: properties.stateId,
        div = valueOrNull(div) ?: properties.div,
    )
)

/**
 * @param stateId State ID.
 * @param div Contents.
 */
@Generated
fun Data.State.defer(
    `use named arguments`: Guard = Guard.instance,
    stateId: ReferenceProperty<Int>? = null,
    div: ReferenceProperty<Div>? = null,
): Data.State = Data.State(
    Data.State.Properties(
        stateId = stateId ?: properties.stateId,
        div = div ?: properties.div,
    )
)

/**
 * @param stateId State ID.
 * @param div Contents.
 */
@Generated
fun Data.State.modify(
    `use named arguments`: Guard = Guard.instance,
    stateId: Property<Int>? = null,
    div: Property<Div>? = null,
): Data.State = Data.State(
    Data.State.Properties(
        stateId = stateId ?: properties.stateId,
        div = div ?: properties.div,
    )
)

@Generated
fun Data.State.asList() = listOf(this)
