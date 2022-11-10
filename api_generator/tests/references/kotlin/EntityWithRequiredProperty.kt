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

@Mockable
class EntityWithRequiredProperty(
    @JvmField final val property: Expression<String>, // at least 1 char
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "property", value = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        other ?: return false
        if (other !is EntityWithRequiredProperty) {
            return false
        }
        if (property != other.property) {
            return false
        }
        return true
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

        private val PROPERTY_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithRequiredProperty(env, json = it) }
    }

}
