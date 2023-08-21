package com.yandex.div

import androidx.test.espresso.Espresso.pressBack
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divSelect
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.util.assertNoPopupsAreDisplayed
import com.yandex.test.util.assertPopupDisplayed
import org.junit.Rule
import org.junit.Test


class DivSelectPopupVisibilityTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun popupShown() {
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            clickOnSelect()
            assertPopupDisplayed()
        }
    }

    @Test
    fun popupDismissedOnItemTap() {
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            clickOnSelect()
            assertPopupDisplayed()

            selectFirstOption()
            assertNoPopupsAreDisplayed()
        }
    }

    @Test
    fun popupDismissedOnPressBack() {
        // Press back should work the same as click outside of popup
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            clickOnSelect()
            assertPopupDisplayed()

            pressBack()
            assertNoPopupsAreDisplayed()
        }
    }

    @Test
    fun popupDismissedOnVisibilityInvisible() {
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            openSelectWithDelayedAction("visibility_invisible")

            assertNoPopupsAreDisplayed()
        }
    }

    @Test
    fun popupDismissedOnVisibilityGone() {
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            openSelectWithDelayedAction("visibility_gone")

            assertNoPopupsAreDisplayed()
        }
    }

    @Test
    fun popupAlignedWithAnchor() {
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            val select = stealSelect()

            clickOnSelect()
            assertPopupDisplayed()

            assert { popupAlignedWithAnchor(select) }
        }
    }

    @Test
    fun popupAlignedWithAnchorAfterWidthChange() {
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            val select = stealSelect()

            openSelectWithDelayedAction("width_change")

            assert { popupAlignedWithAnchor(select) }
        }
    }

    @Test
    fun popupAlignedWithAnchorAfterPositionChange() {
        divSelect {
            activityRule.buildContainer()
            assertNoPopupsAreDisplayed()

            val select = stealSelect()

            openSelectWithDelayedAction("position_change")

            assert { popupAlignedWithAnchor(select) }
        }
    }
}
