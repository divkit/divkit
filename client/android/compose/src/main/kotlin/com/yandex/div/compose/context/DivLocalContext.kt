package com.yandex.div.compose.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.compose.triggers.DivTriggerStorage
import com.yandex.div.compose.triggers.observe
import com.yandex.div.compose.utils.divContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivLocalScope
internal class DivLocalContext @Inject constructor(
    val actionHandlingContext: DivActionHandlingContext,
    val expressionResolver: ExpressionResolver,
    val functionProvider: FunctionProviderDecorator,
    val triggerStorage: DivTriggerStorage,
    val variableController: DivVariableController
)

internal val LocalDivContext = compositionLocalOf<DivLocalContext> {
    throw DivException("DivLocalContext not provided")
}

@Composable
internal fun WithLocalDivContext(data: DivBase, content: @Composable () -> Unit) {
    val functions = data.functions.orEmpty()
    val variables = data.variables.orEmpty()
    val triggers = data.variableTriggers.orEmpty()
    if (functions.isEmpty() && variables.isEmpty() && triggers.isEmpty()) {
        return content()
    }

    val localContext = divContext.getLocalContext(
        data,
        viewContext = LocalDivViewContext.current,
        parentContext = LocalDivContext.current
    )
    localContext.triggerStorage.observe()
    CompositionLocalProvider(LocalDivContext provides localContext, content)
}
