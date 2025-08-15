// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivData internal constructor(
    @JsonIgnore val logId: Property<String>?,
    @JsonIgnore val states: Property<List<State>>?,
    @JsonIgnore val transitionAnimationSelector: Property<DivTransitionSelector>?,
    @JsonIgnore val variableTriggers: Property<List<DivTrigger>>?,
    @JsonIgnore val variables: Property<List<DivVariable>>?,
) : Root {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "log_id" to logId,
            "states" to states,
            "transition_animation_selector" to transitionAnimationSelector,
            "variable_triggers" to variableTriggers,
            "variables" to variables,
        )
    }

    class State internal constructor(
        @JsonIgnore val div: Property<Div>?,
        @JsonIgnore val stateId: Property<Int>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "div" to div,
                "state_id" to stateId,
            )
        }
    }
}

fun CardContext.divData(
    logId: ValueProperty<String>,
    states: ValueProperty<List<DivData.State>>,
    transitionAnimationSelector: ValueProperty<DivTransitionSelector>? = null,
    variableTriggers: ValueProperty<List<DivTrigger>>? = null,
    variables: ValueProperty<List<DivVariable>>? = null,
): DivData {
    return DivData(
        logId = logId,
        states = states,
        transitionAnimationSelector = transitionAnimationSelector,
        variableTriggers = variableTriggers,
        variables = variables,
    )
}

fun CardContext.divData(
    logId: String,
    states: List<DivData.State>,
    transitionAnimationSelector: DivTransitionSelector? = null,
    variableTriggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
): DivData {
    return DivData(
        logId = value(logId),
        states = value(states),
        transitionAnimationSelector = optionalValue(transitionAnimationSelector),
        variableTriggers = optionalValue(variableTriggers),
        variables = optionalValue(variables),
    )
}

fun CardContext.state(
    div: ValueProperty<Div>,
    stateId: ValueProperty<Int>,
): DivData.State {
    return DivData.State(
        div = div,
        stateId = stateId,
    )
}

fun CardContext.state(
    div: Div,
    stateId: Int,
): DivData.State {
    return DivData.State(
        div = value(div),
        stateId = value(stateId),
    )
}
