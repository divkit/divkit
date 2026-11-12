package com.yandex.div.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivContextTest {

    private val scope = TestScope()
    private val reporter = TestReporter()
    private val hostVariables = DivVariableController()
    private val configuration = divConfiguration {
        reporter = this@DivContextTest.reporter
        variableController = hostVariables
    }
    private val context = createContext()

    @AfterTest
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `DivContext preserves the Android ContextWrapper contract`() {
        val wrapper = assertIs<ContextWrapper>(context)

        assertSame(getApplicationContext<Context>(), wrapper.baseContext)
    }

    @Test
    fun `view context is cached for the same card`() {
        val card = data(text(text = "Card"))

        assertSame(context.getViewContext(card), context.getViewContext(card))
    }

    @Test
    fun `clearing a card creates a new view scope`() {
        val card = data(text(text = "Card"))
        val before = context.getViewContext(card).component

        context.clearViewContext(card)

        assertNotSame(before.actionHandler, context.getViewContext(card).component.actionHandler)
    }

    @Test
    fun `different cards have independent view scopes`() {
        val first = context.getViewContext(data(text(text = "First"), logId = "first"))
        val second = context.getViewContext(data(text(text = "Second"), logId = "second"))

        assertNotSame(first.component.actionHandler, second.component.actionHandler)
    }

    @Test
    fun `different contexts do not share card scopes`() {
        val card = data(text(text = "Card"))

        assertNotSame(
            context.getViewContext(card).component.actionHandler,
            createContext().getViewContext(card).component.actionHandler,
        )
    }

    @Test
    fun `root local component is stable across lazy accesses`() {
        val component = context.getViewContext(data(text(text = "Card"))).component

        assertSame(component.rootLocalComponent.value, component.rootLocalComponent.value)
    }

    @Test
    fun `local scopes reuse the card action handler`() {
        val viewContext = context.getViewContext(data(text(text = "Card")))
        val local = viewContext.getLocalComponent(
            data = text(text = "Local", variables = listOf(variable("local", 1L))).value(),
            parentComponent = viewContext.rootLocalComponent,
        )

        assertSame(viewContext.component.actionHandler, local.actionHandler)
    }

    @Test
    fun `local scopes have independent expression resolvers`() {
        val viewContext = context.getViewContext(data(text(text = "Card")))
        val first = viewContext.getLocalComponent(
            data = text(text = "First", variables = listOf(variable("local", 1L))).value(),
            parentComponent = viewContext.rootLocalComponent,
        )
        val second = viewContext.getLocalComponent(
            data = text(text = "Second", variables = listOf(variable("local", 2L))).value(),
            parentComponent = viewContext.rootLocalComponent,
        )

        assertNotSame(first.expressionResolver, second.expressionResolver)
    }

    @Test
    fun `host card and local variables keep their own values`() {
        hostVariables.declare(Variable.IntegerVariable("counter", 1L))
        val viewContext = context.getViewContext(
            data(text(text = "Card"), variables = listOf(variable("counter", 10L)))
        )
        val local = viewContext.getLocalComponent(
            data = text(text = "Local", variables = listOf(variable("counter", 30L))).value(),
            parentComponent = viewContext.rootLocalComponent,
        )

        assertEquals(1L, hostVariables.get("counter")?.getValue())
        assertEquals(10L, viewContext.rootLocalComponent.variableProvider.controller.get("counter")?.getValue())
        assertEquals(30L, local.variableProvider.controller.get("counter")?.getValue())
    }

    @Test
    fun `local component uses the configured reporter`() {
        val viewContext = context.getViewContext(data(text(text = "Card")))

        assertSame(reporter, viewContext.rootLocalComponent.reporter)
    }

    private fun createContext() = DivContext(
        baseContext = getApplicationContext(),
        configuration = configuration,
        debugConfiguration = DivDebugConfiguration(coroutineScope = scope),
    )
}
