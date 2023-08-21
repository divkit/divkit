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
class EntityWithOptionalProperty(
    @JvmField final val property: Expression<String>? = null, // at least 1 char
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "property", value = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_optional_property"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithOptionalProperty {
            val logger = env.logger
            return EntityWithOptionalProperty(
                property = JsonParser.readOptionalExpression(json, "property", PROPERTY_VALIDATOR, logger, env, TYPE_HELPER_STRING)
            )
        }

        private val PROPERTY_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalProperty(env, json = it) }
    }

}
