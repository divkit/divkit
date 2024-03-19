package com.yandex.div

import com.yandex.div.rule.ActivityParamsTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divFocusTypedActions
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import org.junit.Rule
import org.junit.Test

class TypedFocusActionsTest {

    @get:Rule
    val rule = uiTestRule {
        ActivityParamsTestRule(
            DivScreenshotActivity::class.java,
            DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to "ui_test_data/focus/focus-element-and-clear-actions.json"
        )
    }

    @Test
    fun focusViewFocusInput() {
        divFocusTypedActions {
            triggerFocusInputAction()
            assert { checkInputFocused() }
        }
    }

    @Test
    fun clearFocusRemovesFocusFromInput() {
        divFocusTypedActions {
            triggerFocusInputAction()
            waitForClearFocusActionTriggered()

            assert { checkFocusCleared() }
        }
    }
}
