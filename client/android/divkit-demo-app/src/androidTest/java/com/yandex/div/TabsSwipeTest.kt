package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divTabs
import com.yandex.div.steps.tabSwipe
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.demo.div.DemoDiv2Logger
import org.junit.Rule
import org.junit.Test

class TabsSwipeTest {
    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun tabChangesOnSwipe() {
        tabSwipe {
            testAsset = "scenarios/tabs_max_height.json"
            activityTestRule.buildContainer()
            swipeTabLeft()
            swipeTabLeft()
            swipeTabLeft()
        }
        divTabs {
            val selectedTab = selectedTab
            assert { checkSelectedTab(3, selectedTab) }
        }

        tabSwipe { swipeTabRight() }
        divTabs {
            val selectedTab = selectedTab
            assert { checkSelectedTab(2, selectedTab) }
        }
    }

    @Test
    fun tabScrollLogging() {
        tabSwipe {
            testAsset = "scenarios/tabs_max_height.json"
            activityTestRule.buildContainer()

            swipeTabLeft()
            assert { checkTabPageChangedLogged("card", 1, DemoDiv2Logger.logActions) }

            swipeTabLeft()
            assert { checkTabPageChangedLogged("card", 2, DemoDiv2Logger.logActions) }
        }
    }

    @Test
    fun tabTitlesScrollLogging() {
        tabSwipe {
            testAsset = "scenarios/tabs_max_height.json"
            activityTestRule.buildContainer()

            swipeHeadersLeft()
            assert { checkTabTitleScrollLogged("card", DemoDiv2Logger.logActions) }
        }
    }

    @Test
    fun tabSwipeRestricted() {
        tabSwipe {
            testAsset = "scenarios/tabs_disabled_switch_tabs_by_swipe.json"
            activityTestRule.buildContainer()

            swipeTabLeft()
            swipeTabLeft()
        }
        divTabs {
            val selectedTab = selectedTab
            assert { checkSelectedTab(0, selectedTab) }
        }
        tabSwipe { clickOnHeader("Tab 2") }
        divTabs {
            val selectedTab = selectedTab
            assert { checkSelectedTab(2, selectedTab) }
        }
    }
}
