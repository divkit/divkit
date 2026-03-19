package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.yandex.div.compose.DivException
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.compose.utils.divContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import javax.inject.Inject

@DivLocalScope
internal class DivLocalContext @Inject constructor(
    val actionHandlingContext: DivActionHandlingContext,
    val expressionResolver: ExpressionResolver,
    val variableController: DivVariableController
)

internal val LocalDivContext = compositionLocalOf<DivLocalContext> {
    throw DivException("DivLocalContext not provided")
}

@Composable
internal fun WithLocalDivContext(data: DivData, content: @Composable () -> Unit) {
    val divContext = divContext
    val localContext = remember(data) {
        divContext.createLocalContext(
            baseVariableController = divContext.component.variableController,
            variables = data.variables.orEmpty()
        )
    }
    CompositionLocalProvider(LocalDivContext provides localContext, content)
}

@Composable
internal fun WithLocalDivContext(data: DivBase, content: @Composable () -> Unit) {
    val variables = data.variables.orEmpty()
    if (variables.isEmpty()) {
        return content()
    }

    val divContext = divContext
    val localContext = LocalDivContext.current
    val newLocalContext = remember(data) {
        divContext.createLocalContext(
            baseVariableController = localContext.variableController,
            variables = variables
        )
    }
    CompositionLocalProvider(LocalDivContext provides newLocalContext, content)
}
