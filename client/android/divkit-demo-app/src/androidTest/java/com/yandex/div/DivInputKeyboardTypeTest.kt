package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

internal const val TEXT_WITH_DIFFERENT_SYMBOLS =
        "https://Text_with different\nsymbols+(123)@site.ru"

private const val TEXT_WITHOUT_BREAK = "https://Text_with differentsymbols+(123)@site.ru"

class DivInputKeyboardTypeTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun checkMultiLineText() {
        checkType("multi_line_text", TEXT_WITH_DIFFERENT_SYMBOLS)
    }

    @Test
    fun checkSingleLineText() {
        checkType("single_line_text", TEXT_WITHOUT_BREAK)
    }

    @Test
    fun checkNumber() {
        checkType("number", "912302.")
    }

    @Test
    fun checkPhone() {
        checkType("phone", ";//- N+(123)2.")
    }

    @Test
    fun checkEmail() {
        checkType("email", TEXT_WITHOUT_BREAK)
    }

    @Test
    fun checkUri() {
        checkType("uri", TEXT_WITHOUT_BREAK)
    }

    private fun checkType(type: String, typedText: String) {
        divInput {
            activityRule.buildContainerForCase(type)
            typeTextInInput()
            assert { textTyped(typedText) }
        }
    }
}
