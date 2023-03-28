// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*

@Mockable
class EntityWithSimpleProperties(
    @JvmField final val boolean: Expression<Boolean>? = null,
    @JvmField final val booleanInt: Expression<Boolean>? = null,
    @JvmField final val color: Expression<Int>? = null,
    @JvmField final val double: Expression<Double>? = null,
    @JvmField final val id: Long? = null,
    @JvmField final val integer: Expression<Long>? = null,
    @JvmField final val positiveInteger: Expression<Long>? = null, // constraint: number > 0
    @JvmField final val string: Expression<String>? = null, // at least 1 char
    @JvmField final val url: Expression<Uri>? = null,
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "boolean", value = boolean)
        json.writeExpression(key = "boolean_int", value = booleanInt)
        json.writeExpression(key = "color", value = color, converter = COLOR_INT_TO_STRING)
        json.writeExpression(key = "double", value = double)
        json.write(key = "id", value = id)
        json.writeExpression(key = "integer", value = integer)
        json.writeExpression(key = "positive_integer", value = positiveInteger)
        json.writeExpression(key = "string", value = string)
        json.write(key = "type", value = TYPE)
        json.writeExpression(key = "url", value = url, converter = URI_TO_STRING)
        return json
    }

    companion object {
        const val TYPE = "entity_with_simple_properties"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithSimpleProperties {
            val logger = env.logger
            return EntityWithSimpleProperties(
                boolean = JsonParser.readOptionalExpression(json, "boolean", ANY_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN),
                booleanInt = JsonParser.readOptionalExpression(json, "boolean_int", ANY_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN),
                color = JsonParser.readOptionalExpression(json, "color", STRING_TO_COLOR_INT, logger, env, TYPE_HELPER_COLOR),
                double = JsonParser.readOptionalExpression(json, "double", NUMBER_TO_DOUBLE, logger, env, TYPE_HELPER_DOUBLE),
                id = JsonParser.readOptional(json, "id", NUMBER_TO_INT, logger, env),
                integer = JsonParser.readOptionalExpression(json, "integer", NUMBER_TO_INT, logger, env, TYPE_HELPER_INT),
                positiveInteger = JsonParser.readOptionalExpression(json, "positive_integer", NUMBER_TO_INT, POSITIVE_INTEGER_VALIDATOR, logger, env, TYPE_HELPER_INT),
                string = JsonParser.readOptionalExpression(json, "string", STRING_VALIDATOR, logger, env, TYPE_HELPER_STRING),
                url = JsonParser.readOptionalExpression(json, "url", STRING_TO_URI, logger, env, TYPE_HELPER_URI)
            )
        }

        private val POSITIVE_INTEGER_TEMPLATE_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
        private val POSITIVE_INTEGER_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
        private val STRING_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val STRING_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithSimpleProperties(env, json = it) }
    }

}
