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

class EntityWithArrayWithTransformTemplate : JSONSerializable, JsonTemplate<EntityWithArrayWithTransform> {
    @JvmField val array: Field<ExpressionList<Int>>

    constructor(
        array: Field<ExpressionList<Int>>,
    ) {
        this.array = array
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithArrayWithTransformTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        array = JsonTemplateParser.readExpressionListField(json, "array", topLevel, parent?.array, STRING_TO_COLOR_INT, ARRAY_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_COLOR)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayWithTransform {
        return EntityWithArrayWithTransform(
            array = this.array.resolveExpressionList(env = env, key = "array", data = data, reader = ARRAY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionListField(key = "array", field = array, converter = COLOR_INT_TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_with_transform"

        private val ARRAY_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }
        private val ARRAY_TEMPLATE_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }

        val ARRAY_READER: Reader<ExpressionList<Int>> = { key, json, env -> JsonParser.readExpressionList(json, key, STRING_TO_COLOR_INT, ARRAY_VALIDATOR, env.logger, env, TYPE_HELPER_COLOR) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayWithTransformTemplate(env, json = it) }
    }
}
