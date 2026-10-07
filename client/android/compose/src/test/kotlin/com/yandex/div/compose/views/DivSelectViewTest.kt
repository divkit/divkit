package com.yandex.div.compose.views

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.data
import com.yandex.div.test.data.select
import com.yandex.div.test.data.selectOption
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibility.Mode
import com.yandex.div2.DivAccessibility.Type
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivSelectViewTest {

    @get:Rule
    val rule = createComposeRule()

    private val value = Variable.StringVariable("choice", "first")
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(value) },
    )

    @Test
    fun `select has dropdown role without accessibility block`() {
        setContent()

        rule.onNodeWithTag("select")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
    }

    @Test
    fun `auto accessibility type preserves dropdown role`() {
        setContent(accessibility(type = Type.AUTO))

        rule.onNodeWithTag("select")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
    }

    @Test
    fun `explicit accessibility type overrides dropdown role`() {
        setContent(accessibility(type = Type.BUTTON))

        rule.onNodeWithTag("select")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }

    @Test
    fun `explicit text accessibility type has no dropdown role`() {
        setContent(accessibility(type = Type.TEXT))

        rule.onNodeWithTag("select").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    @Test
    fun `accessibility click opens regular select popup`() {
        setContent()

        rule.onNodeWithTag("select").performSemanticsAction(SemanticsActions.OnClick) { it() }

        rule.onNodeWithText("Second").assertIsDisplayed()
    }

    @Test
    fun `accessibility click opens merged select popup`() {
        setContent(accessibility(mode = Mode.MERGE))

        rule.onNodeWithTag("select").performSemanticsAction(SemanticsActions.OnClick) { it() }

        rule.onNodeWithText("Second").assertIsDisplayed()
    }

    @Test
    fun `merged select exposes current value`() {
        setContent(accessibility(mode = Mode.MERGE))

        rule.onNodeWithTag("select").assertTextEquals("First")
    }

    @Test
    fun `merged select exposes changed value`() {
        setContent(accessibility(mode = Mode.MERGE))

        rule.runOnIdle { value.set("second") }

        rule.onNodeWithTag("select").assertTextEquals("Second")
    }

    @Test
    fun `merged select exposes hint when selected option is empty`() {
        value.set("empty")
        setContent(accessibility(mode = Mode.MERGE))

        rule.onNodeWithTag("select").assertTextEquals("Choose an option")
    }

    @Test
    fun `choosing merged select option updates variable`() {
        setContent(accessibility(mode = Mode.MERGE))
        rule.onNodeWithTag("select").performSemanticsAction(SemanticsActions.OnClick) { it() }

        rule.onNodeWithText("Second").performSemanticsAction(SemanticsActions.OnClick) { it() }

        assertEquals("second", value.getValue())
    }

    @Test
    fun `excluded select has no accessibility click action`() {
        setContent(accessibility(mode = Mode.EXCLUDE))

        rule.onNodeWithTag("select").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
    }

    @Test
    fun `excluded select has no accessibility role`() {
        setContent(accessibility(mode = Mode.EXCLUDE))

        rule.onNodeWithTag("select").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    private fun setContent(accessibility: DivAccessibility? = null) {
        rule.setContent(configuration, data = data(select(
            accessibility = accessibility,
            hintText = "Choose an option",
            id = "select",
            options = listOf(
                selectOption("first", "First"),
                selectOption("second", "Second"),
                selectOption("empty", ""),
            ),
            valueVariable = value.name,
        )))
    }
}
