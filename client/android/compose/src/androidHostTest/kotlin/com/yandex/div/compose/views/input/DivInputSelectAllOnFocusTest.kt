package com.yandex.div.compose.views.input

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivInputSelectAllOnFocusTest {

    @get:Rule
    val rule = createComposeRule()

    private val source = Variable.StringVariable("text", "abc")
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(source) },
    )
    private val field get() = rule.onNode(hasSetTextAction())

    @Test
    fun `tap selects all text`() {
        setContent(selectAllOnFocus = true)

        field.performTouchInput { click(centerLeft) }

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0, 3)))
    }

    @Test
    fun `focus request selects all text`() {
        setContent(selectAllOnFocus = true)

        field.performSemanticsAction(SemanticsActions.RequestFocus)

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0, 3)))
    }

    @Test
    fun `tap places cursor without select all on focus`() {
        setContent(selectAllOnFocus = false)

        field.performTouchInput { click(centerLeft) }

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0)))
    }

    @Test
    fun `empty text stays empty on focus`() {
        source.set("")
        setContent(selectAllOnFocus = true)

        field.performSemanticsAction(SemanticsActions.RequestFocus)

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0)))
    }

    @Test
    fun `enabling select all on focus applies on the next focus`() {
        val selectAll = Variable.BooleanVariable("select_all", false)
        configuration.variableController.declare(selectAll)
        rule.setContent(
            configuration = configuration,
            data = data(
                container(
                    items = listOf(
                        input(selectAllOnFocus = booleanExpression("@{select_all}"), textVariable = "text"),
                        input(textVariable = "text"),
                    )
                )
            ),
        )
        val first = rule.onAllNodes(hasSetTextAction()).onFirst()
        first.performSemanticsAction(SemanticsActions.RequestFocus)
        rule.waitForIdle()

        selectAll.set(true)

        first.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(3)))
        rule.onAllNodes(hasSetTextAction()).onLast().performSemanticsAction(SemanticsActions.RequestFocus)
        first.performSemanticsAction(SemanticsActions.RequestFocus)
        first.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0, 3)))
    }

    private fun setContent(selectAllOnFocus: Boolean) {
        rule.setContent(
            configuration = configuration,
            data = data(input(selectAllOnFocus = constant(selectAllOnFocus), textVariable = "text")),
        )
    }
}
