package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.slider
import com.yandex.div.steps.sliderPreferences
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

            scrollToText(BUTTON_VALUE_NOT_SURE)

            setTo(2)
            assert {
                checkThumbValue(2)
                hasTextOnScreen(BUTTON_VALUE_BAD)
            }

            setTo(9)
            assert {
                checkThumbValue(9)
                hasTextOnScreen(BUTTON_VALUE_PERFECT)
            }
        }
    }

    @Test
    fun doubleSlider() {
        slider {
            testAsset = "regression_test_data/double_slider.json"
            activityTestRule.buildContainer()

            scrollToText(BUTTON_VALUE_NOT_SURE)

            setTo(4)
            assert {
                checkThumbValue(4)
                hasTextOnScreen(BUTTON_VALUE_NOT_SURE)
            }

            setTo(8)
            assert {
                checkThumbValue(4)
                checkSecondaryThumbValue(8)
                hasTextOnScreen(BUTTON_VALUE_NOT_SURE)
            }

            setTo(3)
            assert {
                checkThumbValue(3)
                hasTextOnScreen(BUTTON_VALUE_BAD)
            }
        }
    }

    @Test
    fun sliderPreferencesDefaultSliderInteractivity() {
        sliderPreferences {
            val slider = defaultSlider
            slider {
                testAsset = "regression_test_data/slider_presets.json"
                activityTestRule.buildContainer()

                setTo(5, slider)
                assert { checkThumbValue(5, slider) }
            }
        }
    }

    @Test
    fun sliderPreferencesMax10SliderInteractivity() {
        sliderPreferences {
            val slider = max10Slider
            slider {
                testAsset = "regression_test_data/slider_presets.json"
                activityTestRule.buildContainer()

                setTo(9, slider)
                assert { checkThumbValue(9, slider) }
            }
        }
    }

    @Test
    fun sliderPreferencesMax3SliderInteractivity() {
        sliderPreferences {
            val slider = max3Slider
            slider {
                testAsset = "regression_test_data/slider_presets.json"
                activityTestRule.buildContainer()

                setTo(2, slider)
                assert { checkThumbValue(2, slider) }
            }
        }
    }

    @Test
    fun sliderPreferencesDoubleDefaultSliderInteractivity() {
        sliderPreferences {
            val slider = doubleDefaultSlider
            slider {
                testAsset = "regression_test_data/slider_presets.json"
                activityTestRule.buildContainer()

                setTo(9, slider)
                setTo(1, slider)
                assert {
                    checkThumbValue(1, slider)
                    checkSecondaryThumbValue(9, slider)
                }
            }
        }
    }

    @Test
    fun sliderPreferencesDoubleWithDivisionsSliderInteractivity() {
        sliderPreferences {
            val slider = doubleWithDivisionsSlider
            slider {
                testAsset = "regression_test_data/slider_presets.json"
                activityTestRule.buildContainer()

                setTo(1, slider)
                setTo(9, slider)
                assert {
                    checkThumbValue(1, slider)
                    checkSecondaryThumbValue(9, slider)
                }
            }
        }
    }
}
