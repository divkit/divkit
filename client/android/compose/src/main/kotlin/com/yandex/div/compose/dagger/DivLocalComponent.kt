package com.yandex.div.compose.dagger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.expressions.ExpressionCache
import com.yandex.div.compose.internal.NetworkRestorationController
import com.yandex.div.compose.triggers.DivTriggerStorage
import com.yandex.div.compose.triggers.observe
import com.yandex.div.compose.variables.DivVariableAdapter
import com.yandex.div.compose.variables.VariableAndConstantProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div2.DivAction
import com.yandex.div2.DivBase
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component

@DivLocalScope
@Component(
    isRoot = false,
    modules = [
        DivLocalModule::class
    ]
)
internal interface DivLocalComponent {

    val actionHandler: DivActionHandler
    val actionHandlingContext: DivActionHandlingContext
    val expressionCache: ExpressionCache
    val expressionResolver: DivComposeExpressionResolver
    val functionProvider: FunctionProviderDecorator
    val networkRestorationController: NetworkRestorationController
    val reporter: DivReporter
    val triggerStorage: DivTriggerStorage
    val variableAdapter: DivVariableAdapter
    val variableProvider: VariableAndConstantProvider

    @Component.Builder
    interface Builder {
        fun build(
            @BindsInstance functionProvider: FunctionProviderDecorator,
            @BindsInstance variableProvider: VariableAndConstantProvider
        ): DivLocalComponent
    }
}

internal fun DivLocalComponent.handleActions(
    actions: List<DivAction>,
    source: DivActionSource,
) {
    actionHandler.handle(actionHandlingContext, actions, source)
}

/**

 * An element with its own variables, functions or triggers is resolved in its own scope.

 */
internal val DivBase.declaresLocalScope: Boolean
    get() = !variables.isNullOrEmpty() || !functions.isNullOrEmpty() ||
        !variableTriggers.isNullOrEmpty()

internal val LocalComponent = staticCompositionLocalOf<DivLocalComponent> {
    throw DivException("DivLocalComponent not provided")
}

@Composable
internal inline fun WithLocalComponent(data: DivBase, crossinline content: @Composable () -> Unit) {
    if (!data.declaresLocalScope) {
        return content()
    }

    val localComponent = LocalDivViewContext.current.getLocalComponent(
        data,
        parentComponent = LocalComponent.current
    )
    if (localComponent === LocalComponent.current) {
        return content()
    }
    localComponent.triggerStorage.observe()
    CompositionLocalProvider(LocalComponent provides localComponent) {
        content()
    }
}

@Composable
internal inline fun WithLocalComponent(
    component: DivLocalComponent?,
    crossinline content: @Composable () -> Unit,
) {
    if (component == null || component === LocalComponent.current) {
        return content()
    }
    CompositionLocalProvider(LocalComponent provides component) { content() }
}
