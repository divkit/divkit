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
class EntityWithRawArray(
    @JvmField final val array: Expression<JSONArray>,
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            array.hashCode()
        _hash = hash
        return hash
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "array", value = array)
        json.write(key = "type", value = TYPE)
        return json
    }

    fun copy(
        array: Expression<JSONArray> = this.array,
    ) = EntityWithRawArray(
        array = array,
    )

    companion object {
        const val TYPE = "entity_with_raw_array"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithRawArray {
            val logger = env.logger
            var array: Expression<JSONArray> = JsonParser.readExpression(json, "array", logger, env, TYPE_HELPER_JSON_ARRAY)
            return EntityWithRawArray(
                array = array
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithRawArray(env, json = it) }
    }

}
