package divkit.dsl

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
