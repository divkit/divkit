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
class EntityWithArrayWithTransform(
    @JvmField final val array: ExpressionsList<Int>, // at least 1 elements
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionsList(key = "array", value = array, converter = COLOR_INT_TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        other ?: return false
        if (other !is EntityWithArrayWithTransform) {
            return false
        }
        if (array != other.array) {
            return false
        }
        return true
    }

    companion object {
        const val TYPE = "entity_with_array_with_transform"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArrayWithTransform {
            val logger = env.logger
            return EntityWithArrayWithTransform(
                array = JsonParser.readExpressionsList(json, "array", STRING_TO_COLOR_INT, ARRAY_VALIDATOR, logger, env, TYPE_HELPER_COLOR)
            )
        }

        private val ARRAY_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayWithTransform(env, json = it) }
    }

}
