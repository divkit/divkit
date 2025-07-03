package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divFocusableInput
import com.yandex.div.steps.divInput
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class DivFocusableInputTest {

    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun disablingInputWorks() {
        divFocusableInput {
            activityTestRule.buildContainer()
            clickOnChangeStateButton() // disable
            clickOnInput()

            assert {
                checkInputIsNotFocused()
            }
        }
    }

    @Test
    fun inputEnablingInputAfterDisablingWorks() {
        divFocusableInput {
            activityTestRule.buildContainer()
            clickOnChangeStateButton() //disable
            clickOnChangeStateButton() //enable
            clickOnInput()

            assert {
                checkInputIsFocused()
            }
        }
    }
}
