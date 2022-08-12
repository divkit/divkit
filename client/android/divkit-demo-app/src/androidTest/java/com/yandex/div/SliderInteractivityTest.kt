package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.sliderActivity
import com.yandex.divkit.regression.SliderActivity
import org.junit.Rule
import org.junit.Test

class SliderInteractivityTest {

    private val activityTestRule = ActivityTestRule(SliderActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun thumbPositionIsChanged_whenSliderIsInteractive() {
        sliderActivity {
            setTo(4)
            assert { checkThumbValue(4) }
        }
    }

    @Test
    fun thumbPositionIsNotChanged_whenSliderIsNotInteractive() {
        sliderActivity {
            makeNonInteractive()
            setTo(4)
            assert { checkThumbValue(7) }
        }
    }

    @Test
    fun secondaryThumbPositionIsChanged_whenSliderIsInteractive() {
        sliderActivity {
            enableSecondaryThumb()
            setTo(4)
            assert { checkSecondaryThumbValue(4) }
        }
    }

    @Test
    fun secondaryThumbPositionIsNotChanged_whenSliderIsNotInteractive() {
        sliderActivity {
            enableSecondaryThumb()
            makeNonInteractive()
            setTo(4)
            assert { checkSecondaryThumbValue(3) }
        }
    }
}
