package com.yandex.div.compose.views

import androidx.compose.runtime.compositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.variables.DivVariableAdapter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject
import javax.inject.Named

@DivViewScope
internal class DivViewContext @Inject constructor(
    val expressionResolver: ExpressionResolver,
    val reporter: DivReporter,
    val variableAdapter: DivVariableAdapter,
    @param:Named(Names.CARD_VARIABLES) val variableController: DivVariableController
) {

    val actionHandlingContext: DivActionHandlingContext
        get() = DivActionHandlingContext(
            expressionResolver = expressionResolver
        )
}

internal val LocalDivViewContext = compositionLocalOf<DivViewContext> {
    throw DivException("DivViewContext not provided")
}
