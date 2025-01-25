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

class EntityWithOptionalStringEnumProperty(
    @JvmField val property: Expression<Property>? = null,
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            (property?.hashCode() ?: 0)
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithOptionalStringEnumProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property?.evaluate(resolver) == other.property?.evaluate(otherResolver)
    }

    fun copy(
        property: Expression<Property>? = this.property,
    ) = EntityWithOptionalStringEnumProperty(
        property = property,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "property", value = property, converter = Property.TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_optional_string_enum_property"

        private val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithOptionalStringEnumProperty.Property.values().first()) { it is EntityWithOptionalStringEnumProperty.Property }

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithOptionalStringEnumProperty {
            val logger = env.logger
            return EntityWithOptionalStringEnumProperty(
                property = JsonParser.readOptionalExpression(json, "property", Property.FROM_STRING, logger, env, TYPE_HELPER_PROPERTY)
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

            fun fromString(value: String): Property? {
                return when (value) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            @JvmField
            val TO_STRING = { value: Property -> toString(value) }

            @JvmField
            val FROM_STRING = { value: String -> fromString(value) }
        }
    }
}
