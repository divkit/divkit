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
class EntityWithArrayOfExpressions(
    @JvmField final val items: ExpressionList<String>, // at least 1 elements
) : JSONSerializable, Hashable {

    private var _propertiesHash: Int? = null 
    private var _hash: Int? = null 

    override fun propertiesHash(): Int {
        _propertiesHash?.let {
            return it
        }
        val propertiesHash = javaClass.hashCode()
        _propertiesHash = propertiesHash
        return propertiesHash
    }

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            propertiesHash() +
            items.hashCode()
        _hash = hash
        return hash
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionList(key = "items", value = items)
        json.write(key = "type", value = TYPE)
        return json
    }

    fun copy(
        items: ExpressionList<String> = this.items,
    ) = EntityWithArrayOfExpressions(
        items = items,
    )

    companion object {
        const val TYPE = "entity_with_array_of_expressions"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArrayOfExpressions {
            val logger = env.logger
            return EntityWithArrayOfExpressions(
                items = JsonParser.readExpressionList(json, "items", ITEMS_VALIDATOR, logger, env, TYPE_HELPER_STRING)
            )
        }

        private val ITEMS_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfExpressions(env, json = it) }
    }

}
