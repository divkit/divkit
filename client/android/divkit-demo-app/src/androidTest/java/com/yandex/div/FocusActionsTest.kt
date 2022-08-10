package com.yandex.div

import com.yandex.div.rule.ActivityParamsTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divFocus
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import org.junit.Rule
import org.junit.Test

class FocusActionsTest {

    @get:Rule
    val rule = uiTestRule {
        ActivityParamsTestRule(
            DivScreenshotActivity::class.java,
            DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to "ui_test_data/focus/actions.json"
        )
    }

    @Test
    fun blurActionHandledWithoutFocusActions() {
        divFocus {
            clickOnMiddleInput()
            assert { blurOnTopHandled() }
        }
    }

    @Test
    fun focusActionHandledWithoutBlurActions() {
        divFocus {
            clickOnMiddleInput()
            assert { focusOnMiddleHandled() }
        }
    }

    @Test
    fun noActionHandledOnBlurWithoutBlurActions() {
        divFocus {
            clickOnMiddleInput()
            clearMiddleInput()
            clickOnBottomInput()
            assert { focusOnMiddleNotHandled() }
        }
    }

    @Test
    fun focusActionHandledWithBlurActions() {
        divFocus {
            clickOnBottomInput()
            assert { focusOnBottomHandled() }
        }
    }

    @Test
    fun blurActionHandledWithFocusActions() {
        divFocus {
            clickOnBottomInput()
            clickOnMiddleInput()
            assert { blurOnBottomHandled() }
        }
    }
}
