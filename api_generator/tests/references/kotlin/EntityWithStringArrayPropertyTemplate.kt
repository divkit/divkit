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

class EntityWithStringArrayPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithStringArrayProperty> {
    @JvmField val array: Field<ExpressionList<String>>

    constructor(
        array: Field<ExpressionList<String>>,
    ) {
        this.array = array
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithStringArrayPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        array = JsonTemplateParser.readExpressionListField(json, "array", topLevel, parent?.array, ARRAY_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_STRING)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithStringArrayProperty {
        return EntityWithStringArrayProperty(
            array = this.array.resolveExpressionList(env = env, key = "array", data = data, reader = ARRAY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionListField(key = "array", field = array)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_string_array_property"

        private val ARRAY_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
        private val ARRAY_TEMPLATE_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }

        val ARRAY_READER: Reader<ExpressionList<String>> = { key, json, env -> JsonParser.readExpressionList(json, key, ARRAY_VALIDATOR, env.logger, env, TYPE_HELPER_STRING) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringArrayPropertyTemplate(env, json = it) }
    }
}
