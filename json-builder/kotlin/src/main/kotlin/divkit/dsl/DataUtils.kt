package divkit.dsl

import divkit.dsl.core.Guard
import divkit.dsl.core.valueOrNull
import divkit.dsl.scope.DivScope

/**
 * @param div Contents.
 */
fun DivScope.singleState(
    `use named arguments`: Guard = Guard.instance,
    div: Div,
): List<Data.State> = listOf(
    Data.State(
        Data.State.Properties(
            stateId = valueOrNull(0),
            div = valueOrNull(div),
        )
    )
)
