package com.yandex.div.compose.focus

import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.viewinterop.AndroidView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div.test.data.text
import com.yandex.div2.DivFocus
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import kotlin.test.Test

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

    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(Variable.StringVariable("input", "")) },
    )
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
            AndroidView(factory = { context ->
                LinearLayout(context).apply {
                    addView(ComposeView(context).apply { setContent { DivView(data(input(textVariable = "input"))) } })
                }
            })
        }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.runOnIdle { inputFocus.clear() }

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    private fun setContent(content: @Composable () -> Unit) {
        rule.setContentWithDivContext(configuration) {
            inputFocus = divContext.component.inputFocus
            content()
        }
    }
}
