// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayWithTransform(
    @JvmField final val array: ExpressionList<Int>, // at least 1 elements
) {

    companion object {
        const val TYPE = "entity_with_array_with_transform"

        private val ARRAY_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }
    }

}
