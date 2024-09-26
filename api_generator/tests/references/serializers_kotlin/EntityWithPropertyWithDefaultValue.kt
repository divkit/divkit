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
        return builtInParserComponent.entityWithPropertyWithDefaultValueJsonEntityParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_property_with_default_value"

        private val INT_DEFAULT_VALUE = Expression.constant(0L)
        private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithPropertyWithDefaultValue {
            return builtInParserComponent.entityWithPropertyWithDefaultValueJsonEntityParser
                .value
                .deserialize(context = env, data = json)
        }

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
            return builtInParserComponent.entityWithPropertyWithDefaultValueNestedJsonEntityParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            private val INT_DEFAULT_VALUE = Expression.constant(0L)
            private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

            @JvmStatic
            @JvmName("fromJson")
            operator fun invoke(env: ParsingEnvironment, json: JSONObject): Nested {
                return builtInParserComponent.entityWithPropertyWithDefaultValueNestedJsonEntityParser
                    .value
                    .deserialize(context = env, data = json)
            }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Nested(env, json = it) }
        }
    }
}
