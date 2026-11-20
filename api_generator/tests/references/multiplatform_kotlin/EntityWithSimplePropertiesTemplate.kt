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

public class EntityWithSimplePropertiesTemplate @InternalApi constructor (
    @JvmField public val boolean: Field<Expression<Boolean>>,
    @JvmField public val booleanInt: Field<Expression<Boolean>>,
    @JvmField public val color: Field<Expression<Color>>,
    @JvmField public val double: Field<Expression<Double>>,
    @JvmField public val id: Field<Long>,
    @JvmField public val integer: Field<Expression<Long>>,
    @JvmField public val positiveInteger: Field<Expression<Long>>,
    @JvmField public val string: Field<Expression<String>>,
    @JvmField public val url: Field<Expression<Url>>,
) : Template<EntityWithSimpleProperties> {

    public companion object {
        public const val TYPE: String = "entity_with_simple_properties"
    }
}
