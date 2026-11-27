package com.yandex.div.compose.focus

import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div.test.data.select
import com.yandex.div.test.data.selectOption
import com.yandex.div.test.data.text
import com.yandex.div2.DivFocus
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InputFocusTest {

    private val rule = createComposeRule()

    // Robolectric adds windows in keyboard mode unless graphics are native,
    // and there a cleared focus returns to the first focusable element.
    private val touchMode = object : ExternalResource() {
        override fun before() = InstrumentationRegistry.getInstrumentation().setInTouchMode(true)
    }

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(touchMode).around(rule)

    private val actions = TestExternalActionHandler()
    private val selectElement = select(
        id = "select",
        options = listOf(selectOption("first", "First"), selectOption("second", "Second")),
        valueVariable = "choice",
    )

    private val configuration = divConfiguration {
        actionHandler = actions
        reporter = TestReporter()
        variableController = DivVariableController().apply {
            declare(Variable.StringVariable("input", ""))
            declare(Variable.StringVariable("choice", "first"))
        }
    }
    private lateinit var inputFocus: InputFocus

    @Test
    fun `clearing removes focus from input`() {
        setContent { DivView(data(input(textVariable = "input"))) }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.runOnIdle { inputFocus.clear() }

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `clearing keeps focus that moved from input to other element`() {
        setContent {
            DivView(data(container(items = listOf(
                input(textVariable = "input"),
                text(id = "text", text = "Text", focus = DivFocus()),
            ))))
        }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.runOnIdle { inputFocus.clear() }

        rule.onNodeWithTag("text").assertIsFocused()
    }

    @Test
    fun `clearing reaches input in another ComposeView`() {
        setContent {
            val divContext = LocalDivContext.current
            AndroidView(factory = { context ->
                LinearLayout(context).apply {
                    addView(ComposeView(context).apply {
                        setContent {
                            CompositionLocalProvider(LocalDivContext provides divContext) {
                                DivView(data(input(textVariable = "input")))
                            }
                        }
                    })
                }
            })
        }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.runOnIdle { inputFocus.clear() }

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `clearing skips an input removed from the composition`() {
        val showInput = mutableStateOf(true)
        val inputCard = data(input(textVariable = "input"), logId = "input_card")
        val textCard = data(text(id = "text", text = "Text", focus = DivFocus()), logId = "text_card")
        setContent {
            if (showInput.value) DivView(inputCard)
            DivView(textCard)
        }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNode(hasSetTextAction()).assertIsFocused()

        rule.runOnIdle { showInput.value = false }
        rule.onNode(hasSetTextAction()).assertDoesNotExist()
        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.runOnIdle { inputFocus.clear() }

        rule.onNodeWithTag("text").assertIsFocused()
    }

    @Test
    fun `opening a select by touch blurs the focused input`() {
        val inputElement = input(
            textVariable = "input",
            focus = DivFocus(onBlur = listOf(action(id = "blur"))),
        )
        setContent { DivView(data(container(items = listOf(inputElement, selectElement)))) }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNode(hasSetTextAction()).assertIsFocused()

        rule.onNodeWithTag("select").performTouchInput { click() }

        rule.onNodeWithText("Second").assertIsDisplayed()
        rule.onNode(hasSetTextAction()).assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `opening a select by touch keeps focus on a non-input element`() {
        val textElement = text(id = "text", text = "Text", focus = DivFocus())
        setContent { DivView(data(container(items = listOf(textElement, selectElement)))) }
        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("text").assertIsFocused()

        rule.onNodeWithTag("select").performTouchInput { click() }

        rule.onNodeWithText("Second").assertIsDisplayed()
        rule.onNodeWithTag("text").assertIsFocused()
    }

    private fun setContent(content: @Composable () -> Unit) {
        rule.setContentWithDivContext(configuration) {
            inputFocus = LocalDivContext.current.component.inputFocus
            content()
        }
    }
}
