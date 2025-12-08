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

class EntityWithArrayOfEnumsTemplate(
    @JvmField val items: Field<List<EntityWithArrayOfEnums.Item>>,
) : JSONSerializable, JsonTemplate<EntityWithArrayOfEnums> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithArrayOfEnumsTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        items = JsonTemplateParser.readListField(json, "items", topLevel, parent?.items, EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_TEMPLATE_VALIDATOR, env.logger, env)
    )

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfEnums {
        return EntityWithArrayOfEnums(
            items = this.items.resolveList(env = env, key = "items", data = data, ITEMS_VALIDATOR, reader = ITEMS_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "items", field = items, converter = EntityWithArrayOfEnums.Item.TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_of_enums"

        private val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfEnums.Item> { it: List<*> -> it.size >= 1 }
        private val ITEMS_TEMPLATE_VALIDATOR = ListValidator<EntityWithArrayOfEnums.Item> { it: List<*> -> it.size >= 1 }

        val ITEMS_READER: Reader<List<EntityWithArrayOfEnums.Item>> = { key, json, env -> JsonParser.readList(json, key, EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_VALIDATOR, env.logger, env) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfEnumsTemplate(env, json = it) }
    }
}
