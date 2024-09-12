// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayWithTransform(
    @JvmField final val array: ExpressionList<Int>, // at least 1 elements
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

    fun equals(other: EntityWithArrayWithTransform?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return array.evaluate(resolver).compareWith(other.array.evaluate(otherResolver)) { a, b -> a == b }
    }

    fun copy(
        array: ExpressionList<Int> = this.array,
    ) = EntityWithArrayWithTransform(
        array = array,
    )

    companion object {
        const val TYPE = "entity_with_array_with_transform"

        private val ARRAY_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }
    }

}
