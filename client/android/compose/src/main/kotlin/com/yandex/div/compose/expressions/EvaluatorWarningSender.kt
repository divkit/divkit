package com.yandex.div.compose.expressions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.WarningSender
import javax.inject.Inject

@DivContextScope
internal class EvaluatorWarningSender @Inject constructor(
    private val reporter: DivReporter
): WarningSender {

    override fun send(
        expressionContext: ExpressionContext,
        message: String
    ) {
        reporter.reportWarning(message)
    }
}
