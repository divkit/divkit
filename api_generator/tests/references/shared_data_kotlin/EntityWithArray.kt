// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArray(
    @JvmField final val array: List<Entity>, // at least 1 elements
) : Hashable {

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

    companion object {
        const val TYPE = "entity_with_array"

        private val ARRAY_VALIDATOR = ListValidator<Entity> { it: List<*> -> it.size >= 1 }
    }
}
