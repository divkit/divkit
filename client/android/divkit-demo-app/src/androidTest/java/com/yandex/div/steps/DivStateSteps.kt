package com.yandex.div.steps

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.view.checkIsDisplayed
import com.yandex.div.view.click
import com.yandex.divkit.demo.DummyActivity
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matchers
import java.util.regex.Matcher

fun divState(f: DivStateSteps.() -> Unit) = f(DivStateSteps())

@StepsDsl
class DivStateSteps : DivTestAssetSteps() {

    fun ActivityTestRule<DummyActivity>.buildContainerWithContainer() = buildContainer("states_in_container")

    fun ActivityTestRule<DummyActivity>.buildContainerWithGallery() = buildContainer("states_in_gallery")

    fun ActivityTestRule<DummyActivity>.buildContainerWithPager() = buildContainer("states_in_pager")

    private fun ActivityTestRule<DummyActivity>.buildContainer(filename: String) {
        testAsset = "ui_test_data/state/$filename.json"
        buildContainer(MATCH_PARENT, MATCH_PARENT)
    }

    fun clickOnState(index: Int): Unit = step("Click on State $index") {
        onView(withText(Matchers.startsWith("State $index"))).click()
    }

    fun assert(f: DivStateAssertions.() -> Unit) = f(DivStateAssertions())
}

@StepsDsl
class DivStateAssertions {

    fun checkState(index: Int, enabled: Boolean) {
        val text = "State $index is ${if (enabled) "en" else "dis"}abled"
        step("Check $text") {
            onView(withText(text)).checkIsDisplayed()
        }
    }
}
