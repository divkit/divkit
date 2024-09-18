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
class EntityWithArray(
    @JvmField final val array: List<Entity>, // at least 1 elements
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            array.sumOf { it.hash() }
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithArray?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return array.compareWith(other.array) { a, b -> a.equals(b, resolver, otherResolver) }
    }

    fun copy(
        array: List<Entity> = this.array,
    ) = EntityWithArray(
        array = array,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "array", value = array)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArray {
            val logger = env.logger
            return EntityWithArray(
                array = JsonParser.readList(json, "array", Entity.CREATOR, ARRAY_VALIDATOR, logger, env)
            )
        }

        private val ARRAY_VALIDATOR = ListValidator<Entity> { it: List<*> -> it.size >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArray(env, json = it) }
    }
}
