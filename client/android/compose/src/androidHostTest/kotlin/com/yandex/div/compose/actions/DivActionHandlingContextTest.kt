package com.yandex.div.compose.actions

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actionData
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div2.DivAction
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivActionHandlingContextTest {
    private val reporter = mock<DivReporter>()
    private val counter = Variable.StringVariable("counter", "before")
    private val variableController = DivVariableController().apply { declare(counter) }
    private val externalActionHandler = mock<DivExternalActionHandler>()
    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(
            reporter = reporter,
            variableController = variableController,
            actionHandler = externalActionHandler,
        ),
    )

    private val setCounter = action(
        scopeId = "target",
        typed = setVariableAction("counter", typedValue("after")),
    )
    private val firstTarget = text(id = "target", text = "First", variables = listOf(variable("counter", "first")))
    private val secondTarget = text(id = "target", text = "Second", variables = listOf(variable("counter", "second")))
    private val ambiguousView by lazy {
        divContext.getViewContext(data(container(items = listOf(firstTarget, secondTarget)))).apply {
            getOwnComponent(firstTarget.value(), rootLocalComponent)
            getOwnComponent(secondTarget.value(), rootLocalComponent)
        }
    }
    private val nearTarget = text(id = "target", text = "First", variables = listOf(variable("counter", 100L)))
    private val farTarget = text(id = "target", text = "Second", variables = listOf(variable("counter", 200L)))
    private val sourceBranch = container(
        variables = listOf(variable("branch", "first")),
        items = listOf(nearTarget),
    )
    private val propertySetterView by lazy {
        divContext.getViewContext(data(container(items = listOf(sourceBranch, farTarget)))).apply {
            val source = getOwnComponent(sourceBranch.value(), rootLocalComponent)
            getOwnComponent(nearTarget.value(), source)
            getOwnComponent(farTarget.value(), rootLocalComponent)
        }
    }
    private val propertySource by lazy {
        propertySetterView.getOwnComponent(sourceBranch.value(), propertySetterView.rootLocalComponent)
    }
    private val nearScope by lazy { propertySetterView.getOwnComponent(nearTarget.value(), propertySource) }
    private val farScope by lazy {
        propertySetterView.getOwnComponent(farTarget.value(), propertySetterView.rootLocalComponent)
    }

    @Test
    fun `missing scope reports a target not found error`() {
        handleActionWithMissingScope()

        assertScopeError("Element with id 'target' not found")
    }

    @Test
    fun `missing scope leaves the origin counter unchanged`() {
        handleActionWithMissingScope()

        assertEquals("before", counter.getValue())
    }

    @Test
    fun `ambiguous scope reports an ambiguous target error`() {
        handleActionWithAmbiguousScope()

        assertScopeError("Element with id 'target' is ambiguous")
    }

    @Test
    fun `ambiguous scope leaves the origin counter unchanged`() {
        handleActionWithAmbiguousScope()

        assertEquals("before", counter.getValue())
    }

    @Test
    fun `ambiguous scope leaves both target counters unchanged`() {
        handleActionWithAmbiguousScope()

        val root = ambiguousView.rootLocalComponent
        val first = ambiguousView.getOwnComponent(firstTarget.value(), root)
        val second = ambiguousView.getOwnComponent(secondTarget.value(), root)
        assertEquals("first", first.expressionResolver.getVariable("counter")?.getValue())
        assertEquals("second", second.expressionResolver.getVariable("counter")?.getValue())
    }

    @Test
    fun `scoped URL updates the target and reports the source action data`() {
        val target = text(
            id = "target",
            text = "Target",
            variables = listOf(variable("counter", "scope")),
        )
        val view = divContext.getViewContext(data(container(items = listOf(target))))
        val source = view.rootLocalComponent
        val targetComponent = view.getOwnComponent(target.value(), source)
        val scopedAction = DivAction(
            scopeId = constant("target"),
            logId = expression("@{counter}"),
            logUrl = uriExpression("https://example.com/log/@{counter}"),
            referer = uriExpression("https://example.com/referer/@{counter}"),
            url = uriExpression("div-action://set_variable?name=counter&value=updated_@{counter}"),
        )

        source.actionHandler.handle(
            context = source.actionHandlingContext,
            action = scopedAction,
            source = DivActionSource.EXTERNAL,
        )

        val triggeredAction = argumentCaptor<DivActionData>()
        verify(externalActionHandler).onActionTriggered(
            same(source.actionHandlingContext),
            triggeredAction.capture(),
        )
        assertEquals(
            actionData(
                id = "before",
                source = DivActionSource.EXTERNAL,
                url = "div-action://set_variable?name=counter&value=updated_before",
                logUrl = "https://example.com/log/before",
                referer = "https://example.com/referer/before",
            ),
            triggeredAction.firstValue,
        )
        assertEquals(
            "updated_before",
            targetComponent.expressionResolver.getVariable("counter")?.getValue(),
        )
    }

    @Test
    fun `property setter updates the nearest scope using new_value`() {
        handlePropertySetter()

        assertEquals(6L, nearScope.expressionResolver.getVariable("counter")?.getValue())
    }

    @Test
    fun `property setter leaves the farther scope unchanged`() {
        handlePropertySetter()

        assertEquals(200L, farScope.expressionResolver.getVariable("counter")?.getValue())
    }

    private fun handleActionWithMissingScope() {
        val root = divContext.getViewContext(data(text(text = "Origin"))).rootLocalComponent
        root.actionHandler.handle(root.actionHandlingContext, setCounter, DivActionSource.EXTERNAL)
    }

    private fun handleActionWithAmbiguousScope() {
        val root = ambiguousView.rootLocalComponent
        root.actionHandler.handle(root.actionHandlingContext, setCounter, DivActionSource.EXTERNAL)
    }

    private fun assertScopeError(expectedCause: String) {
        val error = argumentCaptor<Exception>()
        verify(reporter).reportError(error.capture())
        assertEquals("Failed to resolve action scope 'target'", error.firstValue.message)
        assertEquals(expectedCause, error.firstValue.cause?.message)
    }

    private fun handlePropertySetter() {
        val root = propertySetterView.rootLocalComponent
        val context = propertySource.actionHandlingContext.wrap(
            propertySource.expressionResolver.withPropertyNewValueVariable("new_value", 5L)
        )
        val action = action(
            scopeId = "target",
            typed = setVariableAction("counter", typedValue(intExpression("@{new_value + 1}"))),
        )
        root.actionHandler.handle(context, action, DivActionSource.EXTERNAL)
    }
}
