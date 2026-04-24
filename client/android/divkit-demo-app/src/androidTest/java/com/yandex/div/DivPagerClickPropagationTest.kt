package com.yandex.div

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.DivViewInteractions.viewWithTag
import com.yandex.div.steps.divView
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class DivPagerClickPropagationTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun clickOnPagerIsPropagatedToParentWithAction() {
        divView {
            testAsset = "ui_test_data/pager/pager_click_propagation.json"
            activityRule.buildContainer(MATCH_PARENT, MATCH_PARENT)

            tapOnText("Page 1")

            assert {
                viewWithTag(tag = "result_label").checkHasText(text = "parent_clicked")
            }
        }
    }

    @Test
    fun longClickOnPagerIsPropagatedToParentWithLongTapAction() {
        divView {
            testAsset = "ui_test_data/pager/pager_long_click_propagation.json"
            activityRule.buildContainer(MATCH_PARENT, MATCH_PARENT)

            onView(withText("Page 1")).perform(longClick())

            assert {
                viewWithTag(tag = "result_label").checkHasText(text = "parent_long_clicked")
            }
        }
    }

    @Test
    fun clickOnPagerPageIsNotPropagatedToParent() {
        divView {
            testAsset = "ui_test_data/pager/pager_page_click_no_propagation.json"
            activityRule.buildContainer(MATCH_PARENT, MATCH_PARENT)

            tapOnText("Page 1")

            assert {
                viewWithTag(tag = "result_label").checkHasText(text = "page_clicked")
            }
        }
    }

    @Test
    fun clickOnPagerPageTriggersPageAction() {
        divView {
            testAsset = "ui_test_data/pager/pager_page_click.json"
            activityRule.buildContainer(MATCH_PARENT, MATCH_PARENT)

            tapOnText("Page 1")

            assert {
                viewWithTag(tag = "result_label").checkHasText(text = "page_clicked")
            }
        }
    }
}
