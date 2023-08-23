// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithOptionalComplexProperty(
    @JvmField final val property: Property? = null,
) {

    companion object {
        const val TYPE = "entity_with_optional_complex_property"
    }


    class Property(
        @JvmField final val value: Expression<Uri>,
    ) {
    }
}
