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
class EntityWithJsonProperty(
    @JvmField final val jsonProperty: JSONObject = JSON_PROPERTY_DEFAULT_VALUE, // default value: { "key": "value", "items": [ "value" ] }
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            jsonProperty.hashCode()
        _hash = hash
        return hash
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "json_property", value = jsonProperty)
        json.write(key = "type", value = TYPE)
        return json
    }

    fun copy(
        jsonProperty: JSONObject = this.jsonProperty,
    ) = EntityWithJsonProperty(
        jsonProperty = jsonProperty,
    )

    companion object {
        const val TYPE = "entity_with_json_property"

        private val JSON_PROPERTY_DEFAULT_VALUE = JSONObject("""
        {
            "key": "value",
            "items": [
                "value"
            ]
        }
        """)

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithJsonProperty {
            val logger = env.logger
            return EntityWithJsonProperty(
                jsonProperty = JsonParser.readOptional(json, "json_property", logger, env) ?: JSON_PROPERTY_DEFAULT_VALUE
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithJsonProperty(env, json = it) }
    }

}
