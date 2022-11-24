package com.yandex.div.steps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.yandex.div.internal.widget.slider.SliderView
import org.hamcrest.Matchers.allOf

internal fun sliderPreferences(f: SliderPreferencesSteps.() -> Unit) = f(SliderPreferencesSteps())

class SliderPreferencesSteps {
    val defaultSlider get() = onView(allOf(withContentDescription("Default slider"),
        isAssignableFrom(SliderView::class.java)))

    val max10Slider get() = onView(allOf(withContentDescription("Min - 0, max - 10"),
        isAssignableFrom(SliderView::class.java)))

    val max3Slider get() = onView(allOf(withContentDescription("Min - 0, max - 3"),
        isAssignableFrom(SliderView::class.java)))

    val doubleDefaultSlider get() = onView(allOf(withContentDescription("Double without ticks"),
        isAssignableFrom(SliderView::class.java)))

    val doubleWithDivisionsSlider get() = onView(allOf(withContentDescription("Double with ticks"),
        isAssignableFrom(SliderView::class.java)))
}
