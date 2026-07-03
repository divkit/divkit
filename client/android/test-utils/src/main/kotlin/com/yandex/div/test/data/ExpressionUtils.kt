package com.yandex.div.test.data

import android.net.Uri
import com.yandex.div.internal.parser.STRING_TO_URI
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.parser.TYPE_HELPER_DOUBLE
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TYPE_HELPER_URI
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivVisibility
import kotlin.test.fail

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

fun doubleExpression(expression: String): Expression<Double> {
    return Expression.MutableExpression<Double, Double>(
        expressionKey = "test",
        rawExpression = expression,
        converter = { it },
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TYPE_HELPER_DOUBLE,
    )
}

fun intExpression(
    expression: String,
    failOnError: Boolean = true
): Expression<Long> {
    return Expression.MutableExpression<Long, Long>(
        expressionKey = "test",
        rawExpression = expression,
        converter = { it },
        validator = { true },
        logger = { if (failOnError) fail(it.message) },
        typeHelper = TYPE_HELPER_INT,
    )
}

fun uriExpression(expression: String): Expression<Uri> {
    return Expression.MutableExpression(
        expressionKey = "test",
        rawExpression = expression,
        converter = STRING_TO_URI,
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TYPE_HELPER_URI,
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
