// Generated code. Do not modify.

package com.yandex.div.reference

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.type.Color
import com.yandex.div.core.type.Url
import com.yandex.div.model.entity.*
import com.yandex.div.model.expression.*
import com.yandex.div.model.entity.EntityTemplate as Template

public class EntityWithJsonProperty @InternalApi constructor (
    @JvmField public val jsonProperty: Expression<JsonObject> = JSON_PROPERTY_DEFAULT_VALUE, // default value: { "key": "value", "items": [ "value" ] }
) {

    @InternalApi
    public fun copy(
        jsonProperty: Expression<JsonObject> = this.jsonProperty,
    ): EntityWithJsonProperty = EntityWithJsonProperty(
        jsonProperty = jsonProperty,
    )

    public companion object {
        public const val TYPE: String = "entity_with_json_property"

        private val JSON_PROPERTY_DEFAULT_VALUE: Expression<JsonObject> = Expression.constant(JsonObject("""
        {
            "key": "value",
            "items": [
                "value"
            ]
        }
        """))
    }
}
