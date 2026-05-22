package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.compose.views.input.DivInputState
import com.yandex.div.compose.views.input.rememberDivInputState
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.data.Variable
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask
import com.yandex.div2.DivPhoneInputMask
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class PhoneMaskTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val localComponent = mockLocalComponent(
        reporter = reporter,
        variableController = variableController,
    )

    private val rawVar = Variable.StringVariable(RAW_VAR, "")
    private val displayVar = Variable.StringVariable(DISPLAY_VAR, "")

    init {
        variableController.declare(rawVar, displayVar)
    }

    @Test
    fun `typing digits writes raw and formats display`() {
        var state: DivInputState? = null
        setContent { state = phoneInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("79161234567", TextRange(11)))
        composeRule.waitForIdle()

        assertEquals("79161234567", state!!.text.text)
        assertEquals("79161234567", rawVar.getValue())
        assertEquals("+7 (916) 123-45-67", displayVar.getValue())
        assertEquals("+7 (916) 123-45-67", state!!.formattedDisplay())
    }

    @Test
    fun `non-digit characters are dropped on`() {
        var state: DivInputState? = null
        setContent { state = phoneInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("+7 (916) 123-45-67", TextRange(18)))
        composeRule.waitForIdle()

        assertEquals("79161234567", state!!.text.text)
        assertEquals("79161234567", rawVar.getValue())
    }

    @Test
    fun `partial input keeps mask scaffolding for display`() {
        var state: DivInputState? = null
        setContent { state = phoneInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("7916", TextRange(4)))
        composeRule.waitForIdle()

        assertEquals("7916", state!!.text.text)
        assertEquals("+7 (916) ", state!!.formattedDisplay())
    }

    @Test
    fun `external raw variable update reformats display`() {
        var state: DivInputState? = null
        setContent { state = phoneInput().rememberDivInputState() }

        rawVar.set("79161234567")
        composeRule.waitForIdle()

        assertEquals("79161234567", state!!.text.text)
        assertEquals("+7 (916) 123-45-67", displayVar.getValue())
    }

    @Test
    fun `removing trailing digit shrinks raw and formatted output`() {
        var state: DivInputState? = null
        setContent { state = phoneInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("79161234567", TextRange(11)))
        composeRule.waitForIdle()

        val current = state!!.text
        val deleted = current.text.dropLast(1)
        state!!.onValueChange(TextFieldValue(deleted, TextRange(deleted.length)))
        composeRule.waitForIdle()

        assertEquals("7916123456", state!!.text.text)
        assertEquals("7916123456", rawVar.getValue())
    }

    private fun DivInputState.formattedDisplay(): String =
        visualTransformation.filter(AnnotatedString(text.text)).text.text

    private fun phoneInput(): DivInput = DivInput(
        textVariable = DISPLAY_VAR,
        mask = DivInputMask.Phone(DivPhoneInputMask(rawTextVariable = RAW_VAR)),
    )

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}

private const val RAW_VAR = "phone_raw"
private const val DISPLAY_VAR = "phone_display"
