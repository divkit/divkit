package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

internal const val TEXT_WITH_DIFFERENT_SYMBOLS =
        "https://Text_with different+symbols(123)@site.ru\nsecond_line"

private const val TEXT_BEFORE_BREAK = "https://Text_with different+symbols(123)@site.ru"

class DivInputKeyboardTypeTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun checkMultiLineText() {
        checkType(
            type = "multi_line_text",
            expectedText = TEXT_WITH_DIFFERENT_SYMBOLS
        )
    }

    @Test
    fun checkSingleLineText() {
        checkType(
            type = "single_line_text",
            expectedText = TEXT_BEFORE_BREAK
        )
    }

    @Test
    fun checkPositiveNumber() {
        checkType(
            type = "number",
            expectedText = "912302.",
            typedText = "912302."
        )
    }

    @Test
    fun checkNegativeNumber() {
        checkType(
            type = "number",
            expectedText = "-912302.",
            typedText = "-912302."
        )
    }

    @Test
    fun checkPhone() {
        checkType(
            type = "phone",
            expectedText = ";//- N+(123)2."
        )
    }

    @Test
    fun checkEmail() {
        checkType(
            type = "email",
            expectedText = TEXT_BEFORE_BREAK
        )
    }

    @Test
    fun checkUri() {
        checkType(
            type = "uri",
            expectedText = TEXT_BEFORE_BREAK
        )
    }

    private fun checkType(
        type: String,
        expectedText: String,
        typedText: String = TEXT_WITH_DIFFERENT_SYMBOLS
    ) {
        divInput {
            activityRule.buildContainerForCase(type)
            typeTextInInput(typedText)
            assert { textTyped(expectedText) }
        }
    }
}
