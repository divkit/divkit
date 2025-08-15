package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.defaultSlider
import com.yandex.div.steps.doubleDefaultSlider
import com.yandex.div.steps.doubleWithDivisionsSlider
import com.yandex.div.steps.max10Slider
import com.yandex.div.steps.max3Slider
import com.yandex.div.steps.slider
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class SliderTests {

    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun sliderThumbValue() {
        slider {
            testAsset = "regression_test_data/slider_nps.json"
            activityTestRule.buildContainer()
            assert { checkThumbValue(6) }
        }
    }

    @Test
    fun recommendationSlider() {
        slider {
            testAsset = "regression_test_data/slider_nps.json"
            activityTestRule.buildContainer()

            scrollToText(Recommendation.BAD.text)

            setTo(2)
            assert {
                checkThumbValue(2)
                hasTextOnScreen(Recommendation.AWFUL.text)
            }

            setTo(9)
            assert {
                checkThumbValue(9)
                hasTextOnScreen(Recommendation.EXCELLENT.text)
            }
        }
    }

    @Test
    fun doubleSlider() {
        slider {
            testAsset = "regression_test_data/double_slider.json"
            activityTestRule.buildContainer()

            scrollToText(Recommendation.BAD.text)

            setTo(4)
            assert {
                checkThumbValue(4)
                hasTextOnScreen(Recommendation.BAD.text)
            }

            setTo(8)
            assert {
                checkThumbValue(4)
                checkSecondaryThumbValue(8)
                hasTextOnScreen(Recommendation.BAD.text)
            }

            setTo(3)
            assert {
                checkThumbValue(3)
                hasTextOnScreen(Recommendation.AWFUL.text)
            }
        }
    }

    @Test
    fun sliderPreferencesDefaultSliderInteractivity() {
        defaultSlider {
            testAsset = "regression_test_data/slider_presets.json"
            activityTestRule.buildContainer()

            setTo(5)
            assert { checkThumbValue(5) }
        }
    }

    @Test
    fun sliderPreferencesMax10SliderInteractivity() {
        max10Slider {
            testAsset = "regression_test_data/slider_presets.json"
            activityTestRule.buildContainer()

            setTo(9)
            assert { checkThumbValue(9) }
        }
    }

    @Test
    fun sliderPreferencesMax3SliderInteractivity() {
        max3Slider {
            testAsset = "regression_test_data/slider_presets.json"
            activityTestRule.buildContainer()

            setTo(2)
            assert { checkThumbValue(2) }
        }
    }

    @Test
    fun sliderPreferencesDoubleDefaultSliderInteractivity() {
        doubleDefaultSlider {
            testAsset = "regression_test_data/slider_presets.json"
            activityTestRule.buildContainer()

            setTo(9)
            setTo(1)
            assert {
                checkThumbValue(1)
                checkSecondaryThumbValue(9)
            }
        }
    }

    @Test
    fun sliderPreferencesDoubleWithDivisionsSliderInteractivity() {
        doubleWithDivisionsSlider {
            testAsset = "regression_test_data/slider_presets.json"
            activityTestRule.buildContainer()

            setTo(1)
            setTo(9)
            assert {
                checkThumbValue(1)
                checkSecondaryThumbValue(9)
            }
        }
    }

    @Test
    fun sliderDoNotInterruptVerticalScroll() {
        max10Slider {
            testAsset = "regression_test_data/slider_scroll_vertical.json"
            activityTestRule.buildContainer()

            saveCoordinates()
            swipeUp()

            assert {
                parentWasScrolled()
            }
        }
    }

    @Test
    fun sliderInterruptHorizontalScroll() {
        max10Slider {
            testAsset = "regression_test_data/slider_scroll_horizontal.json"
            activityTestRule.buildContainer()

            saveCoordinates()
            dragTo(6,5)

            assert {
                parentWasNotScrolled()
            }
        }
    }

    @Test
    fun sliderChangesStatesWhenDragged() {
        max10Slider {
            testAsset = "regression_test_data/slider_nps.json"
            activityTestRule.buildContainer()

            setTo(1)
            assert {
                hasTextOnScreen(recommendationSliderExpectedText(1))
            }
            for (i in 2..10) {
                dragTo(i-1, i)
                assert {
                    hasTextOnScreen(recommendationSliderExpectedText(i))
                }
            }
        }
    }

    @Test
    fun doubleSliderChangesStatesWhenDragged() {
        doubleWithDivisionsSlider {
            testAsset = "regression_test_data/double_slider.json"
            activityTestRule.buildContainer()

            setTo(3)
            assert {
                hasTextOnScreen(recommendationSliderExpectedText(1))
            }
            for (i in 4..10) {
                dragTo(i-1, i)
                assert {
                    hasTextOnScreen(recommendationSliderExpectedText(i))
                }
            }

            setTo(3)
            for (i in 9 downTo 4) {
                dragTo(i+1, i)
                assert {
                    hasTextOnScreen(Recommendation.AWFUL.text)
                }
            }
        }
    }

    private fun recommendationSliderExpectedText(position: Int): String {
        return when (position) {
            in 0..3 -> Recommendation.AWFUL.text
            in 4..6 -> Recommendation.BAD.text
            7 -> Recommendation.OK.text
            8 -> Recommendation.GOOD.text
            9 -> Recommendation.EXCELLENT.text
            10 -> Recommendation.AWESOME.text
            else -> throw RuntimeException("Unexpected slider thumb position")
        }
    }

    private enum class Recommendation(val text: String) {
        AWFUL("This is bad"),
        BAD("Not sure"),
        OK("You can do better"),
        GOOD("Good"),
        EXCELLENT("Excellent"),
        AWESOME("Awesome")
    }
}
