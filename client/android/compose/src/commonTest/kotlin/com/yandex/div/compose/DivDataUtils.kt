package com.yandex.div.compose

import androidx.core.net.toUri
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import org.json.JSONObject
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

fun action(
    typed: DivActionTyped? = null,
    payload: JSONObject? = null,
    url: String? = null,
): DivAction {
    return DivAction(
        logId = constant("test"),
        payload = payload,
        typed = typed,
        url = url?.let { constant(it.toUri()) }
    )
}

fun integerVariable(name: String, value: Long): DivVariable {
    return DivVariable.Integer(IntegerVariable(name = name, value = constant(value)))
}
