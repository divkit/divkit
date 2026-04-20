package com.yandex.div.compose.dagger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.triggers.DivTriggerStorage
import com.yandex.div.compose.triggers.observe
import com.yandex.div.compose.variables.DivVariableAdapter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import kotlin.collections.orEmpty

@DivLocalScope
@Component(
    isRoot = false,
    modules = [
        DivLocalModule::class
    ]
)
internal interface DivLocalComponent {

    val actionHandlingContext: DivActionHandlingContext
    val expressionResolver: ExpressionResolver
    val functionProvider: FunctionProviderDecorator
    val reporter: DivReporter
    val triggerStorage: DivTriggerStorage
    val variableAdapter: DivVariableAdapter
    val variableController: DivVariableController

    @Component.Builder
    interface Builder {
        fun build(
            @BindsInstance functionProvider: FunctionProviderDecorator,
            @BindsInstance variableController: DivVariableController
        ): DivLocalComponent
    }
}

internal val LocalComponent = compositionLocalOf<DivLocalComponent> {
    throw DivException("DivLocalComponent not provided")
}

@Composable
internal fun WithLocalComponent(data: DivBase, content: @Composable () -> Unit) {
    val functions = data.functions.orEmpty()
    val variables = data.variables.orEmpty()
    val triggers = data.variableTriggers.orEmpty()
    if (functions.isEmpty() && variables.isEmpty() && triggers.isEmpty()) {
        return content()
    }

    val localComponent = LocalDivViewContext.current.getLocalComponent(
        data,
        parentComponent = LocalComponent.current
    )
    localComponent.triggerStorage.observe()
    CompositionLocalProvider(LocalComponent provides localComponent, content)
}
