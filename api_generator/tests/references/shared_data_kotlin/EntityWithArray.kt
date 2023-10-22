// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArray(
    @JvmField final val array: List<Entity>, // at least 1 elements
) {

    fun copyWithNewProperties(
        array: List<Entity>,
    ) = EntityWithArray(
        array,
    )

    companion object {
        const val TYPE = "entity_with_array"

        private val ARRAY_VALIDATOR = ListValidator<Entity> { it: List<*> -> it.size >= 1 }
    }

}
