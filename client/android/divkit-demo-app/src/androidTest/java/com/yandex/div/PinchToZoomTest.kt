package com.yandex.div

import android.view.ViewGroup
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.pinchToZoom
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class PinchToZoomTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun doNotTriggerLongTapActions_whenInPinchToZoom() {
        pinchToZoom {
            activityRule.buildContainer(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            withIntending {
                zoomAndHold()

                assert {
                    checkNoLongAction()
                }
            }
        }
    }

    @Test
    fun triggerLongAction_whenLongTapped() {
        pinchToZoom {
            activityRule.buildContainer(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            withIntending {
                longClickOnImage()

                assert {
                    checkLongAction()
                }
            }
        }
    }
}
