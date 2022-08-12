package com.yandex.div

import android.view.ViewGroup
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.superLineHeightTextView
import com.yandex.div.util.dpToPx
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class SuperLineHeightTextViewTest {

    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun applyExtraSpacing_whenTextMatchesParent() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text.json"
            activityTestRule.buildContainer(dpToPx(CONTAINER_HEIGHT_DP))
            assert {
                hasExtraSpacing()
            }
        }
    }

    @Test
    fun applyExtraSpacing_whenParentIsBigEnough() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text_wrap_content.json"
            activityTestRule.buildContainer(dpToPx(CONTAINER_HEIGHT_DP))
            assert {
                hasExtraSpacing()
            }
        }
    }

    @Test
    fun applyExtraSpacing_whenParentHeightIsNotEnough() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text_wrap_content.json"
            activityTestRule.buildContainer(dpToPx(15))
            assert {
                hasExtraSpacing()
            }
        }
    }

    @Test
    fun applyExtraSpacing_whenContainerWrapsContent() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text.json"
            activityTestRule.buildContainer(ViewGroup.LayoutParams.WRAP_CONTENT)
            assert {
                hasExtraSpacing()
            }
        }
    }

    @Test
    fun applyExtraSpacing_afterResetToInitialState() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text.json"
            activityTestRule.buildContainer(dpToPx(CONTAINER_HEIGHT_DP))
            runOnMainSync { div2View.resetToInitialState() }
            assert {
                hasExtraSpacing()
            }
        }
    }

    @Test
    fun keepRequestedSize_whenTextMultiline() {
        superLineHeightTextView {
            testAsset = "div2-test/multi_line_text.json"
            val containerHeight = dpToPx(CONTAINER_HEIGHT_DP)
            activityTestRule.buildContainer(dpToPx(CONTAINER_HEIGHT_DP))
            assert {
                noExtraSpacing()
                hasHeight(containerHeight)
            }
        }
    }

    companion object {
        private const val CONTAINER_HEIGHT_DP = 24
    }
}
