package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divTabs
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class DivSelectedTabTest {

    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun keepSelectedTab_whenDataChangedWithTheSameDataTag() {
        divTabs {
            testAsset = "div2-test/tabs.json"
            activityTestRule.buildContainer()
            selectTab(tab = 3)
            runOnMainSync {
                activityTestRule.setTestData(div2View.dataTag)
            }
            val selectedTab = selectedTab
            assert {
                checkSelectedTab(3, selectedTab)
            }
        }
    }

    @Test
    fun resetSelectedTab_whenDataTagChanged() {
        divTabs {
            testAsset = "div2-test/tabs.json"
            activityTestRule.buildContainer()
            selectTab(tab = 3)
            runOnMainSync {
                activityTestRule.setTestData(DivDataTag("id"))
            }
            val selectedTab = selectedTab
            assert {
                checkSelectedTab(0, selectedTab)
            }
        }
    }

    @Test
    fun keepSelectedTab_whenItemsChanged() {
        divTabs {
            testAsset = "div2-test/tabs.json"
            activityTestRule.buildContainer()
            selectTab(tab = 3)
            testAsset = "div2-test/tabs_less_items.json"
            runOnMainSync {
                activityTestRule.setTestData(div2View.dataTag)
            }
            val selectedTab = selectedTab
            assert {
                checkSelectedTab(3, selectedTab)
            }
        }
    }

    @Test
    fun changeSelectedTab_whenDivSelectedTabChanged() {
        divTabs {
            testAsset = "div2-test/tabs.json"
            activityTestRule.buildContainer()
            selectTab(tab = 3)
            testAsset = "div2-test/tabs_selected_tab.json"
            runOnMainSync {
                activityTestRule.setTestData(div2View.dataTag)
            }
            val selectedTab = selectedTab
            assert {
                checkSelectedTab(5, selectedTab)
            }
        }
    }

    @Test
    fun keepSelectedTab_whenClickOnShowMore() {
        divTabs {
            testAsset = "div2-test/tabs_show_more.json"
            activityTestRule.buildContainer()
            selectTab(tab = 3)
            showMore()
            val selectedTab = selectedTab
            assert {
                checkSelectedTab(3, selectedTab)
            }
        }
    }
}
