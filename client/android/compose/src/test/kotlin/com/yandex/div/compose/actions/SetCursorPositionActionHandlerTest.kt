package com.yandex.div.compose.actions

import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setCursorPositionAction
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class SetCursorPositionActionHandlerTest {
    private val environment = ActionHandlerEnvironment()
    private val storage = FocusBindingStorage(environment.reporter)
    private val handler = SetCursorPositionActionHandler(storage, environment.reporter)
    private val input = FakeInput(shownLength = 17)

    @BeforeTest
    fun setUp() {
        environment.init(setCursorPositionActionHandler = handler)
        storage.register("input", FocusBindingStorage.InputTarget(mock(), input))
    }

    @Test
    fun `range of the shown text is selected`() {
        environment.handle(action(typed = setCursorPositionAction("input", start = 7, end = 12)))

        assertEquals(TextRange(7, 12), input.selection)
    }

    @Test
    fun `end defaults to start`() {
        environment.handle(action(typed = setCursorPositionAction("input", start = 7)))

        assertEquals(TextRange(7), input.selection)
    }

    @Test
    fun `minus one is the end of the text`() {
        environment.handle(action(typed = setCursorPositionAction("input", start = 7, end = -1)))

        assertEquals(TextRange(7, 17), input.selection)
    }

    @Test
    fun `reversed range is selected as given`() {
        environment.handle(action(typed = setCursorPositionAction("input", start = 12, end = 7)))

        assertEquals(TextRange(12, 7), input.selection)
    }

    @Test
    fun `start behind the text is rejected`() {
        environment.reporter.failOnError = false

        environment.handle(action(typed = setCursorPositionAction("input", start = 18)))

        assertEquals(listOf("Wrong start value 18"), environment.reporter.errors)
        assertNull(input.selection)
    }

    @Test
    fun `negative start other than minus one is rejected`() {
        environment.reporter.failOnError = false

        environment.handle(action(typed = setCursorPositionAction("input", start = -2)))

        assertEquals(listOf("Wrong start value -2"), environment.reporter.errors)
        assertNull(input.selection)
    }

    @Test
    fun `end behind the text is rejected`() {
        environment.reporter.failOnError = false

        environment.handle(action(typed = setCursorPositionAction("input", start = 7, end = 18)))

        assertEquals(listOf("Wrong end value 18"), environment.reporter.errors)
        assertNull(input.selection)
    }

    @Test
    fun `missing input is reported`() {
        environment.reporter.failOnError = false

        environment.handle(action(typed = setCursorPositionAction("missing", start = 0)))

        assertEquals(listOf("Element with id 'missing' not found"), environment.reporter.errors)
    }

    @Test
    fun `element that is not an input is not found`() {
        environment.reporter.failOnError = false
        storage.register("text", FocusBindingStorage.Target(mock()))

        environment.handle(action(typed = setCursorPositionAction("text", start = 0)))

        assertEquals(listOf("Element with id 'text' not found"), environment.reporter.errors)
    }

    @Test
    fun `input is found next to another element with its id`() {
        storage.register("input", FocusBindingStorage.Target(mock()))

        environment.handle(action(typed = setCursorPositionAction("input", start = 7, end = 12)))

        assertEquals(TextRange(7, 12), input.selection)
    }

    @Test
    fun `two inputs with one id are ambiguous`() {
        environment.reporter.failOnError = false
        storage.register("input", FocusBindingStorage.InputTarget(mock(), FakeInput(shownLength = 17)))

        environment.handle(action(typed = setCursorPositionAction("input", start = 7)))

        assertEquals(listOf("Element with id 'input' is ambiguous"), environment.reporter.errors)
        assertNull(input.selection)
    }

    @Test
    fun `id is evaluated for each action`() {
        val target = Variable.StringVariable("target", "input")
        environment.variableController.declare(target)
        val second = FakeInput(shownLength = 17)
        storage.register("second", FocusBindingStorage.InputTarget(mock(), second))
        val action = action(typed = setCursorPositionAction(expression("@{target}"), constant(7L)))

        environment.handle(action)
        target.setValueDirectly("second")
        environment.handle(action)

        assertEquals(TextRange(7), input.selection)
        assertEquals(TextRange(7), second.selection)
    }

    @Test
    fun `positions are evaluated with the action context`() {
        environment.variableController.declare(Variable.IntegerVariable("start", 7))

        val typed = setCursorPositionAction(
            id = constant("input"),
            start = intExpression("@{start}"),
            end = intExpression("@{start + 5}"),
        )

        environment.handle(action(typed = typed))

        assertEquals(TextRange(7, 12), input.selection)
    }

    private class FakeInput(private val shownLength: Int) : FocusBindingStorage.InputText {
        var selection: TextRange? = null

        override fun length(): Int = shownLength

        override fun setCursorPosition(start: Int, end: Int) {
            selection = TextRange(start, end)
        }
    }
}
