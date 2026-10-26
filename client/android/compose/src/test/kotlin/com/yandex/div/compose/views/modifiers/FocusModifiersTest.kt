package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.input
import com.yandex.div.test.data.text
import com.yandex.div2.DivFocus
import com.yandex.div2.DivInput
import com.yandex.div2.DivPatch
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class FocusModifiersTest {

    @get:Rule
    val rule = createComposeRule()

    private val variables = DivVariableController()
    private val configuration = divConfiguration {
        reporter = TestReporter()
        variableController = variables
    }

    @Test
    fun `forward id overrides default tab order`() {
        setContent(DivFocus.NextFocusIds(forward = constant("target")))
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.Tab) }

        rule.onNodeWithTag("target").assertIsFocused()
    }

    @Test
    fun `up id overrides spatial navigation`() {
        setContent(DivFocus.NextFocusIds(up = constant("target")))
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.DirectionUp) }

        rule.onNodeWithTag("target").assertIsFocused()
    }

    @Test
    fun `down id overrides spatial navigation`() {
        setContent(DivFocus.NextFocusIds(down = constant("target")))
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.DirectionDown) }

        rule.onNodeWithTag("target").assertIsFocused()
    }

    @Test
    fun `left id handles physical left in rtl`() {
        setContent(DivFocus.NextFocusIds(left = constant("target")), LayoutDirection.Rtl)
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.DirectionLeft) }

        rule.onNodeWithTag("target").assertIsFocused()
    }

    @Test
    fun `right id handles physical right in rtl`() {
        setContent(DivFocus.NextFocusIds(right = constant("target")), LayoutDirection.Rtl)
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.DirectionRight) }

        rule.onNodeWithTag("target").assertIsFocused()
    }

    @Test
    fun `tab keeps default order when forward id is absent`() {
        setContent(DivFocus.NextFocusIds(right = constant("target")))
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.Tab) }

        rule.onNodeWithTag("default").assertIsFocused()
    }

    @Test
    fun `forward id moves focus from text to input`() {
        variables.declare(Variable.StringVariable("input", ""))
        rule.setContent(
            configuration = configuration,
            data = data(container(items = listOf(
                text(
                    id = "source",
                    text = "Source",
                    focus = DivFocus(nextFocusIds = DivFocus.NextFocusIds(forward = constant("target"))),
                ),
                text(id = "default", text = "Default", focus = DivFocus()),
                input(id = "target", textVariable = "input"),
            ))),
        )
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.Tab) }

        rule.onNode(hasSetTextAction()).assertIsFocused()
    }

    @Test
    fun `forward id moves focus from input to text`() {
        variables.declare(Variable.StringVariable("input", ""))
        rule.setContent(
            configuration = configuration,
            data = data(container(items = listOf(
                input(
                    textVariable = "input",
                    keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT),
                    focus = DivFocus(nextFocusIds = DivFocus.NextFocusIds(forward = constant("target"))),
                ),
                text(id = "default", text = "Default", focus = DivFocus()),
                text(id = "target", text = "Target", focus = DivFocus()),
            ))),
        )
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Tab) }

        rule.onNodeWithTag("target").assertIsFocused()
    }

    @Test
    fun `forward id observes expression changes`() {
        val target = Variable.StringVariable("target_id", "default")
        variables.declare(target)
        setContent(DivFocus.NextFocusIds(forward = expression("@{target_id}")))
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.runOnIdle { target.set("target") }
        rule.waitForIdle()

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.Tab) }

        rule.onNodeWithTag("target").assertIsFocused()
    }

    @Test
    fun `forward id uses replacement target after patch`() {
        variables.declare(Variable.StringVariable("input", ""))
        val divContext = DivContext(RuntimeEnvironment.getApplication(), configuration)
        val card = data(container(items = listOf(
            text(
                id = "source",
                text = "Source",
                focus = DivFocus(nextFocusIds = DivFocus.NextFocusIds(forward = constant("target"))),
            ),
            text(id = "default", text = "Default", focus = DivFocus()),
            text(id = "target", text = "Target", focus = DivFocus()),
        )))
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(card)
            }
        }
        rule.onNodeWithTag("source").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.runOnIdle {
            runBlocking {
                assertTrue(divContext.patcher.applyPatch(card, DivPatch(listOf(
                    DivPatch.Change("target", listOf(input(id = "target", textVariable = "input"))),
                ))))
            }
        }
        rule.waitForIdle()

        rule.onNodeWithTag("source").performKeyInput { pressKey(Key.Tab) }

        rule.onNode(hasSetTextAction()).assertIsFocused()
    }

    private fun setContent(nextFocusIds: DivFocus.NextFocusIds, layoutDirection: LayoutDirection = LayoutDirection.Ltr) {
        rule.setContentWithDivContext(configuration) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                DivView(data(container(items = listOf(
                    text(id = "source", text = "Source", focus = DivFocus(nextFocusIds = nextFocusIds)),
                    text(id = "default", text = "Default", focus = DivFocus()),
                    text(id = "target", text = "Target", focus = DivFocus()),
                ))))
            }
        }
    }
}
