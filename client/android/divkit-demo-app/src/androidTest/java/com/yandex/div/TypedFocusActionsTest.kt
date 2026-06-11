package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divFocusTypedActions
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class TypedFocusActionsTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun focusViewFocusInput() {
        divFocusTypedActions {
            activityRule.buildContainer()
            triggerFocusInputAction()
            assert { checkInputFocused() }
        }
    }

    @Test
    fun clearFocusRemovesFocusFromInput() {
        divFocusTypedActions {
            activityRule.buildContainer()
            triggerFocusInputAction()
            waitForClearFocusActionTriggered()

            assert { checkFocusCleared() }
        }
    }
}
