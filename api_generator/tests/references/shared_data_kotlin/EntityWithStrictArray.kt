// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithStrictArray(
    @JvmField final val array: List<Entity>, // at least 1 elements; all received elements must be valid
) {

    fun copyWithNewProperties(
        array: List<Entity>,
    ) = EntityWithStrictArray(
        array,
    )

    companion object {
        const val TYPE = "entity_with_strict_array"

        private val ARRAY_VALIDATOR = ListValidator<Entity> { it: List<*> -> it.size >= 1 }
    }

}
