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
            value.hashCode()
        _hash = hash
        return hash
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "type", value = TYPE)
        json.writeExpression(key = "value", value = value, converter = { v: Value -> Value.toString(v) })
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
                value = JsonParser.readOptionalExpression(json, "value", Value.Converter.FROM_STRING, logger, env, VALUE_DEFAULT_VALUE, TYPE_HELPER_VALUE) ?: VALUE_DEFAULT_VALUE
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

            fun fromString(string: String): Value? {
                return when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    THIRD.value -> THIRD
                    else -> null
                }
            }

            val FROM_STRING = { string: String ->
                when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    THIRD.value -> THIRD
                    else -> null
                }
            }
        }
    }
}
