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
class EntityWithArrayWithTransformTemplate : JSONSerializable, JsonTemplate<EntityWithArrayWithTransform> {
    @JvmField final val array: Field<ExpressionList<Int>> // at least 1 elements

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithArrayWithTransformTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        array = JsonTemplateParser.readExpressionListField(json, "array", topLevel, parent?.array, STRING_TO_COLOR_INT, ARRAY_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_COLOR)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithArrayWithTransform {
        return EntityWithArrayWithTransform(
            array = array.resolveExpressionList(env = env, key = "array", data = rawData, reader = ARRAY_READER)
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
