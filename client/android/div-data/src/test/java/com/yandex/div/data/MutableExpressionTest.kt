package com.yandex.div.data

import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.TYPE_HELPER_STRING
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.invalidValue
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests for [Expression.MutableExpression]
 */
@Config(sdk = [28])
@RunWith(RobolectricTestRunner::class)
class MutableExpressionTest {
    private val failFastLogger = ParsingErrorLogger { e -> throw e }
    private val silentLogger = ParsingErrorLogger { e -> e.printStackTrace() }
    private val resolver = mock<ExpressionResolver> {
        on { get<Any, Any>(any(), any(), any(), anyOrNull(), any(), any(), any()) } doReturn null
    }


    @Test
    fun `once resolved expression cannot be resolved last valid value is used`() {
        val initialValue = "yandex.ru"
        val expression = mutableExpression(
            rawExpression = "@{domain}",
            logger = silentLogger,
            validator = { it == initialValue },
        )

        onResolveReturn(value = initialValue)
        expression.evaluate(resolver) // resolving initialValue
        throwOnExpressionResolve()
        Assert.assertEquals(initialValue, expression.evaluate(resolver))
    }

    @Test
    fun `once resolved expression becomes corrupted last valid value is used`() {
        val initialValue = "http://yandex.ru/mail"
        val expression = mutableExpression(
            rawExpression = "http://@{host}/@{path}",
            logger = silentLogger,
            validator = { it == initialValue },
        )

        onResolveReturn(value = initialValue)

        expression.evaluate(resolver) // resolving initialValue

        onResolveReturn(value = null)
        Assert.assertEquals(initialValue, expression.evaluate(resolver))
    }

    private fun onResolveReturn(value: Any?) {
        whenever(resolver.get<Any, Any>(any(), any(), any(), anyOrNull(), any(), any(), any()))
            .thenReturn(value)
    }

    private fun throwOnExpressionResolve() {
        whenever(resolver.get<Any, Any>(any(), any(), any(), anyOrNull(), any(), any(), any()))
            .thenThrow(invalidValue("/path", "value"))
    }

    private fun mutableExpression(
        rawExpression: String,
        logger: ParsingErrorLogger = failFastLogger,
        validator: (String) -> Boolean = { true },
    ) = Expression.MutableExpression<String, String>(
        expressionKey = "some_key",
        rawExpression = rawExpression,
        validator = validator,
        converter = { it },
        logger = logger,
        typeHelper = TYPE_HELPER_STRING,
    )
}
