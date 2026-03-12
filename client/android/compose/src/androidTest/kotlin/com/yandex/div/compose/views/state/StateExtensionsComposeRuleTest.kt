package com.yandex.div.compose.views.state

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.StoredValueProvider
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

class StateExtensionsComposeRuleTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val variables = mutableMapOf<String, Variable>()
    private val storedValueProvider = StoredValueProvider { null }

    private val evaluationContext = EvaluationContext(
        variableProvider = { variables[it]?.getValue() },
        storedValueProvider = storedValueProvider,
        functionProvider = GeneratedBuiltinFunctionProvider,
        warningSender = { _, _ -> }
    )

    private val resolver = DivComposeExpressionResolver(
        evaluator = Evaluator(evaluationContext),
        reporter = DivReporter()
    )

    @Test
    fun subscribesToExpressionInComposition() {
        variables.clear()
        variables["counter"] = Variable.IntegerVariable("counter", 1)
        val expression = expression("@{counter}")
        var observedValue by mutableLongStateOf(0L)

        setDivContent(resolver = resolver) {
            observedValue = expression.observeAsValue()
        }

        composeRule.runOnIdle {
            assertEquals(1L, observedValue)
            variables["counter"]?.set("2")
            resolver.notifyVariableChanged("counter")
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(2L, observedValue)
        }
    }

    @Test
    fun unsubscribesWhenComposableLeavesComposition() {
        variables.clear()
        variables["counter"] = Variable.IntegerVariable("counter", 1)
        val expression = expression("@{counter}")
        val showExpression = mutableStateOf(true)
        var observedValue by mutableLongStateOf(0L)

        setDivContent(resolver = resolver) {
            if (showExpression.value) {
                observedValue = expression.observeAsValue()
            }
        }

        composeRule.runOnIdle {
            assertEquals(1L, observedValue)
            showExpression.value = false
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            variables["counter"]?.set("2")
            resolver.notifyVariableChanged("counter")
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(1L, observedValue)
        }
    }

    @Test
    fun switchesSubscriptionWhenExpressionInstanceChanges() {
        variables.clear()
        variables["counter_a"] = Variable.IntegerVariable("counter_a", 1)
        variables["counter_b"] = Variable.IntegerVariable("counter_b", 10)
        val expressionA = expression("@{counter_a}")
        val expressionB = expression("@{counter_b}")
        val useSecondExpression = mutableStateOf(false)
        var observedValue by mutableLongStateOf(0L)

        setDivContent(resolver = resolver) {
            val currentExpression = if (useSecondExpression.value) expressionB else expressionA
            observedValue = currentExpression.observeAsValue()
        }

        composeRule.runOnIdle {
            assertEquals(1L, observedValue)
            useSecondExpression.value = true
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(10L, observedValue)
            variables["counter_a"]?.set("2")
            resolver.notifyVariableChanged("counter_a")
            variables["counter_b"]?.set("20")
            resolver.notifyVariableChanged("counter_b")
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(20L, observedValue)
        }
    }

    @Test
    fun mutableExpressionUpdatesStateWhenResolverVariableChanges() {
        variables.clear()
        variables["counter"] = Variable.IntegerVariable("counter", 1)
        val expression = expression("@{counter}")
        var observedValue by mutableLongStateOf(0L)

        setDivContent(resolver = resolver) {
            observedValue = expression.observeAsValue()
        }

        composeRule.runOnIdle {
            assertEquals(1L, observedValue)
            variables["counter"]?.set("2")
            resolver.notifyVariableChanged("counter")
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(2L, observedValue)
        }
    }

    private fun expression(rawExpression: String) = Expression.MutableExpression<Long, Long>(
        expressionKey = "test",
        rawExpression = rawExpression,
        validator = { true },
        converter = { it },
        logger = { fail(it.message) },
        typeHelper = TYPE_HELPER_INT,
    )

    private fun setDivContent(
        resolver: ExpressionResolver = ExpressionResolver.EMPTY,
        content: @androidx.compose.runtime.Composable () -> Unit
    ) {
        composeRule.setContent {
            val baseContext = LocalContext.current
            val divContext = remember(baseContext, resolver) {
                DivContext(
                    baseContext = baseContext,
                    expressionResolver = resolver,
                    reporter = DivReporter()
                )
            }
            CompositionLocalProvider(LocalContext provides divContext) {
                content()
            }
        }
    }
}
