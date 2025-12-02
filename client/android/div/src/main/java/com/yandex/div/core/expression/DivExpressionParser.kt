package com.yandex.div.core.expression

import android.net.Uri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.JsonParsers.alwaysValid
import com.yandex.div.internal.parser.JsonParsers.doNotConvert
import com.yandex.div.internal.parser.NUMBER_TO_DOUBLE
import com.yandex.div.internal.parser.NUMBER_TO_INT
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.parser.TYPE_HELPER_DICT
import com.yandex.div.internal.parser.TYPE_HELPER_DOUBLE
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_JSON_ARRAY
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.typeMismatch
import com.yandex.div2.DivEvaluableType
import java.util.TimeZone

internal object DivExpressionParser {

    fun readTypedExpression(
        raw: String,
        key: String,
        expectedType: DivEvaluableType,
        logger: ParsingErrorLogger,
    ): Expression<*> {
        return when (expectedType) {
            DivEvaluableType.STRING -> readExpression(raw, key, TYPE_HELPER_STRING, logger)
            DivEvaluableType.INTEGER -> readExpression(raw, key, TYPE_HELPER_INT, NUMBER_TO_INT, logger)
            DivEvaluableType.NUMBER -> readExpression(raw, key, TYPE_HELPER_DOUBLE, NUMBER_TO_DOUBLE, logger)
            DivEvaluableType.BOOLEAN -> readExpression(raw, key, TYPE_HELPER_BOOLEAN, ANY_TO_BOOLEAN, logger)
            DivEvaluableType.URL -> readExpression(raw, key, TYPE_HELPER_URL, ANY_TO_URL, logger)
            DivEvaluableType.COLOR -> readExpression(raw, key, TYPE_HELPER_COLOR_OBJ, ANY_TO_COLOR, logger)
            DivEvaluableType.DICT -> readExpression(raw, key, TYPE_HELPER_DICT, logger)
            DivEvaluableType.ARRAY -> readExpression(raw, key, TYPE_HELPER_JSON_ARRAY, logger)
            DivEvaluableType.DATETIME -> readExpression(raw, key, TYPE_HELPER_DATETIME, logger)
        }
    }

    private fun <T: Any> readExpression(
        raw: String,
        key: String,
        typeHelper: TypeHelper<T>,
        logger: ParsingErrorLogger,
    ): Expression<T> = readExpression<T, T>(raw, key, typeHelper, doNotConvert(), logger)

    private fun <T, R: Any> readExpression(
        raw: String,
        key: String,
        typeHelper: TypeHelper<R>,
        converter: Function1<T, R>,
        logger: ParsingErrorLogger,
    ): Expression<R> {
        if (Expression.mayBeExpression(raw)) {
            return Expression.MutableExpression<T, R>(key, raw, converter, alwaysValid<R>(), logger, typeHelper)
        }

        if (!typeHelper.isTypeValid(raw)) {
            throw typeMismatch(key, raw, raw)
        }
        return Expression.constant(raw as R, logger)
    }

    private val TYPE_HELPER_URL = object : TypeHelper<Url> {
        override val typeDefault = Url("")
        override fun isTypeValid(value: Any) = value is Url
    }

    private val TYPE_HELPER_COLOR_OBJ = object : TypeHelper<Color> {
        override val typeDefault = Color(0)
        override fun isTypeValid(value: Any) = value is Color
    }

    private val TYPE_HELPER_DATETIME = object : TypeHelper<DateTime> {
        override val typeDefault = DateTime(0, TimeZone.getDefault())
        override fun isTypeValid(value: Any) = value is DateTime
    }

    private val ANY_TO_URL: Converter<Any, Url> = { value ->
        when (value) {
            is String -> Url(value)
            is Url -> value
            is Uri -> Url(value.toString())
            else -> throw ClassCastException("Received value of wrong type")
        }
    }

    private val ANY_TO_COLOR: Converter<Any, Color> = { value ->
        when (value) {
            is String -> Color.parse(value)
            is Color -> value
            is Int -> Color(value)
            else -> throw ClassCastException("Received value of wrong type")
        }
    }
}
