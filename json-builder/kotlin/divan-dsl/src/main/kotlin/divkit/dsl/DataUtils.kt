package divkit.dsl

import divkit.dsl.core.Guard
import divkit.dsl.core.value
import divkit.dsl.scope.DivScope

/**
 * @param div Contents.
 */
fun DivScope.singleRoot(div: Div): List<Data.State> =
    Data.State(
        Data.State.Properties(
            stateId = value(0),
            div = value(div),
        )
    ).asList()

/**
 * Creates [Data] with single state.
 */
fun DivScope.data(
    @Suppress("UNUSED_PARAMETER") `use named arguments`: Guard = Guard.instance,
    logId: String,
    div: Div,
    timers: List<Timer>? = null,
    variableTriggers: List<Trigger>? = null,
    variables: List<Variable>? = null,
): Data {
    return data(
        logId = logId,
        states = singleRoot(
            div = div
        ),
        timers = timers,
        variableTriggers = variableTriggers,
        variables = variables
    )
}
