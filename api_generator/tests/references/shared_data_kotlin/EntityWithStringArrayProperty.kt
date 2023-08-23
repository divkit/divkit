// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithStringArrayProperty(
    @JvmField final val array: ExpressionList<String>, // at least 1 elements
) {

    companion object {
        const val TYPE = "entity_with_string_array_property"

        private val ARRAY_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
    }

}
