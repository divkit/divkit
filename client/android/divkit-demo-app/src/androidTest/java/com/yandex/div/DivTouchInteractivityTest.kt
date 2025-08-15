package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.testClicks
import com.yandex.div.view.ViewActions
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.demo.div.DemoDiv2Logger
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class DivTouchInteractivityTest {
    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun singleTapScenarioSingleTapIsHandled() {
        testClicks {
            testAsset = "regression_test_data/simple_tap.json"
            activityTestRule.buildContainer()
            click("Click on me")
            assert {
                checkShown("You clicked on me :)")
            }
        }
    }

    @Test
    fun longTapScenarioLongTapIsHandled() {
        testClicks {
            testAsset = "regression_test_data/long_tap.json"
            activityTestRule.buildContainer()
            longClick("Long tap menu")
            assert {
                checkShown("Show")
            }
        }
    }

    @Test
    @Ignore("DIVKIT-3241")
    fun singleTapIsHandled() {
        testClicks {
            activityTestRule.buildContainer()
            click("With double and long taps")

            assert {
                checkShown("Single tap")
            }
        }
    }

    @Test
    fun doubleTapIsHandled() {
        testClicks {
            activityTestRule.buildContainer()
            doubleClick("With double and long taps")

            assert {
                checkShown("Double tap")
            }
        }
    }

    @Test
    fun longTapIsHandled() {
        testClicks {
            activityTestRule.buildContainer()
            longClick("With double and long taps")

            assert {
                checkShown("Long tap")
            }
        }
    }

    @Test
    @Ignore("DIVKIT-3241")
    fun singleTapIsHandledWhenDoubleTapActionIsNotSet() {
        testClicks {
            activityTestRule.buildContainer()
            doubleClick("Without double tap")

            assert {
                checkShown("Single tap")
            }
        }
    }

    @Test
    fun clickOnChildIsNotPassedToParentWithAction() {
        testClicks {
            activityTestRule.buildContainer()
            clickOnImage()
            assert {
                checkShown("Single tap on child")
            }
        }
    }

    @Test
    fun longClickIsLogged() {
        DemoDiv2Logger.clearLogActions()
        testClicks {
            activityTestRule.buildContainer()
            longClick("With double and long taps")

            assert { checkLongClickLogged("action_button", "longtap_actions") }
        }
    }

    @Test
    @Ignore("DIVKIT-3241")
    fun singleClickIsLogged() {
        DemoDiv2Logger.clearLogActions()
        testClicks {
            activityTestRule.buildContainer()
            click("With double and long taps")

            assert { checkClickLogged("action_button", "single_tap") }
        }
    }

    @Test
    fun doubleClickLogged() {
        DemoDiv2Logger.clearLogActions()
        testClicks {
            activityTestRule.buildContainer()
            doubleClick("With double and long taps")

            assert { checkDoubleClickLogged("action_button", "doubletap_actions") }
        }
    }

    @Test
    fun disabledTapActions() {
        testClicks {
            testAsset = "regression_test_data/actions/is_enabled.json"
            activityTestRule.buildContainer()

            click("Tested text")
            doubleClick("Tested text")
            longClick("Tested text")

            assert { checkShown("Last catched action: none") }
        }
    }

    @Test
    fun enabledTapActions() {
        testClicks {
            testAsset = "regression_test_data/actions/is_enabled.json"
            activityTestRule.buildContainer()

            click("Enable actions")

            click("Tested text")
            assert { checkShown("Last catched action: click", ViewActions.WAITING_TIMEOUT) }
            doubleClick("Tested text")
            assert { checkShown("Last catched action: double_click") }
            longClick("Tested text")
            assert { checkShown("Last catched action: long_click") }
        }
    }
}
