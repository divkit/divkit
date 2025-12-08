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

class EntityWithRawArrayTemplate(
    @JvmField val array: Field<Expression<JSONArray>>,
) : JSONSerializable, JsonTemplate<EntityWithRawArray> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithRawArrayTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        array = JsonTemplateParser.readFieldWithExpression(json, "array", topLevel, parent?.array, env.logger, env, TYPE_HELPER_JSON_ARRAY)
    )

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithRawArray {
        return EntityWithRawArray(
            array = this.array.resolve(env = env, key = "array", data = data, reader = ARRAY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "array", field = array)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_raw_array"

        val ARRAY_READER: Reader<Expression<JSONArray>> = { key, json, env -> JsonParser.readExpression(json, key, env.logger, env, TYPE_HELPER_JSON_ARRAY) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithRawArrayTemplate(env, json = it) }
    }
}
