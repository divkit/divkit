package com.yandex.div.steps

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.rule.ActivityTestRule
import com.yandex.div.view.swipeLeft
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import ru.tinkoff.allure.Step

internal fun gallery(f: DivGallerySteps.() -> Unit) = f(DivGallerySteps())

@StepsDsl
class DivGallerySteps: DivTestAssetSteps() {

    private val gallery = onView(isAssignableFrom(RecyclerView::class.java))

    fun ActivityTestRule<*>.buildContainer(): Unit = Step.step("Build container") {
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun swipeLeft() = step("Swipe gallery left") {
        gallery.swipeLeft()
    }
}
