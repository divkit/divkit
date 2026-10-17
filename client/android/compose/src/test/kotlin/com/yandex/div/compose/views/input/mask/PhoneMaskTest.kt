package com.yandex.div.compose.views.input.mask

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div2.DivInputMask
import com.yandex.div2.DivPhoneInputMask
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class PhoneMaskTest {

    @get:Rule
    val rule = createComposeRule()

    private val rawVar = Variable.StringVariable("phone_raw", "")
    private val displayVar = Variable.StringVariable("phone_display", "")
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(rawVar, displayVar) },
    )
    private val field get() = rule.onNode(hasSetTextAction())

    @Test
    fun `typing digits writes raw and formats display`() {
        setContent()

        field.performTextInput("79161234567")

        field.assertTextEquals("+7 (916) 123-45-67")
        assertEquals("79161234567", rawVar.getValue())
        assertEquals("+7 (916) 123-45-67", displayVar.getValue())
    }

    @Test
    fun `non-digit characters are dropped on`() {
        setContent()

        field.performTextInput("+7 (916) 123-45-67")

        field.assertTextEquals("+7 (916) 123-45-67")
        assertEquals("79161234567", rawVar.getValue())
    }

    @Test
    fun `partial input keeps mask scaffolding for display`() {
        setContent()

        field.performTextInput("7916")

        field.assertTextEquals("+7 (916) ")
    }

    @Test
    fun `external raw variable update reformats display`() {
        setContent()

        rawVar.set("79161234567")

        field.assertTextEquals("+7 (916) 123-45-67")
        assertEquals("+7 (916) 123-45-67", displayVar.getValue())
    }

    @Test
    fun `removing trailing digit shrinks raw and formatted output`() {
        setContent()
        field.performTextInput("79161234567")

        field.performKeyInput { pressKey(Key.Backspace) }

        field.assertTextEquals("+7 (916) 123-45-6")
        assertEquals("7916123456", rawVar.getValue())
    }

    @Test
    fun `digits beyond the mask are dropped`() {
        setContent()

        field.performTextInput("791612345678901234")

        field.assertTextEquals("+7 (916) 123-45-6789")
        assertEquals("7916123456789", rawVar.getValue())
    }

    private fun setContent() {
        rule.setContent(
            configuration = configuration,
            data = data(
                input(
                    mask = DivInputMask.Phone(DivPhoneInputMask(rawTextVariable = "phone_raw")),
                    textVariable = "phone_display",
                )
            ),
        )
    }
}
