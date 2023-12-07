// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithSimpleProperties(
    @JvmField final val boolean: Expression<Boolean>? = null,
    @JvmField final val booleanInt: Expression<Boolean>? = null,
    @JvmField final val color: Expression<Int>? = null,
    @JvmField final val double: Expression<Double>? = null,
    @JvmField final val id: Long = ID_DEFAULT_VALUE, // default value: 0
    @JvmField final val integer: Expression<Long> = INTEGER_DEFAULT_VALUE, // default value: 0
    @JvmField final val positiveInteger: Expression<Long>? = null, // constraint: number > 0
    @JvmField final val string: Expression<String>? = null,
    @JvmField final val url: Expression<Uri>? = null,
) {

    companion object {
        const val TYPE = "entity_with_simple_properties"

        private val ID_DEFAULT_VALUE = 0L
        private val INTEGER_DEFAULT_VALUE = Expression.constant(0L)

        private val POSITIVE_INTEGER_TEMPLATE_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
        private val POSITIVE_INTEGER_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
    }

}
