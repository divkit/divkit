// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithSimpleProperties(
    @JvmField final val boolean: Expression<Boolean>? = null,
    @JvmField final val booleanInt: Expression<Boolean>? = null,
    @JvmField final val color: Expression<Int>? = null,
    @JvmField final val double: Expression<Double>? = null,
    @JvmField final val id: Long? = null,
    @JvmField final val integer: Expression<Long>? = null,
    @JvmField final val positiveInteger: Expression<Long>? = null, // constraint: number > 0
    @JvmField final val string: Expression<String>? = null, // at least 1 char
    @JvmField final val url: Expression<Uri>? = null,
) {

    companion object {
        const val TYPE = "entity_with_simple_properties"

        private val POSITIVE_INTEGER_TEMPLATE_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
        private val POSITIVE_INTEGER_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
        private val STRING_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val STRING_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
    }

}
