package com.yandex.div.compose.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.DivReporter
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
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@DivLocalScope
@GraphExtension(
    bindingContainers = [
        DivLocalModule::class
    ]
)
internal interface DivLocalComponent : DivViewDependencies {

    val actionHandlingContext: DivActionHandlingContext
    val expressionCache: ExpressionCache
    val expressionResolver: DivComposeExpressionResolver
    val functionProvider: FunctionProviderDecorator
    val networkRestorationController: NetworkRestorationController
    val reporter: DivReporter
    val triggerStorage: DivTriggerStorage
    val variableAdapter: DivVariableAdapter
    val variableProvider: VariableAndConstantProvider

    @GraphExtension.Factory
    interface Builder {
        fun build(
            @Provides functionProvider: FunctionProviderDecorator,
            @Provides variableProvider: VariableAndConstantProvider
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
internal inline fun WithLocalComponent(
    data: DivBase,
    crossinline content: @Composable () -> Unit
) {
    if (!data.declaresLocalScope) {
        return content()
    }

    val parentComponent = LocalComponent.current
    val component = LocalDivViewContext.current.getLocalComponent(
        data = data,
        parentComponent = parentComponent
    )
    if (component === parentComponent) {
        return content()
    }
    component.triggerStorage.observe()
    CompositionLocalProvider(LocalComponent provides component) {
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
