package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divState
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class DivStateTest {

    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun state1IsEnabled_inContainer_onStart() {
        divState {
            activityTestRule.buildContainerWithContainer()
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state2IsDisabled_inContainer_onStart() {
        divState {
            activityTestRule.buildContainerWithContainer()
            assert { checkState(2, enabled = false) }
        }
    }

    @Test
    fun state1IsDisabled_inContainer_afterClickOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            assert { checkState(1, enabled = false) }
        }
    }

    @Test
    fun state2IsDisabled_inContainer_afterClickOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            assert { checkState(2, enabled = false) }
        }
    }

    @Test
    fun state1IsEnabled_inContainer_afterClickOnState2() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(2)
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state2IsEnabled_inContainer_afterClickOnState2() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(2)
            assert { checkState(2, enabled = true) }
        }
    }

    @Test
    fun state1IsEnabled_inContainer_after2ClicksOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            clickOnState(1)
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state1IsEnabled_inGallery_onStart() {
        divState {
            activityTestRule.buildContainerWithContainer()
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state2IsDisabled_inGallery_onStart() {
        divState {
            activityTestRule.buildContainerWithContainer()
            assert { checkState(2, enabled = false) }
        }
    }

    @Test
    fun state1IsDisabled_inGallery_afterClickOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            assert { checkState(1, enabled = false) }
        }
    }

    @Test
    fun state2IsDisabled_inGallery_afterClickOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            assert { checkState(2, enabled = false) }
        }
    }

    @Test
    fun state1IsEnabled_inGallery_afterClickOnState2() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(2)
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state2IsEnabled_inGallery_afterClickOnState2() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(2)
            assert { checkState(2, enabled = true) }
        }
    }

    @Test
    fun state1IsEnabled_inGallery_after2ClicksOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            clickOnState(1)
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state1IsEnabled_inPager_onStart() {
        divState {
            activityTestRule.buildContainerWithContainer()
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state2IsDisabled_inPager_onStart() {
        divState {
            activityTestRule.buildContainerWithContainer()
            assert { checkState(2, enabled = false) }
        }
    }

    @Test
    fun state1IsDisabled_inPager_afterClickOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            assert { checkState(1, enabled = false) }
        }
    }

    @Test
    fun state2IsDisabled_inPager_afterClickOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            assert { checkState(2, enabled = false) }
        }
    }

    @Test
    fun state1IsEnabled_inPager_afterClickOnState2() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(2)
            assert { checkState(1, enabled = true) }
        }
    }

    @Test
    fun state2IsEnabled_inPager_afterClickOnState2() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(2)
            assert { checkState(2, enabled = true) }
        }
    }

    @Test
    fun state1IsEnabled_inPager_after2ClicksOnState1() {
        divState {
            activityTestRule.buildContainerWithContainer()
            clickOnState(1)
            clickOnState(1)
            assert { checkState(1, enabled = true) }
        }
    }
}
