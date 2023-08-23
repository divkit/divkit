// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithComplexPropertyWithDefaultValue(
    @JvmField final val property: Property = PROPERTY_DEFAULT_VALUE, // default value: EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))
) {

    companion object {
        const val TYPE = "entity_with_complex_property_with_default_value"

        private val PROPERTY_DEFAULT_VALUE = EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))
    }


    class Property(
        @JvmField final val value: Expression<String>,
    ) {
    }
}
