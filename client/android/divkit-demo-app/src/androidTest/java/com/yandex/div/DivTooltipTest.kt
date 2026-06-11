package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.tooltipDiv
import com.yandex.div2.DivTooltip.Position
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.util.Report.step
import org.junit.Rule
import org.junit.Test

class DivTooltipTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @Rule
    @JvmField
    val rule = uiTestRule { activityRule }

    @Test
    fun tooltipIsDismissedAfterClickOutside() {
        tooltipDiv { activityRule.buildContainer() }
        Position.values().forEach {
            checkTooltip(it)
        }
    }

    @Test
    fun tooltipIsDismissedAfterSwipeOutside() {
        tooltipDiv {
            activityRule.buildContainer()
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
            activityRule.buildContainer()
            showTooltip(Position.TOP)

            clickTooltip()

            assert {
                noTooltipsDisplayed()
                statusIsClicked()
                disappearActionHandled()
            }
        }
    }

    @Test
    fun tooltipIsNotDismissedWhenCloseByTapOutsideIsFalse() {
        tooltipDiv {
            activityRule.buildContainer()
            showNonCloseByTapOutsideTooltip()
            
            assert {
                tooltipShown()
            }

            clickOnTooltipWrapper()
            assert {
                tooltipShown()
            }

            closeTooltip()

            assert {
                noTooltipsDisplayed()
            }
        }
    }

    @Test
    fun tooltipTapOutsideActionsAreExecuted() {
        tooltipDiv {
            activityRule.buildContainer()
            assert {
                outsideActionsCalledIsFalse()
            }

            showTooltipWithTapOutsideActions()
            clickOnTooltipWrapper()

            assert {
                outsideActionsCalledIsTrue()
                noTooltipsDisplayed()
            }
        }
    }

    @Test
    fun nonModalTooltipAllowsClickingUnderlyingElements() {
        tooltipDiv {
            activityRule.buildContainer()
            assert {
                nonModalButtonClickedIsFalse()
            }

            val buttonPosition = getUnderlyingButtonPosition()
            showNonModalTooltip()
            
            assert {
                tooltipShown()
            }

            clickAtPoint(buttonPosition)

            assert {
                nonModalButtonClickedIsTrue()
                noTooltipsDisplayed()
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
