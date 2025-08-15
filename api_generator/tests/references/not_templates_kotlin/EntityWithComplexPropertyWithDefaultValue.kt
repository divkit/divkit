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

class EntityWithComplexPropertyWithDefaultValue(
    @JvmField val property: Property = PROPERTY_DEFAULT_VALUE, // default value: EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            property.hash()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithComplexPropertyWithDefaultValue?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property.equals(other.property, resolver, otherResolver)
    }

    fun copy(
        property: Property = this.property,
    ) = EntityWithComplexPropertyWithDefaultValue(
        property = property,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "property", value = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_complex_property_with_default_value"

        private val PROPERTY_DEFAULT_VALUE = EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithComplexPropertyWithDefaultValue {
            val logger = env.logger
            return EntityWithComplexPropertyWithDefaultValue(
                property = JsonParser.readOptional(json, "property", Property.CREATOR, logger, env) ?: PROPERTY_DEFAULT_VALUE
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithComplexPropertyWithDefaultValue(env, json = it) }
    }

    class Property(
        @JvmField val value: Expression<String>,
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

        fun equals(other: Property?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
            other ?: return false
            return value.evaluate(resolver) == other.value.evaluate(otherResolver)
        }

        fun copy(
            value: Expression<String> = this.value,
        ) = Property(
            value = value,
        )

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.writeExpression(key = "value", value = value)
            return json
        }

        companion object {
            @JvmStatic
            @JvmName("fromJson")
            operator fun invoke(env: ParsingEnvironment, json: JSONObject): Property {
                val logger = env.logger
                return Property(
                    value = JsonParser.readExpression(json, "value", logger, env, TYPE_HELPER_STRING)
                )
            }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Property(env, json = it) }
        }
    }
}
