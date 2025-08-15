package com.yandex.div

import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.test.rule.ActivityTestRule
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.superLineHeightTextView
import com.yandex.divkit.demo.DummyActivity
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore
class SuperLineHeightTextViewTest {

    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    private val metrics: DisplayMetrics
        get() = activityTestRule.activity.resources.displayMetrics

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun applyExtraSpacing_whenTextMatchesParent() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text.json"
            activityTestRule.buildContainer(CONTAINER_HEIGHT_DP.dpToPx(metrics))
            assert {
                hasExtraSpacing()
            }
        }
    }

    @Test
    fun applyExtraSpacing_whenParentIsBigEnough() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text_wrap_content.json"
            activityTestRule.buildContainer(CONTAINER_HEIGHT_DP.dpToPx(metrics))
            assert {
                hasExtraSpacing()
            }
        }
    }

    @Test
    fun applyExtraSpacing_whenParentHeightIsNotEnough() {
        superLineHeightTextView {
            testAsset = "div2-test/single_line_text_wrap_content.json"
            activityTestRule.buildContainer(15.dpToPx(metrics))
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
            activityTestRule.buildContainer(CONTAINER_HEIGHT_DP.dpToPx(metrics))
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
            val containerHeight = CONTAINER_HEIGHT_DP.dpToPx(metrics)
            activityTestRule.buildContainer(containerHeight)
            assert {
                hasHeight(containerHeight)
            }
        }
    }

    companion object {
        private const val CONTAINER_HEIGHT_DP = 24
    }
}
