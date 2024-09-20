package com.yandex.div.steps

import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`

private val divViewMatcher = isAssignableFrom(Div2View::class.java)

internal fun divView(block: DivViewSteps.() -> Unit) = block(DivViewSteps())

@StepsDsl
class DivViewSteps: DivTestAssetSteps() {

    fun Div2Context.buildContainer(tag: String): Unit = ru.tinkoff.allure.step("Build container") {
        buildContainer(
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = ViewGroup.LayoutParams.MATCH_PARENT,
            tag = tag
        )
    }

    fun cleanUp() = runOnMainSync {
        div2View.cleanup()
    }

    fun attachToParent() = runOnMainSync {
        container.addView(div2View)
    }

    fun detachFromParent() = runOnMainSync {
        container.removeView(div2View)
    }

    fun tapOnText(text: String): Unit = step("Click on text '$text'") {
        onView(withText(text)).perform(click())
    }

    fun assert(f: DivViewAssertions.() -> Unit) = f(DivViewAssertions())
}

object DivViewInteractions {

    fun viewWithTag(tag: String): ViewInteraction {
        return onView(withTagValue(`is`(tag)))
    }

    fun viewWithTagAndText(tag: String, text: String): ViewInteraction {
        return onView(allOf(withTagValue(`is`(tag)), withText(text)))
    }
}

@StepsDsl
class DivViewAssertions {

    fun ViewInteraction.checkHasText(text: String): Unit = step("Assert view has text '$text'") {
        check(ViewAssertions.matches(withText(text)))
    }
}
