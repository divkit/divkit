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

public class EntityWithArrayOfExpressions @InternalApi constructor (
    @JvmField public val items: ExpressionList<String>, // at least 1 elements
) {

    @InternalApi
    public fun copy(
        items: ExpressionList<String> = this.items,
    ): EntityWithArrayOfExpressions = EntityWithArrayOfExpressions(
        items = items,
    )

    public companion object {
        public const val TYPE: String = "entity_with_array_of_expressions"
    }
}
