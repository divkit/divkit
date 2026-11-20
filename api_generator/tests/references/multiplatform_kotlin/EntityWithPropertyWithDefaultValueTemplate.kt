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

public class EntityWithPropertyWithDefaultValueTemplate @InternalApi constructor (
    @JvmField public val colorAarrggbb: Field<Expression<Color>>,
    @JvmField public val colorRrggbb: Field<Expression<Color>>,
    @JvmField public val int: Field<Expression<Long>>,
    @JvmField public val nested: Field<NestedTemplate>,
    @JvmField public val url: Field<Expression<Url>>,
) : Template<EntityWithPropertyWithDefaultValue> {

    public companion object {
        public const val TYPE: String = "entity_with_property_with_default_value"
    }

    public class NestedTemplate @InternalApi constructor (
        @JvmField public val int: Field<Expression<Long>>,
        @JvmField public val nonOptional: Field<Expression<String>>,
        @JvmField public val url: Field<Expression<Url>>,
    ) : Template<EntityWithPropertyWithDefaultValue.Nested> {
    }
}
