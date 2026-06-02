package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.testClicks
import com.yandex.div.view.ViewActions
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.demo.div.DemoDiv2Logger
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DivTouchInteractivityTest {
    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Before
    fun setUp() {
        DemoDiv2Logger.clearLogActions()
    }

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
    fun singleTapIsHandled() {
        testClicks {
            activityTestRule.buildContainer()
            click("Button 1 (tap)")

            assert {
                checkShown("Button 1 tapped")
            }
        }
    }

    @Test
    fun longTapIsHandled() {
        testClicks {
            activityTestRule.buildContainer()
            longClick("Button 2 (long tap)")

            assert {
                checkShown("Button 2 long tapped")
            }
        }
    }

    @Test
    fun singleTapIsHandledOnElementWithTapAndLongTapActions() {
        testClicks {
            activityTestRule.buildContainer()
            click("Button 5 (tap + long tap)")

            assert {
                checkShown("Button 5 tapped")
            }
        }
    }

    @Test
    fun longTapIsHandledOnElementWithTapAndLongTapActions() {
        testClicks {
            activityTestRule.buildContainer()
            longClick("Button 5 (tap + long tap)")

            assert {
                checkShown("Button 5 long tapped")
            }
        }
    }

    @Test
    fun singleTapIsHandledOnElementWithTapAndDoubleTapActions() {
        testClicks {
            activityTestRule.buildContainer()
            click("Button 6 (tap + double tap)")

            assert {
                checkShown("Button 6 tapped")
            }
        }
    }

    @Test
    fun doubleTapIsHandledOnElementWithTapAndDoubleTapActions() {
        testClicks {
            activityTestRule.buildContainer()
            doubleClick("Button 6 (tap + double tap)")

            assert {
                checkShown("Button 6 double tapped")
            }
        }
    }

    @Test
    fun clickOnChildIsNotPassedToParentWithAction() {
        testClicks {
            activityTestRule.buildContainer()
            click("Button 7")

            assert {
                checkShown("Button 7 inner part tapped")
            }
        }
    }

    @Test
    fun singleClickIsLogged() {
        testClicks {
            activityTestRule.buildContainer()
            click("Button 1 (tap)")

            assert { checkClickLogged("test_card", "button1_tap") }
        }
    }

    @Test
    fun longClickIsLogged() {
        testClicks {
            activityTestRule.buildContainer()
            longClick("Button 2 (long tap)")

            assert { checkLongClickLogged("test_card", "button2_longtap") }
        }
    }

    @Test
    fun doubleClickLogged() {
        testClicks {
            activityTestRule.buildContainer()
            doubleClick("Button 6 (tap + double tap)")

            assert { checkDoubleClickLogged("test_card", "button6_doubletap") }
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
