package com.yandex.div.steps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.yandex.div.core.widget.slider.SliderView
import org.hamcrest.Matchers.allOf

internal fun sliderPreferences(f: SliderPreferencesSteps.() -> Unit) = f(SliderPreferencesSteps())

class SliderPreferencesSteps {
    val defaultSlider get() = onView(allOf(withContentDescription("Дефолтный слайдер"),
        isAssignableFrom(SliderView::class.java)))

    val max10Slider get() = onView(allOf(withContentDescription("Мин - 0, макс - 10"),
        isAssignableFrom(SliderView::class.java)))

    val max3Slider get() = onView(allOf(withContentDescription("Мин - 0, макс - 3"),
        isAssignableFrom(SliderView::class.java)))

    val doubleDefaultSlider get() = onView(allOf(withContentDescription("Двойной без делений"),
        isAssignableFrom(SliderView::class.java)))

    val doubleWithDivisionsSlider get() = onView(allOf(withContentDescription("Двойной с делениями"),
        isAssignableFrom(SliderView::class.java)))
}
