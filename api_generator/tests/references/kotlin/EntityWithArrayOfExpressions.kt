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
class EntityWithArrayOfExpressions(
    @JvmField final val items: ExpressionsList<String>, // at least 1 elements
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionsList(key = "items", value = items)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_of_expressions"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArrayOfExpressions {
            val logger = env.logger
            return EntityWithArrayOfExpressions(
                items = JsonParser.readExpressionsList(json, "items", ITEMS_VALIDATOR, ITEMS_ITEM_VALIDATOR, logger, env, TYPE_HELPER_STRING)
            )
        }

        private val ITEMS_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
        private val ITEMS_ITEM_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val ITEMS_ITEM_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfExpressions(env, json = it) }
    }

}
