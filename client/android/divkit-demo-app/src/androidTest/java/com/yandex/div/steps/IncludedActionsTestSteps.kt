package com.yandex.div.steps

import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import ru.tinkoff.allure.step as allureStep

internal fun includedActions(f: IncludedActionsTestSteps.() -> Unit) = f(IncludedActionsTestSteps())

@StepsDsl
class IncludedActionsTestSteps: DivTestAssetSteps() {

    private val expandButton = onView(withText("EXPAND"))
    private val commentsButton = onView(withText("SHOW COMMENTS"))

    fun ActivityTestRule<*>.buildContainer(): Unit = allureStep("Build container") {
        testAsset = "regression_test_data/visibility_actions/article.json"
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun expand() = step("Click on button with text \"EXPAND\"") {
        expandButton.perform(click())
    }

    fun showComments() = step("Click on button with text \"SHOW COMMENTS\"") {
        commentsButton.perform(click())
    }
}
