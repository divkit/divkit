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

class EntityWithArrayWithTransform(
    @JvmField val array: ExpressionList<Int>, // at least 1 elements
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            array.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithArrayWithTransform?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return array.evaluate(resolver).compareWith(other.array.evaluate(otherResolver)) { a, b -> a == b }
    }

    fun copy(
        array: ExpressionList<Int> = this.array,
    ) = EntityWithArrayWithTransform(
        array = array,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionList(key = "array", value = array, converter = COLOR_INT_TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_with_transform"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArrayWithTransform {
            val logger = env.logger
            return EntityWithArrayWithTransform(
                array = JsonParser.readExpressionList(json, "array", STRING_TO_COLOR_INT, ARRAY_VALIDATOR, logger, env, TYPE_HELPER_COLOR)
            )
        }

        private val ARRAY_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayWithTransform(env, json = it) }
    }
}
