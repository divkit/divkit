package com.yandex.div.steps

import android.app.Activity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.view2.Div2View
import com.yandex.div.glide.GlideDivImageLoader
import com.yandex.div.test.crossplatform.IntegrationTestCase
import com.yandex.div.test.crossplatform.IntegrationTestLogger
import com.yandex.div.utils.contentView
import com.yandex.div.view.ViewAssertions.notExistOrDisplayed
import com.yandex.div.view.checkIsDisplayed
import com.yandex.div2.DivData
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import com.yandex.test.util.performOnMain
import com.yandex.test.util.runOnMainSync
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

internal fun integration(case: IntegrationTestCase, activity: Activity, block: IntegrationTestSteps.() -> Unit) =
    block(IntegrationTestSteps(case, activity))

@StepsDsl
class IntegrationTestSteps(private val case: IntegrationTestCase, private val activity: Activity) {

    private lateinit var divView: Div2View

    init {
        case.parseDivData()?.let {
            setupTestData(it)
        }
    }

    private fun setupTestData(data: DivData) {
        val actions = case.parseActions()

        runOnMainSync {
            val config = DivConfiguration.Builder(GlideDivImageLoader(activity))
                .divErrorsReporter(ErrorReporter(case.logger))
                .build()
            val context = Div2Context(activity, config)

            case.declareResultVariables(data.variables ?: emptyList(), context.divVariableController)

            divView = Div2View(context)
            divView.setData(data, DivDataTag("div2"))
            actions.forEach { divView.handleAction(it) }
            (activity.contentView as ViewGroup).addView(divView, MATCH_PARENT, MATCH_PARENT)
            Espresso.onIdle()
        }
    }

    fun checkResult(): Unit = step("Check expected result") {
        val resolver = performOnMain { divView.expressionResolver }
        case.checkResult(resolver, ::checkView)
    }

    private fun checkView(view: IntegrationTestCase.ExpectedResult.View) {
        val idMatcher = view.id.toIdMatcher()
        val scopeMatcher = view.scopeId?.let { isDescendantOfA(it.toIdMatcher()) }
        val textMatcher = view.text?.let { withText(it) }
        val element = when {
            scopeMatcher != null && textMatcher != null -> allOf(scopeMatcher, idMatcher, textMatcher)
            scopeMatcher != null -> allOf(scopeMatcher, idMatcher)
            textMatcher != null -> allOf(idMatcher, textMatcher)
            else -> idMatcher
        }
        onView(element).checkVisibility(view.isShown)
    }

    private fun String.toIdMatcher() = withTagValue(equalTo(this))

    private fun ViewInteraction.checkVisibility(isShown: Boolean) =
        if (isShown) checkIsDisplayed() else check(notExistOrDisplayed())

    private class ErrorReporter(private val logger: IntegrationTestLogger) : DivErrorsReporter {

        override fun onRuntimeError(divData: DivData?, divDataTag: DivDataTag, error: Throwable) =
            logger.logErrorDirectly(error)

        override fun onWarning(divData: DivData?, divDataTag: DivDataTag, warning: Throwable) =
            logger.logErrorDirectly(warning)
    }
}
