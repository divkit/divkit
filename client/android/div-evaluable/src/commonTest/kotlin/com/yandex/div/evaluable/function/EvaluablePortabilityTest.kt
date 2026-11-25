package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.evaluable.types.Url
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EvaluablePortabilityTest {

    @Test
    fun resolvesLeapYearMonthBoundaries() {
        assertEquals(
            "2024-02-29",
            evaluate("formatDateAsUTC(setDay(setMonth(setYear(parseUnixTime(0), 2024), 3), -1), 'yyyy-MM-dd')"),
        )
        assertEquals(
            "2024-02-29",
            evaluate("formatDateAsUTC(setDay(setMonth(setYear(parseUnixTime(0), 2024), 2), 29), 'yyyy-MM-dd')"),
        )
    }

    @Test
    fun preservesMillisWhenSettingCalendarFieldsBeforeEpoch() {
        assertEquals(
            "1969-12-01 23:59:59.999",
            evaluate("formatDateAsUTC(setDay(addMillis(parseUnixTime(0), -1), 1), 'yyyy-MM-dd HH:mm:ss.SSS')"),
        )
    }

    @Test
    fun formatsUtcWithLocaleAndRepeatedPatternFields() {
        assertEquals(
            "015/02/70 012:034:056",
            evaluate("formatDateAsUTCWithLocale(parseUnixTime(3933296), 'ddd/MM/yy HHH:mmm:sss', 'en-US')"),
        )
    }

    @Test
    fun validatesAbsoluteUrls() {
        assertEquals("https://ya.ru", Url.from("https://ya.ru").toString())
        assertFailsWith<IllegalArgumentException> { Url.from("relative/path") }
        assertFailsWith<IllegalArgumentException> { Url.from("") }
    }

    @Test
    fun rejectsMalformedDecimalPatterns() {
        for (pattern in listOf("..", ".,", "#,.", "0#0", "#.##0")) {
            val failure = assertFailsWith<EvaluableException> { evaluate("0.decimalFormat('$pattern')") }
            assertEquals("Failed to evaluate [decimalFormat('$pattern')]. Incorrect format pattern.", failure.message)
        }
    }

    @Test
    fun formatsDecimalsUsingTheRequestedLocale() {
        assertEquals("123,4", evaluate("123.4.decimalFormat('.#', 'ru-RU')"))
        assertEquals("123.4", evaluate("123.4.decimalFormat('.#', 'en-US')"))
    }

    private fun evaluate(expression: String): String {
        val context = EvaluationContext(
            variableProvider = { null },
            storedValueProvider = object : ScopedStoredValueProvider {
                override fun get(name: String, scope: String): Any? = null
            },
            functionProvider = GeneratedBuiltinFunctionProvider,
            warningSender = { _, _ -> },
        )
        return Evaluator(context).eval(Evaluable.prepare("@{$expression}"))
    }
}
