package com.yandex.div.steps

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.divkit.demo.div.DemoDiv2Logger
import com.yandex.test.idling.SimpleIdlingResource
import com.yandex.test.idling.register
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl

internal fun visibilityActions(f: VisibilityActionsSteps.() -> Unit) = f(VisibilityActionsSteps())

class VisibilityActionsSteps {

    fun awaitViewShown() {
        VisibilityActionsIdlingResource("logViewShown").register().use { Espresso.onIdle() }
    }

    fun awaitViewShownLogged(cardId: String, actionId: String) {
        val log = "logViewShown\\(cardId = $cardId, id = $actionId"
        VisibilityActionsIdlingResource(log, pollingIntervalMs = 200L).register().use { Espresso.onIdle() }
    }

    fun click(text: String): Unit = step("Click on view with text='$text'") {
        onView(withText(text)).perform(ViewActions.click())
    }

    internal fun assert(f: VisibilityActionsAssertions.() -> Unit) = run {
        f(VisibilityActionsAssertions())
    }
}

private fun getMatcher(visibilityActions: List<String>, matcher: String): Boolean {
    return visibilityActions.any { Regex(matcher).containsMatchIn(it) }
}

private class VisibilityActionsIdlingResource(
    private val log: String,
    pollingIntervalMs: Long = 500L,
) : SimpleIdlingResource(pollingIntervalMs) {

    override fun checkIdle(): Boolean = getMatcher(DemoDiv2Logger.logActions, log)

    override fun getName() = "VisibilityActionsIdlingResource"
}

@StepsDsl
class VisibilityActionsAssertions() {

    fun checkViewShownWithText(text: String) =
        step("Check view shown with text $text") {
            onView(withText(text)).check(matches(ViewMatchers.isDisplayed()))
        }

    fun checkViewShownLogged(cardId: String, actionId: String) =
        step("Check view shown logged") {
            val log = "logViewShown\\(cardId = $cardId, id = $actionId"
            VisibilityActionsIdlingResource(log).register().use { Espresso.onIdle() }
        }

    fun checkPagerChangePageLogged(cardId: String, currentPageIndex: Int) =
        step("Check page change logged") {
            val log = "logPagerChangePage\\(cardId = $cardId, currentPageIndex =" +
                    " $currentPageIndex\\), scrollDirection = next\\)"
            VisibilityActionsIdlingResource(log).register().use { Espresso.onIdle() }
        }

    fun checkScrollCompleted(cardId: String, firstVisibleItem: Int? = null, lastVisibleItem: Int? = null) =
        step("Check gallery scroll complete logged") {
            val log = if (firstVisibleItem == null) {
                "logGalleryCompleteScroll\\(cardId = $cardId, firstVisibleItem = [0-9]" +
                        "[0-9]?, lastVisibleItem = [0-9][0-9]?, scrollDirection = next\\)"
            } else {
                "logGalleryCompleteScroll\\(cardId = $cardId, firstVisibleItem = " +
                        "$firstVisibleItem, lastVisibleItem = $lastVisibleItem, scrollDirection = next\\)"
            }
            VisibilityActionsIdlingResource(log).register().use { Espresso.onIdle() }
        }

    fun checkGalleryScroll(cardId: String) =
        step("Check gallery scroll logged, cardId=$cardId") {
            val log = "logGalleryScroll\\(cardId = $cardId\\)"
            VisibilityActionsIdlingResource(log).register().use { Espresso.onIdle() }
        }
}
