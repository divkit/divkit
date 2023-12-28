package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divCustom
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class DivCustomTest {

    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun stateChanging_whenDivInsideCustom() {
        divCustom {
            activityTestRule.buildContainerForCase("state_changing")
            clickOnText("Text to click")
            assert {
                textDisplayed("Clicked text")
            }
        }
    }
}
