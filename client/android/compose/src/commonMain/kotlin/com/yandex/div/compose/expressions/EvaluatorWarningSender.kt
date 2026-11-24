package com.yandex.div.compose.expressions

import com.yandex.div.compose.DivReporter
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.WarningSender

internal class EvaluatorWarningSender(
    private val reporter: DivReporter
): WarningSender {

    override fun send(
        expressionContext: ExpressionContext,
        message: String
    ) {
        reporter.reportWarning(message)
    }
}
