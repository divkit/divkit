package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div2.DivData

internal fun createExpressionResolver(
    reporter: DivReporter = TestReporter(),
    variableController: DivVariableController
): DivComposeExpressionResolver {
    return DivComposeExpressionResolver(
        functionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider),
        reporter = reporter,
        variableController = variableController
    )
}

fun ComposeContentTestRule.setContent(
    configuration: DivComposeConfiguration,
    debugConfiguration: DivDebugConfiguration = DivDebugConfiguration(),
    data: DivData
) {
    setContent {
        val divContext = DivContext(
            baseContext = LocalContext.current,
            configuration = configuration,
            debugConfiguration = debugConfiguration
        )
        CompositionLocalProvider(LocalContext provides divContext) {
            DivView(data = data)
        }
    }
}
