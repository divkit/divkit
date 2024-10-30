package com.yandex.div

import com.yandex.div.rule.ActivityParamsTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.tooltipDiv
import com.yandex.div2.DivTooltip.Position
import com.yandex.divkit.demo.screenshot.DivScreenshotActivity
import com.yandex.test.util.Report.step
import org.junit.Rule
import org.junit.Test

class DivTooltipTest {
    @Rule
    @JvmField
    val rule = uiTestRule {
        ActivityParamsTestRule(
            DivScreenshotActivity::class.java,
            DivScreenshotActivity.EXTRA_DIV_ASSET_NAME to "ui_test_data/tooltips/div_tooltips.json"
        )
    }

    @Test
    fun tooltipIsDismissedAfterClickOutside() {
        Position.values().forEach {
            checkTooltip(it)
        }
    }

    @Test
    fun tooltipIsDismissedAfterSwipeOutside() {
        tooltipDiv {
            showTooltip(Position.TOP_LEFT)
            assert {
                tooltipShown()
            }

            swipeOnTooltipWrapper()

            assert {
                noTooltipsDisplayed()
            }
        }
    }

    @Test
    fun tooltipActionsAreHandled() {
        tooltipDiv {
            showTooltip(Position.TOP)

            clickTooltip()

            assert {
                noTooltipsDisplayed()
                statusIsClicked()
                disappearActionHandled()
            }
        }
    }

    private fun checkTooltip(position: Position) = step("Checking tooltip with position: $position") {
        tooltipDiv {
            showTooltip(position)
            assert {
                tooltipShown()
            }

            clickOnTooltipWrapper()

            assert {
                noTooltipsDisplayed()
            }
        }
    }
}
