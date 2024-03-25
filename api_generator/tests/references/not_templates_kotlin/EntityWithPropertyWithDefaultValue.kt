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
import org.json.JSONArray

@Mockable
class EntityWithPropertyWithDefaultValue(
    @JvmField final val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
    @JvmField final val nested: Nested? = null,
    @JvmField final val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            int.hashCode() +
            (nested?.hash() ?: 0) +
            url.hashCode()
        _hash = hash
        return hash
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "int", value = int)
        json.write(key = "nested", value = nested)
        json.write(key = "type", value = TYPE)
        json.writeExpression(key = "url", value = url, converter = URI_TO_STRING)
        return json
    }

    companion object {
        const val TYPE = "entity_with_property_with_default_value"

        private val INT_DEFAULT_VALUE = Expression.constant(0L)
        private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithPropertyWithDefaultValue {
            val logger = env.logger
            var int: Expression<Long> = INT_DEFAULT_VALUE
            var nested: Nested? = null
            var url: Expression<Uri> = URL_DEFAULT_VALUE
            for (jsonKey in json.keys()) {
                when (jsonKey) {
                    "int" -> int = JsonParser.readOptionalExpression(json, "int", NUMBER_TO_INT, INT_VALIDATOR, logger, env, INT_DEFAULT_VALUE, TYPE_HELPER_INT) ?: INT_DEFAULT_VALUE
                    "nested" -> nested = JsonParser.readOptional(json, "nested", Nested.CREATOR, logger, env)
                    "url" -> url = JsonParser.readOptionalExpression(json, "url", STRING_TO_URI, URL_VALIDATOR, logger, env, URL_DEFAULT_VALUE, TYPE_HELPER_URI) ?: URL_DEFAULT_VALUE
                }
            }
            return EntityWithPropertyWithDefaultValue(
                int = int,
                nested = nested,
                url = url
            )
        }

        private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
        private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithPropertyWithDefaultValue(env, json = it) }
    }


    @Mockable
    class Nested(
        @JvmField final val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
        @JvmField final val nonOptional: Expression<String>,
        @JvmField final val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
    ) : JSONSerializable, Hashable {

        private var _hash: Int? = null 

        override fun hash(): Int {
            _hash?.let {
                return it
            }
            val hash = 
                int.hashCode() +
                nonOptional.hashCode() +
                url.hashCode()
            _hash = hash
            return hash
        }

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.writeExpression(key = "int", value = int)
            json.writeExpression(key = "non_optional", value = nonOptional)
            json.writeExpression(key = "url", value = url, converter = URI_TO_STRING)
            return json
        }

        companion object {
            private val INT_DEFAULT_VALUE = Expression.constant(0L)
            private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

            @JvmStatic
            @JvmName("fromJson")
            operator fun invoke(env: ParsingEnvironment, json: JSONObject): Nested {
                val logger = env.logger
                var int: Expression<Long> = INT_DEFAULT_VALUE
                var nonOptional: Expression<String> = JsonParser.readExpression(json, "non_optional", logger, env, TYPE_HELPER_STRING)
                var url: Expression<Uri> = URL_DEFAULT_VALUE
                for (jsonKey in json.keys()) {
                    when (jsonKey) {
                        "int" -> int = JsonParser.readOptionalExpression(json, "int", NUMBER_TO_INT, INT_VALIDATOR, logger, env, INT_DEFAULT_VALUE, TYPE_HELPER_INT) ?: INT_DEFAULT_VALUE
                        "url" -> url = JsonParser.readOptionalExpression(json, "url", STRING_TO_URI, URL_VALIDATOR, logger, env, URL_DEFAULT_VALUE, TYPE_HELPER_URI) ?: URL_DEFAULT_VALUE
                    }
                }
                return Nested(
                    int = int,
                    nonOptional = nonOptional,
                    url = url
                )
            }

            private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
            private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Nested(env, json = it) }
        }

    }
}
