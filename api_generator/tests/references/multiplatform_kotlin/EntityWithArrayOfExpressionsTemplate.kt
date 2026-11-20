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

public class EntityWithArrayOfExpressionsTemplate @InternalApi constructor (
    @JvmField public val items: Field<ExpressionList<String>>,
) : Template<EntityWithArrayOfExpressions> {

    public companion object {
        public const val TYPE: String = "entity_with_array_of_expressions"
    }
}
