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
class EntityWithStringEnumProperty(
    @JvmField final val property: Expression<Property>,
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            property.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithStringEnumProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property.evaluate(resolver) == other.property.evaluate(otherResolver)
    }

    fun copy(
        property: Expression<Property> = this.property,
    ) = EntityWithStringEnumProperty(
        property = property,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "property", value = property, converter = Property.TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_string_enum_property"

        private val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithStringEnumProperty.Property.values().first()) { it is EntityWithStringEnumProperty.Property }

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithStringEnumProperty {
            val logger = env.logger
            return EntityWithStringEnumProperty(
                property = JsonParser.readExpression(json, "property", Property.FROM_STRING, logger, env, TYPE_HELPER_PROPERTY)
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringEnumProperty(env, json = it) }
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

            val TO_STRING = { value: Property -> toString(value) }
            val FROM_STRING = { value: String -> fromString(value) }
        }
    }
}
