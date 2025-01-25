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

class EntityWithStringArrayProperty(
    @JvmField val array: ExpressionList<String>, // at least 1 elements
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
        return builtInParserComponent.entityWithStringArrayPropertyJsonEntityParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_string_array_property"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithStringArrayProperty {
            return builtInParserComponent.entityWithStringArrayPropertyJsonEntityParser
                .value
                .deserialize(context = env, data = json)
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringArrayProperty(env, json = it) }
    }
}
