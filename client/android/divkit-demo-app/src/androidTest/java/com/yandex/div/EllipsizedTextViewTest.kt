package com.yandex.div

import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.ellipsizedTextView
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class EllipsizedTextViewTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun ellipsizeMultilineTextRegularly() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_default_ellipsis.json"
            activityRule.buildContainer()
            assert {
                hasDefaultEllipsis()
                hasTruncateAtEndEllipsize()
                hasHeight(lines = 2)
            }
        }
    }

    @Test
    fun ellipsizeMultilineTextCustomEllipsis() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_custom_ellipsis.json"
            activityRule.buildContainer()
            assert {
                hasCustomEllipsis()
                hasHeight(lines = 2)
            }
        }
    }

    @Test
    fun ellipsizeTextWithHyphensCustomEllipsis() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_custom_ellipsis_with_hyphenation.json"
            activityRule.buildContainer()
            assert {
                hasCustomEllipsis("… more")
            }
        }
    }

    @Test
    fun rebindDoesNotResetEllipsizedText() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_custom_ellipsis.json"
            activityRule.buildContainer()
            runOnMainSync {
                activityRule.setTestData()
            }
            assert {
                hasCustomEllipsis()
                hasHeight(lines = 2)
            }
        }
    }

    @Test
    fun updateEllipsisWhenWidthChanged() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_custom_ellipsis.json"
            activityRule.buildContainer()
            val before = ellipsizedText
            runOnMainSync {
                container.layoutParams = container.layoutParams.also {
                    it.width = it.width * 3 / 2
                }
            }
            Espresso.onIdle()
            val after = ellipsizedText
            assert {
                checkEllipsizedTextChanged(before!!, after!!)
            }
        }
    }

    @Test
    fun changeMaxLines() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_custom_ellipsis.json"
            activityRule.buildContainer()
            val before = ellipsizedText
            testAsset = "div2-test/ellipsized_text_view_long_text_custom_ellipsis_3_max_lines.json"
            runOnMainSync {
                activityRule.setTestData()
            }
            val after = ellipsizedText
            val displayText = displayText
            assert {
                checkEllipsizedTextChanged(before!!, after!!)
                checkEllipsizedTextIsDisplayed(displayText!!, after)
                hasHeight(lines = 3)
            }
        }
    }

    @Test
    fun specialSymbols() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_special_symbols.json"
            activityRule.buildContainer(height = ViewGroup.LayoutParams.MATCH_PARENT)
            assert {
                hasCustomEllipsis()
                noCutOff()
            }
        }
    }

    @Test
    fun singleLine() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_custom_ellipsis_single_line.json"
            activityRule.buildContainer()
            assert {
                hasCustomEllipsis()
                hasHeight(lines = 1)
            }
        }
    }

    @Test
    fun noEllipsize_whenMaxLinesNotSet() {
        ellipsizedTextView {
            testAsset = "div2-test/ellipsized_text_view_long_text_no_max_lines.json"
            activityRule.buildContainer(height = ViewGroup.LayoutParams.MATCH_PARENT)
            assert {
                hasNullEllipsize()
            }
        }
    }
}
