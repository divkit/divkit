package com.yandex.div.test.expression

import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.types.DateTime
import org.junit.Assert
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val DEFAULT_FORMAT_PATTERN = "yyyy-MM-dd hh:mm:ss"

fun <T> withEvaluator(
    evaluationContext: EvaluationContext,
    warningsValidator: (List<String>) -> Unit = { Assert.assertTrue(it.isEmpty()) },
    block: Evaluator.() -> T
) = run {
    val warnings = mutableListOf<String>()
    val evaluator = Evaluator(
        EvaluationContext(
            variableProvider = evaluationContext.variableProvider,
            storedValueProvider = evaluationContext.storedValueProvider,
            functionProvider = evaluationContext.functionProvider,
            warningSender = { expressionContext, message ->
                warnings.add(message)
                evaluationContext.warningSender.send(expressionContext, message)
            }
        )
    )

    block(evaluator).also {
        warningsValidator(warnings)
    }
}

fun parseAsUTC(source: String): DateTime {
    val dateFormat = SimpleDateFormat(DEFAULT_FORMAT_PATTERN, Locale.getDefault())
    val date: Date = dateFormat.parse(source)!!
    return DateTime(
        timestampMillis = date.time + Calendar.getInstance().timeZone.rawOffset,
        timezone = TimeZone.getTimeZone("UTC"),
    )
}
