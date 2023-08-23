// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayOfExpressions(
    @JvmField final val items: ExpressionList<String>, // at least 1 elements
) {

    companion object {
        const val TYPE = "entity_with_array_of_expressions"

        private val ITEMS_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
        private val ITEMS_ITEM_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val ITEMS_ITEM_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
    }

}
