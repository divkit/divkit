package com.yandex.div.steps

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.yandex.divkit.demo.R
import com.yandex.test.util.Report

internal fun sliderActivity(f: SliderActivitySteps.() -> Unit) = f(SliderActivitySteps())

internal class SliderActivitySteps : SliderSteps(SliderViews.commonSlider) {
    private val interactivity get() = Espresso.onView(ViewMatchers.withId(R.id.interactive_switch))
    private val secondaryThumbSwitcher get() = Espresso.onView(ViewMatchers.withId(R.id.secondary_thumb_switch))

    fun makeNonInteractive(): Unit = Report.step("Click on interactivity switcher") {
        interactivity.perform(ViewActions.click())
    }

    fun enableSecondaryThumb(): Unit = Report.step("Click on secondary thumb switcher") {
        secondaryThumbSwitcher.perform(ViewActions.click())
    }
}
