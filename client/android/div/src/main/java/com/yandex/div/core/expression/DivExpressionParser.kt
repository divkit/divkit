package com.yandex.div.core.expression

import android.net.Uri
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.JsonExpressionParser.readExpression
import com.yandex.div.internal.parser.JsonParsers
import com.yandex.div.internal.parser.NUMBER_TO_DOUBLE
import com.yandex.div.internal.parser.NUMBER_TO_INT
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.parser.TYPE_HELPER_DICT
import com.yandex.div.internal.parser.TYPE_HELPER_DOUBLE
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_JSON_ARRAY
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.json.expressions.Expression
import com.yandex.div.serialization.ParsingContext
import com.yandex.div2.DivEvaluableType
import org.json.JSONArray
import org.json.JSONObject
import java.util.TimeZone

@Suppress("NOTHING_TO_INLINE")
internal object DivExpressionParser {

    inline fun readTypedExpression(
        context: ParsingContext,
        obj: JSONObject,
        key: String,
        expectedType: DivEvaluableType,
    ) : Expression<*> {
        return when (expectedType) {
            DivEvaluableType.STRING -> readStringExpression(context, obj, key)
            DivEvaluableType.INTEGER -> readExpression(context, obj, key, TYPE_HELPER_INT, NUMBER_TO_INT)
            DivEvaluableType.NUMBER -> readExpression(context, obj, key, TYPE_HELPER_DOUBLE, NUMBER_TO_DOUBLE)
            DivEvaluableType.BOOLEAN -> readBooleanExpression(context, obj, key)
            DivEvaluableType.URL -> readExpression(context, obj, key, TYPE_HELPER_URL, ANY_TO_URL)
            DivEvaluableType.COLOR -> readExpression(context, obj, key, TYPE_HELPER_COLOR_OBJ, ANY_TO_COLOR)
            DivEvaluableType.DICT -> readExpression(context, obj, key, TYPE_HELPER_DICT)
            DivEvaluableType.ARRAY -> readArrayExpression(context, obj, key)
            DivEvaluableType.DATETIME -> readExpression(context, obj, key, TYPE_HELPER_DATETIME)
        }
    }

    @JvmStatic
    @JvmOverloads
    inline fun readStringExpression(
        context: ParsingContext,
        obj: JSONObject,
        key: String,
        validator: ValueValidator<String> = JsonParsers.alwaysValid(),
    ): Expression<String> = readExpression(context, obj, key, TYPE_HELPER_STRING, validator)

    @JvmStatic
    @JvmOverloads
    inline fun readBooleanExpression(
        context: ParsingContext,
        obj: JSONObject,
        key: String,
        validator: ValueValidator<Boolean> = JsonParsers.alwaysValid(),
    ): Expression<Boolean> = readExpression(context, obj, key, TYPE_HELPER_BOOLEAN, ANY_TO_BOOLEAN, validator)

    @JvmStatic
    @JvmOverloads
    inline fun readArrayExpression(
        context: ParsingContext,
        obj: JSONObject,
        key: String,
        validator: ValueValidator<JSONArray> = JsonParsers.alwaysValid(),
    ): Expression<JSONArray> = readExpression(context, obj, key, TYPE_HELPER_JSON_ARRAY, validator)

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
            is String -> Color.Companion.parse(value)
            is Color -> value
            is Int -> Color(value)
            else -> throw ClassCastException("Received value of wrong type")
        }
    }
}
