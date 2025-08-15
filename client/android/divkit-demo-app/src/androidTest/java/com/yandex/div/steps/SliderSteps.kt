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
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.internal.widget.slider.SliderView
import com.yandex.div.view.ViewAssertions.hasChangedPosition
import com.yandex.div.view.ViewAssertions.hasNotChangedPosition
import com.yandex.div.view.checkIsDisplayed
import com.yandex.div.view.scrollTo
import com.yandex.div.view.swipeUp
import com.yandex.test.util.Report.step
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.TypeSafeMatcher

internal fun slider(f: SliderSteps.() -> Unit) = f(SliderSteps(SliderViews.commonSlider))

internal fun defaultSlider(f: SliderSteps.() -> Unit) = f(SliderSteps(SliderViews.defaultSlider))

internal fun max10Slider(f: SliderSteps.() -> Unit) = f(SliderSteps(SliderViews.max10Slider))

internal fun max3Slider(f: SliderSteps.() -> Unit) = f(SliderSteps(SliderViews.max3Slider))

internal fun doubleDefaultSlider(f: SliderSteps.() -> Unit) = f(SliderSteps(SliderViews.doubleDefaultSlider))

internal fun doubleWithDivisionsSlider(f: SliderSteps.() -> Unit) =
    f(SliderSteps(SliderViews.doubleWithDivisionsSlider))

internal open class SliderSteps(
    private val sliderView: ViewInteraction
) : DivTestAssetSteps(), CoordinateSteps by CoordinateStepsMixin() {

    fun ActivityTestRule<*>.buildContainer(): Unit = step("Build container") {
        buildContainer(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            isScrollable = true,
        )
    }

    fun setTo(sliderPosition: Int): Unit = step("Set slider value to $sliderPosition") {
        sliderView.perform(clickOnValue(sliderPosition))
    }

    fun dragTo(startPosition: Int, endPosition: Int): Unit =
        step("Drag thumb from $startPosition to $endPosition") {
            sliderView.perform(dragToValue(startPosition, endPosition))
        }

    fun scrollToText(text: String): Unit = step("Scroll to text=$text") {
        onView(withText(containsString(text))).scrollTo()
    }

    fun saveCoordinates() = step("Save current slider on-screen coordinates") {
        saveCoordinates(sliderView)
    }

    fun swipeUp(): Unit = step("Swipe slider view upwards") {
        sliderView.swipeUp()
    }

    fun assert(f: SliderAssertions.() -> Unit) = f(SliderAssertions(this, sliderView))
}

internal class SliderAssertions(
    private val coordinateSteps: CoordinateSteps,
    private val sliderView: ViewInteraction
) {

    private val lastCoordinates get() = coordinateSteps.lastSavedCoordinates
        ?: throw RuntimeException("View should have saved position coordinates before position change assertion call")

    fun checkThumbValue(value: Int): Unit = step("Assert thumb value is $value") {
        checkThumbValue(value, false)
    }

    fun checkSecondaryThumbValue(value: Int): Unit = step("Assert secondary thumb value is $value") {
        checkThumbValue(value, true)
    }

    fun hasTextOnScreen(text: String): Unit = step("Assert has text on screen $text") {
        onView(allOf(withText(containsString(text)),
            isAssignableFrom(AppCompatTextView::class.java))).checkIsDisplayed()
    }

    fun parentWasScrolled() = step("Assert view has changed position on screen") {
        hasChangedPosition(sliderView, lastCoordinates)
    }

    fun parentWasNotScrolled() = step("Assert view stayed at the same position on screen") {
        hasNotChangedPosition(sliderView, lastCoordinates)
    }

    private fun checkThumbValue(value: Int, secondary: Boolean = false) =
        sliderView.check(matches(thumbMatch(value, secondary)))
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

private fun dragToValue(
    startPosition: Int,
    endPosition: Int,
) = GeneralSwipeAction(
    Swipe.FAST,
    SliderCoordinatesProvider(startPosition),
    SliderCoordinatesProvider(endPosition),
    Press.PINPOINT
)

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
