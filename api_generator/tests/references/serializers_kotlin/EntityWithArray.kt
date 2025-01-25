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

class EntityWithArray(
    @JvmField val array: List<Entity>, // at least 1 elements
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
        return builtInParserComponent.entityWithArrayJsonEntityParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_array"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArray {
            return builtInParserComponent.entityWithArrayJsonEntityParser
                .value
                .deserialize(context = env, data = json)
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArray(env, json = it) }
    }
}
