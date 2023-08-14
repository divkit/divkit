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
class EntityWithArrayOfExpressionsTemplate : JSONSerializable, JsonTemplate<EntityWithArrayOfExpressions> {
    @JvmField final val items: Field<ExpressionList<String>> // at least 1 elements

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithArrayOfExpressionsTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        items = JsonTemplateParser.readExpressionListField(json, "items", topLevel, parent?.items, ITEMS_TEMPLATE_VALIDATOR, ITEMS_ITEM_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_STRING)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfExpressions {
        return EntityWithArrayOfExpressions(
            items = items.resolveExpressionList(env = env, key = "items", data = data, reader = ITEMS_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionListField(key = "items", field = items)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_of_expressions"

        private val ITEMS_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
        private val ITEMS_TEMPLATE_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
        private val ITEMS_ITEM_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val ITEMS_ITEM_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val ITEMS_READER: Reader<ExpressionList<String>> = { key, json, env -> JsonParser.readExpressionList(json, key, ITEMS_VALIDATOR, ITEMS_ITEM_VALIDATOR, env.logger, env, TYPE_HELPER_STRING) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfExpressionsTemplate(env, json = it) }
    }

}
