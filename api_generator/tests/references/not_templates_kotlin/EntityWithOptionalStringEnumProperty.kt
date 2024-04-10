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
class EntityWithOptionalStringEnumProperty(
    @JvmField final val property: Expression<Property>? = null,
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            (property?.hashCode() ?: 0)
        _hash = hash
        return hash
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "property", value = property, converter = { v: Property -> Property.toString(v) })
        json.write(key = "type", value = TYPE)
        return json
    }

    fun copy(
        property: Expression<Property>? = this.property,
    ) = EntityWithOptionalStringEnumProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_optional_string_enum_property"

        private val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithOptionalStringEnumProperty.Property.values().first()) { it is EntityWithOptionalStringEnumProperty.Property }

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithOptionalStringEnumProperty {
            val logger = env.logger
            return EntityWithOptionalStringEnumProperty(
                property = JsonParser.readOptionalExpression(json, "property", Property.Converter.FROM_STRING, logger, env, TYPE_HELPER_PROPERTY)
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalStringEnumProperty(env, json = it) }
    }


    enum class Property(private val value: String) {
        FIRST("first"),
        SECOND("second");

        companion object Converter {
            fun toString(obj: Property): String {
                return obj.value
            }

            fun fromString(string: String): Property? {
                return when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            val FROM_STRING = { string: String ->
                when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }
        }
    }
}
