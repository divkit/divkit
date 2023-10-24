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
class EntityWithArrayTemplate : JSONSerializable, JsonTemplate<EntityWithArray> {
    @JvmField final val array: Field<List<EntityTemplate>> // at least 1 elements

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithArrayTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        array = JsonTemplateParser.readListField(json, "array", topLevel, parent?.array, EntityTemplate.CREATOR, ARRAY_TEMPLATE_VALIDATOR, logger, env)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithArray {
        return EntityWithArray(
            array = array.resolveTemplateList(env = env, key = "array", data = rawData, ARRAY_VALIDATOR, reader = ARRAY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "array", field = array)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array"

        private val ARRAY_VALIDATOR = ListValidator<Entity> { it: List<*> -> it.size >= 1 }
        private val ARRAY_TEMPLATE_VALIDATOR = ListValidator<EntityTemplate> { it: List<*> -> it.size >= 1 }

        val ARRAY_READER: Reader<List<Entity>> = { key, json, env -> JsonParser.readList(json, key, Entity.CREATOR, ARRAY_VALIDATOR, env.logger, env) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayTemplate(env, json = it) }
    }

}
