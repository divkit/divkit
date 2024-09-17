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
class EntityWithStringArrayProperty(
    @JvmField final val array: ExpressionList<String>, // at least 1 elements
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

    fun equals(other: EntityWithStringArrayProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return array.evaluate(resolver).compareWith(other.array.evaluate(otherResolver)) { a, b -> a == b }
    }

    fun copy(
        array: ExpressionList<String> = this.array,
    ) = EntityWithStringArrayProperty(
        array = array,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpressionList(key = "array", value = array)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_string_array_property"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithStringArrayProperty {
            val logger = env.logger
            return EntityWithStringArrayProperty(
                array = JsonParser.readExpressionList(json, "array", ARRAY_VALIDATOR, logger, env, TYPE_HELPER_STRING)
            )
        }

        private val ARRAY_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringArrayProperty(env, json = it) }
    }
}
