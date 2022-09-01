package com.yandex.div.steps

import android.graphics.Rect
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.core.widget.slider.SliderView
import com.yandex.div.view.checkIsDisplayed
import com.yandex.div.view.scrollTo
import com.yandex.test.util.Report.step
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.TypeSafeMatcher
import ru.tinkoff.allure.step as allureStep

internal fun slider(f: SliderSteps.() -> Unit) = f(SliderSteps())

internal open class SliderSteps : DivTestAssetSteps() {

    val BUTTON_VALUE_PERFECT = "Excellent"
    val BUTTON_VALUE_NOT_SURE = "Not sure"
    val BUTTON_VALUE_BAD = "This is bad"

    fun ActivityTestRule<*>.buildContainer(): Unit = allureStep("Build container") {
        buildContainer(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            isScrollable = true,
        )
    }

    fun setTo(sliderPosition: Int, sliderView: ViewInteraction = commonSlider): Unit =
        step("Set slider value to $sliderPosition") {
            sliderView.perform(clickOnValue(sliderPosition))
        }

    fun scrollToText(text: String): Unit = step("Scroll to text=$text") {
        onView(withText(containsString(text))).scrollTo()
    }

    fun assert(f: SliderAssertions.() -> Unit) = f(SliderAssertions())
}

internal class SliderAssertions {

    fun checkThumbValue(value: Int, sliderView: ViewInteraction? = null): Unit =
        step("Assert thumb value is $value") {
            checkThumbValue(value, false, sliderView)
        }

    fun checkSecondaryThumbValue(value: Int, sliderView: ViewInteraction? = null): Unit =
        step("Assert secondary thumb value is $value") {
            checkThumbValue(value, true, sliderView)
        }

    fun hasTextOnScreen(text: String): Unit = step("Assert has text on screen $text") {
        onView(allOf(withText(containsString(text)),
            isAssignableFrom(AppCompatTextView::class.java))).checkIsDisplayed()
    }

    private fun checkThumbValue(
        value: Int,
        secondary: Boolean = false,
        sliderView: ViewInteraction? = null
    ) {
        val slider = sliderView ?: commonSlider
        slider.check(matches(thumbMatch(value, secondary)))
    }
}

private class SliderCoordinatesProvider(val sliderPosition: Int) : CoordinatesProvider {
    override fun calculateCoordinates(view: View?): FloatArray {
        if (view == null || view !is SliderView) return floatArrayOf()
        view.run {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            val pos = (sliderPosition - minValue) / (view.maxValue - view.minValue)
            val x = pos * (rect.right - paddingRight) +
                    (1 - pos) * (rect.left + paddingLeft +
                    (this).thumbDrawable!!.intrinsicWidth / 2)
            val y = (rect.bottom + rect.top + paddingTop - paddingBottom) * 0.5
            return floatArrayOf(x, y.toFloat())
        }
    }
}

private fun clickOnValue(sliderPosition: Int) = GeneralClickAction(
    Tap.SINGLE,
    SliderCoordinatesProvider(sliderPosition),
    Press.FINGER,
    InputDevice.SOURCE_UNKNOWN,
    MotionEvent.BUTTON_PRIMARY
)

private val commonSlider get() = onView(isAssignableFrom(SliderView::class.java))

private fun thumbMatch(thumbValue: Int, isSecondary: Boolean) : Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("check slider position equals $thumbValue," +
                    "isSecondaryThumb=$isSecondary")
        }

        override fun matchesSafely(view: View): Boolean {
            if (view !is SliderView) return false
            return if (isSecondary) {
                view.thumbSecondaryValue?.toInt() == thumbValue
            } else {
                view.thumbValue.toInt() == thumbValue
            }
        }
    }
}
