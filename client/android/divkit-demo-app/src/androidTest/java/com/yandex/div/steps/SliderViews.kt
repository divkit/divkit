package com.yandex.div.steps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import com.yandex.div.internal.widget.slider.SliderView
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

object SliderViews {

    val commonSlider: ViewInteraction get() = onView(isAssignableFrom(SliderView::class.java))

    val defaultSlider: ViewInteraction get() = onView(allOf(
        withTagValue(equalTo("default_slider")),
        isAssignableFrom(SliderView::class.java))
    )

    val max10Slider: ViewInteraction get() = onView(allOf(
        withTagValue(equalTo("min_0_max_10")),
        isAssignableFrom(SliderView::class.java))
    )

    val max3Slider: ViewInteraction get() = onView(allOf(
        withTagValue(equalTo("min_0_max_3")),
        isAssignableFrom(SliderView::class.java))
    )

    val doubleDefaultSlider: ViewInteraction get() = onView(allOf(
        withTagValue(equalTo("double_without_ticks")),
        isAssignableFrom(SliderView::class.java))
    )

    val doubleWithDivisionsSlider: ViewInteraction get() = onView(allOf(
        withTagValue(equalTo("double_with_ticks")),
        isAssignableFrom(SliderView::class.java))
    )
}
