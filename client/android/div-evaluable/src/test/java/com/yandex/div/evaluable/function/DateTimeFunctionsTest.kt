package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.types.DateTime
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class DateTimeFunctionsTest {
    private val variableProvider = mock<VariableProvider>()
    private val evaluationContext = EvaluationContext(
        variableProvider = variableProvider,
        storedValueProvider = mock(),
        functionProvider = GeneratedBuiltinFunctionProvider,
        warningSender = { _, _ -> }
    )
    private val expressionContext = ExpressionContext(mock())

    @Test
    fun `DateTime string representation shows time in timezone DateTime`() {
        val dateGMT1 = DateTime(
            1772107200000, //2026.02.26 12:00
            TimeZone.getTimeZone("GMT+1")
        )
        val dateGMT2 = DateTime(
            1772107200000, //2026.02.26 12:00
            TimeZone.getTimeZone("GMT+2")
        )

        Assert.assertEquals("2026-02-26 13:00:00", dateGMT1.toString())
        Assert.assertEquals("2026-02-26 14:00:00", dateGMT2.toString())
    }

    @Test
    fun `FormatDateAsUTC ignores specified timezone`() {
        val result = FormatDateAsUTC.invoke(
            evaluationContext,
            expressionContext,
            args = listOf(
                DateTime(
                    1772107200000, //2026.02.26 12:00
                    TimeZone.getTimeZone("GMT+1")
                ),
                "YYYY.MM.dd HH:mm"
            )
        )

        Assert.assertEquals("2026.02.26 12:00", result)
    }


    @Test
    fun `FormatDateAsLocal ignores specified timezone`() {
        val originalTimestamp = 1772107200000 // 2026.02.26 12:00
        val formatPattern = "YYYY.MM.dd HH:mm"
        val actual = FormatDateAsLocal.invoke(
            evaluationContext,
            expressionContext,
            args = listOf(
                DateTime(
                    originalTimestamp,
                    TimeZone.getTimeZone("GMT+2")
                ),
                formatPattern
            )
        )

        val expected = DateTimeFormatter
            .ofPattern(formatPattern)
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(originalTimestamp))

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun formatDateAsLocalWithSameTimezonesLooksSame() {
        val dateTime = DateTime(
            1772107200000, //2026.02.26 12:00
            TimeZone.getDefault()
        )

        val formattedDateTime = FormatDateAsLocal.invoke(
            evaluationContext,
            expressionContext,
            args = listOf(
                dateTime,
                "YYYY-MM-dd HH:mm:ss"
            )
        )

        Assert.assertEquals(dateTime.toString(), formattedDateTime)
    }

    @Test
    fun getTimestampReturnsDateTimeInMilliseconds() {
        val timestampMillis = 1772107200000L
        val dateTime = DateTime(
            timestampMillis,
            TimeZone.getTimeZone("GMT+1")
        )

        val result = GetTimestamp.invoke(
            evaluationContext,
            expressionContext,
            args = listOf(dateTime)
        )

        Assert.assertEquals(timestampMillis, result)
    }
}
