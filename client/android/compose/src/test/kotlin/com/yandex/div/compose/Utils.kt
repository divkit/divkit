package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.expressions.ExpressionCache
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.internal.NetworkRestorationController
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.variables.ScopedVariableProvider
import com.yandex.div.compose.variables.VariableAndConstantProvider
import com.yandex.div.compose.visibility.VisibilityActionTracker
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div2.DivData
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

internal fun createExpressionResolver(
    reporter: DivReporter = TestReporter(),
    storedValueProvider: ScopedStoredValueProvider = mock(),
    variableController: DivVariableController = DivVariableController(),
    variableProvider: VariableAndConstantProvider = rootVariableProvider(variableController),
): DivComposeExpressionResolver {
    return DivComposeExpressionResolver(
        functionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider),
        reporter = reporter,
        storedValueProvider = storedValueProvider,
        variableProvider = variableProvider,
    )
}

internal fun rootVariableProvider(
    variableController: DivVariableController = DivVariableController(),
): ScopedVariableProvider = ScopedVariableProvider(variableController, parent = null)

internal fun mockLocalComponent(
    reporter: DivReporter = TestReporter(),
    networkRestorationController: NetworkRestorationController? = null,
    stateStorage: DivStateStorage = DivStateStorage(),
    variableController: DivVariableController = DivVariableController(),
    visibilityActionTracker: VisibilityActionTracker = mock(),
): DivLocalComponent {
    val variableProvider = rootVariableProvider(variableController)
    val expressionResolver = createExpressionResolver(
        reporter = reporter,
        variableProvider = variableProvider
    )
    val actionHandlingContext = DivActionHandlingContext(
        cardId = "test",
        expressionResolver = expressionResolver
    )
    val expressionCache = ExpressionCache(expressionResolver)
    return mock<DivLocalComponent> {
        on { this.actionHandlingContext } doReturn actionHandlingContext
        on { this.expressionCache } doReturn expressionCache
        on { this.expressionResolver } doReturn expressionResolver
        on { this.reporter } doReturn reporter
        on { this.stateStorage } doReturn stateStorage
        on { this.variableProvider } doReturn variableProvider
        on { this.visibilityActionTracker } doReturn visibilityActionTracker
        networkRestorationController?.let {
            on { this.networkRestorationController } doReturn it
        }
    }
}

fun ComposeContentTestRule.setContentWithDivContext(
    configuration: DivConfiguration,
    debugConfiguration: DivDebugConfiguration = DivDebugConfiguration.Default,
    content: @Composable () -> Unit
) {
    setContent {
        val divContext = DivContext(
            baseContext = LocalContext.current,
            configuration = configuration,
            debugConfiguration = debugConfiguration
        )
        CompositionLocalProvider(LocalDivContext provides divContext) {
            content()
        }
    }
}

fun ComposeContentTestRule.setContent(
    configuration: DivConfiguration,
    debugConfiguration: DivDebugConfiguration = DivDebugConfiguration.Default,
    data: DivData
) {
    setContentWithDivContext(
        configuration = configuration,
        debugConfiguration = debugConfiguration
    ) {
        DivView(data = data)
    }
}
