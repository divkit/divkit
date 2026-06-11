package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divFocus
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class FocusActionsTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun blurActionHandledWithoutFocusActions() {
        divFocus {
            activityRule.buildContainer()
            clickOnTopInput()
            clickOnMiddleInput()
            assert { blurOnTopHandled() }
        }
    }

    @Test
    fun focusActionHandledWithoutBlurActions() {
        divFocus {
            activityRule.buildContainer()
            clickOnMiddleInput()
            assert { focusOnMiddleHandled() }
        }
    }

    @Test
    fun noActionHandledOnBlurWithoutBlurActions() {
        divFocus {
            activityRule.buildContainer()
            clickOnMiddleInput()
            clearMiddleInput()
            clickOnBottomInput()
            assert { focusOnMiddleNotHandled() }
        }
    }

    @Test
    fun focusActionHandledWithBlurActions() {
        divFocus {
            activityRule.buildContainer()
            clickOnBottomInput()
            assert { focusOnBottomHandled() }
        }
    }

    @Test
    fun blurActionHandledWithFocusActions() {
        divFocus {
            activityRule.buildContainer()
            clickOnBottomInput()
            clickOnMiddleInput()
            assert { blurOnBottomHandled() }
        }
    }
}
