package com.yandex.div.compose.views.input

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div2.DivFocus
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivInputEnabledTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val enabled = Variable.BooleanVariable("enabled", true)
    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = TestReporter(),
        variableController = DivVariableController().apply {
            declare(enabled, Variable.StringVariable("text", ""))
        },
    )
    private val field get() = rule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText))

    @Test
    fun `disabling focused input clears focus and dispatches blur`() {
        setContent()
        field.performSemanticsAction(SemanticsActions.RequestFocus)
        field.assertIsFocused()
        actionHandler.reset()

        enabled.set(false)

        rule.runOnIdle {
            assertEquals(actionData(id = "blur", source = DivActionSource.BLUR), actionHandler.handledAction)
        }
    }

    @Test
    fun `disabled input does not take focus`() {
        enabled.set(false)
        setContent()

        field.performClick()

        rule.runOnIdle { assertEquals(emptyList(), actionHandler.handledActions) }
    }

    @Test
    fun `enabled input takes focus again`() {
        enabled.set(false)
        setContent()
        enabled.set(true)

        field.performClick()

        field.assertIsFocused()
    }

    private fun setContent() {
        rule.setContent(
            configuration = configuration,
            data = data(
                input(
                    focus = DivFocus(
                        onBlur = listOf(action(id = "blur")),
                        onFocus = listOf(action(id = "focus")),
                    ),
                    isEnabled = booleanExpression("@{enabled}"),
                    textVariable = "text",
                )
            ),
        )
    }
}
