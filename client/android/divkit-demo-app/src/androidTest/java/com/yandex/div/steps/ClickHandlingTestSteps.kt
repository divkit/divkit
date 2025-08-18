package com.yandex.div.steps

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.view.ViewActions
import com.yandex.divkit.demo.div.DemoDiv2Logger
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.junit.Assert

internal fun testClicks(f: ClickHandlingTestSteps.() -> Unit) = f(ClickHandlingTestSteps())

@StepsDsl
internal open class ClickHandlingTestSteps : DivTestAssetSteps() {
    init {
        testAsset = "regression_test_data/button_actions.json"
    }

    fun ActivityTestRule<*>.buildContainer(): Unit = step("Build container") {
        buildContainer(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }

    fun click(text: String): Unit =
        step("Click on view with text='$text'") {
            findView(text).perform(click())
        }

    fun clickOnImage(): Unit =
        step("Click on image") {
            onView(isAssignableFrom(AppCompatImageView::class.java)).perform(click())
        }

    fun doubleClick(text: String): Unit =
        step("Double click on view with text='$text'") {
            findView(text).perform(doubleClick())
        }

    fun longClick(text: String): Unit =
        step("Long click on view with text='$text'") {
            findView(text).perform(longClick())
        }

    private fun findView(text: String) = onView(withText(text))

    fun assert(f: ClickHandlingAssertions.() -> Unit) = f(ClickHandlingAssertions())
}

@StepsDsl
internal class ClickHandlingAssertions {

    fun checkShown(text: String, timeout: Long? = null): Unit =
        step("View with text='$text' is shown") {
            if (timeout == null) {
                onView(withText(text))
            } else {
                ViewActions.waitForView(withText(text), timeout)
            }.check(matches(isDisplayed()))
        }

    fun checkClickLogged(cardId: String, id: String) =
        step("Check click on cardId=$cardId logged") {
            val log = "logClick(cardId = $cardId, id = $id)"
            val actions = DemoDiv2Logger.logActions
            Assert.assertTrue(actions.any { it.contains(log) })
        }

    fun checkLongClickLogged(cardId: String, id: String) =
        step("Check click on cardId=$cardId logged") {
            val log = "logLongClick(cardId = $cardId, id = $id)"
            val actions = DemoDiv2Logger.logActions
            Assert.assertTrue(actions.any { it.contains(log) })
        }

    fun checkDoubleClickLogged(cardId: String, id: String) =
        step("Check click on cardId=$cardId logged") {
            val log = "logDoubleClick(cardId = $cardId, id = $id)"
            val actions = DemoDiv2Logger.logActions
            Assert.assertTrue(actions.any { it.contains(log) })
        }
}
