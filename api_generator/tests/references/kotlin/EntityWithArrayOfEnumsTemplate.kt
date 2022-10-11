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
class EntityWithArrayOfEnumsTemplate : JSONSerializable, JsonTemplate<EntityWithArrayOfEnums> {
    @JvmField final val items: Field<List<EntityWithArrayOfEnums.Item>> // at least 1 elements

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithArrayOfEnumsTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        items = JsonTemplateParser.readListField(json, "items", topLevel, parent?.items, EntityWithArrayOfEnums.Item.Converter.FROM_STRING, ITEMS_TEMPLATE_VALIDATOR, logger, env)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfEnums {
        return EntityWithArrayOfEnums(
            items = items.resolveList(env = env, key = "items", data = data, ITEMS_VALIDATOR, reader = ITEMS_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "items", field = items, converter = { v: EntityWithArrayOfEnums.Item -> EntityWithArrayOfEnums.Item.toString(v) })
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_of_enums"

        private val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfEnums.Item> { it: List<*> -> it.size >= 1 }
        private val ITEMS_TEMPLATE_VALIDATOR = ListValidator<EntityWithArrayOfEnums.Item> { it: List<*> -> it.size >= 1 }

        val ITEMS_READER: Reader<List<EntityWithArrayOfEnums.Item>> = { key, json, env -> JsonParser.readList(json, key, EntityWithArrayOfEnums.Item.Converter.FROM_STRING, ITEMS_VALIDATOR, env.logger, env) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfEnumsTemplate(env, json = it) }
    }

}
