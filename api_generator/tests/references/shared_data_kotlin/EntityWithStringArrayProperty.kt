// Generated code. Do not modify.

package com.yandex.div.reference

import org.json.JSONArray
import org.json.JSONObject

class EntityWithStringArrayProperty(
    @JvmField val array: ExpressionList<String>, // at least 1 elements
) : Hashable {

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

    companion object {
        const val TYPE = "entity_with_string_array_property"
    }
}
