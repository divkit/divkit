// Generated code. Do not modify.

package com.yandex.div.reference

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.data.*
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import org.json.JSONArray
import org.json.JSONObject

class EntityWithPropertyWithDefaultValue(
    @JvmField val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
    @JvmField val nested: Nested? = null,
    @JvmField val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            int.hashCode() +
            (nested?.hash() ?: 0) +
            url.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithPropertyWithDefaultValue?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return int.evaluate(resolver) == other.int.evaluate(otherResolver) &&
            (nested?.equals(other.nested, resolver, otherResolver) ?: (other.nested == null)) &&
            url.evaluate(resolver) == other.url.evaluate(otherResolver)
    }

    fun copy(
        int: Expression<Long> = this.int,
        nested: Nested? = this.nested,
        url: Expression<Uri> = this.url,
    ) = EntityWithPropertyWithDefaultValue(
        int = int,
        nested = nested,
        url = url,
    )

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
            return EntityWithPropertyWithDefaultValue(
                int = JsonParser.readOptionalExpression(json, "int", NUMBER_TO_INT, INT_VALIDATOR, logger, env, INT_DEFAULT_VALUE, TYPE_HELPER_INT) ?: INT_DEFAULT_VALUE,
                nested = JsonParser.readOptional(json, "nested", Nested.CREATOR, logger, env),
                url = JsonParser.readOptionalExpression(json, "url", ANY_TO_URI, URL_VALIDATOR, logger, env, URL_DEFAULT_VALUE, TYPE_HELPER_URI) ?: URL_DEFAULT_VALUE
            )
        }

        private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
        private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithPropertyWithDefaultValue(env, json = it) }
    }

    class Nested(
        @JvmField val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
        @JvmField val nonOptional: Expression<String>,
        @JvmField val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
    ) : JSONSerializable, Hashable {

        private var _hash: Int? = null 

        override fun hash(): Int {
            _hash?.let {
                return it
            }
            val hash = 
                this::class.hashCode() +
                int.hashCode() +
                nonOptional.hashCode() +
                url.hashCode()
            _hash = hash
            return hash
        }

        fun equals(other: Nested?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
            other ?: return false
            return int.evaluate(resolver) == other.int.evaluate(otherResolver) &&
                nonOptional.evaluate(resolver) == other.nonOptional.evaluate(otherResolver) &&
                url.evaluate(resolver) == other.url.evaluate(otherResolver)
        }

        fun copy(
            int: Expression<Long> = this.int,
            nonOptional: Expression<String> = this.nonOptional,
            url: Expression<Uri> = this.url,
        ) = Nested(
            int = int,
            nonOptional = nonOptional,
            url = url,
        )

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
                return Nested(
                    int = JsonParser.readOptionalExpression(json, "int", NUMBER_TO_INT, INT_VALIDATOR, logger, env, INT_DEFAULT_VALUE, TYPE_HELPER_INT) ?: INT_DEFAULT_VALUE,
                    nonOptional = JsonParser.readExpression(json, "non_optional", logger, env, TYPE_HELPER_STRING),
                    url = JsonParser.readOptionalExpression(json, "url", ANY_TO_URI, URL_VALIDATOR, logger, env, URL_DEFAULT_VALUE, TYPE_HELPER_URI) ?: URL_DEFAULT_VALUE
                )
            }

            private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
            private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Nested(env, json = it) }
        }
    }
}
