package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.internal.NetworkRestorationController
import com.yandex.div.compose.state.DivStateStorage
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
    variableController: DivVariableController
): DivComposeExpressionResolver {
    return DivComposeExpressionResolver(
        functionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider),
        reporter = reporter,
        storedValueProvider = storedValueProvider,
        variableController = variableController
    )
}

internal fun mockViewContext(
    stateStorage: DivStateStorage = DivStateStorage(),
): DivViewContext {
    return mock<DivViewContext> {
        on { this.stateStorage } doReturn stateStorage
    }
}

internal fun mockLocalComponent(
    reporter: DivReporter = TestReporter(),
    variableController: DivVariableController = DivVariableController(),
    networkRestorationController: NetworkRestorationController? = null,
): DivLocalComponent {
    val expressionResolver = createExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )
    val actionHandlingContext = DivActionHandlingContext(
        cardId = "test",
        expressionResolver = expressionResolver
    )
    return mock<DivLocalComponent> {
        on { this.actionHandlingContext } doReturn actionHandlingContext
        on { this.expressionResolver } doReturn expressionResolver
        on { this.reporter } doReturn reporter
        on { this.variableController } doReturn variableController
        networkRestorationController?.let {
            on { this.networkRestorationController } doReturn it
        }
    }
}

fun ComposeContentTestRule.setContentWithDivContext(
    configuration: DivComposeConfiguration,
    debugConfiguration: DivDebugConfiguration = DivDebugConfiguration(),
    content: @Composable () -> Unit
) {
    setContent {
        val divContext = DivContext(
            baseContext = LocalContext.current,
            configuration = configuration,
            debugConfiguration = debugConfiguration
        )
        CompositionLocalProvider(LocalContext provides divContext) {
            content()
        }
    }
}

fun ComposeContentTestRule.setContent(
    configuration: DivComposeConfiguration,
    debugConfiguration: DivDebugConfiguration = DivDebugConfiguration(),
    data: DivData
) {
    setContentWithDivContext(
        configuration = configuration,
        debugConfiguration = debugConfiguration
    ) {
        DivView(data = data)
    }
}
