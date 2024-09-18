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
class EntityWithStringEnumPropertyWithDefaultValue(
    @JvmField final val value: Expression<Value> = VALUE_DEFAULT_VALUE, // default value: second
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            value.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithStringEnumPropertyWithDefaultValue?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return value.evaluate(resolver) == other.value.evaluate(otherResolver)
    }

    fun copy(
        value: Expression<Value> = this.value,
    ) = EntityWithStringEnumPropertyWithDefaultValue(
        value = value,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "type", value = TYPE)
        json.writeExpression(key = "value", value = value, converter = Value.TO_STRING)
        return json
    }

    companion object {
        const val TYPE = "entity_with_string_enum_property_with_default_value"

        private val VALUE_DEFAULT_VALUE = Expression.constant(Value.SECOND)

        private val TYPE_HELPER_VALUE = TypeHelper.from(default = EntityWithStringEnumPropertyWithDefaultValue.Value.values().first()) { it is EntityWithStringEnumPropertyWithDefaultValue.Value }

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithStringEnumPropertyWithDefaultValue {
            val logger = env.logger
            return EntityWithStringEnumPropertyWithDefaultValue(
                value = JsonParser.readOptionalExpression(json, "value", Value.FROM_STRING, logger, env, VALUE_DEFAULT_VALUE, TYPE_HELPER_VALUE) ?: VALUE_DEFAULT_VALUE
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringEnumPropertyWithDefaultValue(env, json = it) }
    }

    enum class Value(private val value: String) {
        FIRST("first"),
        SECOND("second"),
        THIRD("third");

        companion object Converter {

            fun toString(obj: Value): String {
                return obj.value
            }

            fun fromString(value: String): Value? {
                return when (value) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    THIRD.value -> THIRD
                    else -> null
                }
            }

            val TO_STRING = { value: Value -> toString(value) }
            val FROM_STRING = { value: String -> fromString(value) }
        }
    }
}
