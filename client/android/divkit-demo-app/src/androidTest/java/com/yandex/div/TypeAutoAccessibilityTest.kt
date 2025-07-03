package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.accessibility
import com.yandex.divkit.demo.DummyActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TypeAutoAccessibilityTest {
    private val activityTestRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Before
    fun buildContainer() {
        accessibility {
            testAsset = "regression_test_data/accessibility/auto_types.json"
            activityTestRule.buildContainer()
        }
    }

    @Test
    fun checkTextHasCorrectClassnameAndFocusable() {
        accessibility {
            assert {
                checkTextIsFocusable()
            }
        }
    }

    @Test
    fun checkInputHasCorrectClassnameAndFocusable() {
        accessibility {
            assert {
                checkInputIsFocusable()
            }
        }
    }

    @Test
    fun checkSelectHasCorrectClassnameAndFocusable() {
        accessibility {
            assert {
                checkSelectIsFocusable()
            }
        }
    }

    @Test
    fun checkImageWithDescriptionHasCorrectClassnameAndFocusable() {
        accessibility {
            assert {
                checkImageWithDescriptionIsFocusable()
            }
        }
    }

    @Test
    fun checkImageWithoutActionOrDescriptionHasNoClassnameAndNotFocusable() {
        accessibility {
            assert {
                checkImageWithoutDescriptionIsNotFocusable()
            }
        }
    }

    @Test
    fun checkSliderHasCorrectClassnameAndNotFocusable() {
        accessibility {
            assert {
                // slider itself should be to focusable, only it's thumbs are focusable
                checkSliderIsNotFocusable()
            }
        }
    }

    @Test
    fun checkSeparatorHasNoClassnameAndNotFocusable() {
        accessibility {
            assert {
                checkSeparatorIsNotFocusable()
            }
        }
    }
}
