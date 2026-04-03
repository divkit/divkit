package com.yandex.div.test.data

import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivVisibility
import org.junit.Assert.fail

fun <T : Any> constant(value: T) = Expression.ConstantExpression(value)

fun expression(expression: String): Expression<String> {
    return Expression.MutableExpression<String, String>(
        expressionKey = "test",
        rawExpression = expression,
        converter = null,
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TYPE_HELPER_STRING
    )
}

fun booleanExpression(expression: String): Expression<Boolean> {
    return Expression.MutableExpression<Boolean, Boolean>(
        expressionKey = "test",
        rawExpression = expression,
        converter = { it },
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TYPE_HELPER_BOOLEAN,
    )
}

fun intExpression(expression: String): Expression<Long> {
    return Expression.MutableExpression<Long, Long>(
        expressionKey = "test",
        rawExpression = expression,
        converter = { it },
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TYPE_HELPER_INT,
    )
}

fun visibilityExpression(expression: String): Expression<DivVisibility> {
    return Expression.MutableExpression(
        expressionKey = "test",
        rawExpression = expression,
        converter = DivVisibility::fromString,
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TypeHelper.from(default = DivVisibility.VISIBLE) { it is DivVisibility },
    )
}
