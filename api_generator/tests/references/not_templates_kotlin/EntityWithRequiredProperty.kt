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

class EntityWithRequiredProperty(
    @JvmField val property: Expression<String>, // at least 1 char
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

    fun equals(other: EntityWithRequiredProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property.evaluate(resolver) == other.property.evaluate(otherResolver)
    }

    fun copy(
        property: Expression<String> = this.property,
    ) = EntityWithRequiredProperty(
        property = property,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "property", value = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_required_property"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithRequiredProperty {
            val logger = env.logger
            return EntityWithRequiredProperty(
                property = JsonParser.readExpression(json, "property", PROPERTY_VALIDATOR, logger, env, TYPE_HELPER_STRING)
            )
        }

        private val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithRequiredProperty(env, json = it) }
    }
}
