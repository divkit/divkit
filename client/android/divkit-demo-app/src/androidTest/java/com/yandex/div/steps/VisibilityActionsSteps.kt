package com.yandex.div.steps

import androidx.test.espresso.Espresso
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

    internal fun assert(f: VisibilityActionsAssertions.() -> Unit) = run {
        f(VisibilityActionsAssertions())
    }
}

private fun getMatcher(visibilityActions: List<String>, matcher: String): Boolean {
    return visibilityActions.any { Regex(matcher).containsMatchIn(it) }
}

private class VisibilityActionsIdlingResource(private val log: String) : SimpleIdlingResource() {

    override fun checkIdle(): Boolean = getMatcher(DemoDiv2Logger.logActions, log)

    override fun getName() = "VisibilityActionsIdlingResource"
}

@StepsDsl
class VisibilityActionsAssertions() {

    fun checkViewShownLogged(cardId: String, tabId: String) =
        step("Check gallery view shown logged") {
            val log = "logViewShown\\(cardId = $cardId, id = $tabId"
            VisibilityActionsIdlingResource(log).register().use { Espresso.onIdle() }
        }

    fun checkPagerChangePageLogged(cardId: String, currentPageIndex: Int) =
        step("Check gallery view shown logged") {
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
