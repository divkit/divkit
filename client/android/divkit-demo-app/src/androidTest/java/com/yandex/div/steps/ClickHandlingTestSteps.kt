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
import com.yandex.div.utils.UiTestDivActionHandler
import com.yandex.divkit.demo.div.DemoDiv2Logger
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.junit.Assert
import ru.tinkoff.allure.step as allureStep
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

internal fun testClicks(f: ClickHandlingTestSteps.() -> Unit) = f(ClickHandlingTestSteps())

@StepsDsl
internal open class ClickHandlingTestSteps : DivTestAssetSteps() {
    init {
        testAsset = "regression_test_data/button_actions.json"
    }

    private val actions = LinkedBlockingQueue<Pair<String, String>>()

    private val divActionHandler = UiTestDivActionHandler(onClick = { type, description ->
        actions.put(type to description)
    })

    fun ActivityTestRule<*>.buildContainer(): Unit = allureStep("Build container") {
        buildContainer(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            divActionHandler = divActionHandler
        )
    }

    fun click(text: String): Unit =
        allureStep("Click on view with text='$text'") {
            findView(text).perform(click())
        }

    fun clickOnImage(): Unit =
        allureStep("Click on image") {
            onView(isAssignableFrom(AppCompatImageView::class.java)).perform(click())
        }

    fun doubleClick(text: String): Unit =
        allureStep("Double click on view with text='$text'") {
            findView(text).perform(doubleClick())
        }

    fun longClick(text: String): Unit =
        allureStep("Long click on view with text='$text'") {
            findView(text).perform(longClick())
        }

    private fun findView(text: String) =
        onView(withText(text))

    fun assert(f: ClickHandlingAssertions.() -> Unit) {
        val actionsReceived = mutableMapOf<Pair<String, String>, Int>()
        while (true) {
            val action = actions.poll(3000, TimeUnit.MILLISECONDS) ?: break
            val numberOfReceived = actionsReceived.getOrPut(action) { 0 }
            actionsReceived[action] = numberOfReceived + 1
        }
        f(ClickHandlingAssertions(actionsReceived))
    }
}

@StepsDsl
internal class ClickHandlingAssertions(private val actionsReceived: Map<Pair<String, String>, Int>) {

    fun checkClicked(contentDescription: String, times: Int = 1) {
        val actualTimesClicked = actionsReceived["click" to contentDescription]
        allureStep("Expected times clicked on '$contentDescription': $times, actual: $actualTimesClicked") {
            Assert.assertEquals(times, actualTimesClicked)
        }
    }

    fun checkShown(text: String) {
        allureStep("View with text='$text' is shown") {
            onView(withText(text)).check(matches(isDisplayed()))
        }
    }

    fun checkClickLogged(cardId: String, id: String) =
        step("Check click on cardId=$cardId logged") {
            val log = "logClick(cardId = $cardId, id = $id)"
            val visibilityActions = DemoDiv2Logger.logActions
            Assert.assertTrue(visibilityActions.any { it.contains(log) })
        }

    fun checkLongClickLogged(cardId: String, id: String) =
        step("Check click on cardId=$cardId logged") {
            val log = "logLongClick(cardId = $cardId, id = $id)"
            val visibilityActions = DemoDiv2Logger.logActions
            Assert.assertTrue(visibilityActions.any { it.contains(log) })
        }

    fun checkDoubleClickLogged(cardId: String, id: String) =
        step("Check click on cardId=$cardId logged") {
            val log = "logDoubleClick(cardId = $cardId, id = $id)"
            val visibilityActions = DemoDiv2Logger.logActions
            Assert.assertTrue(visibilityActions.any { it.contains(log) })
        }
}
