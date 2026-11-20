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

public class EntityWithSimpleProperties @InternalApi constructor (
    @JvmField public val boolean: Expression<Boolean>? = null,
    @JvmField public val booleanInt: Expression<Boolean>? = null,
    @JvmField public val color: Expression<Color>? = null,
    @JvmField public val double: Expression<Double>? = null,
    @JvmField public val id: Long = ID_DEFAULT_VALUE, // default value: 0
    @JvmField public val integer: Expression<Long> = INTEGER_DEFAULT_VALUE, // default value: 0
    @JvmField public val positiveInteger: Expression<Long>? = null, // constraint: number > 0
    @JvmField public val string: Expression<String>? = null,
    @JvmField public val url: Expression<Url>? = null,
) {

    @InternalApi
    public fun copy(
        boolean: Expression<Boolean>? = this.boolean,
        booleanInt: Expression<Boolean>? = this.booleanInt,
        color: Expression<Color>? = this.color,
        double: Expression<Double>? = this.double,
        id: Long = this.id,
        integer: Expression<Long> = this.integer,
        positiveInteger: Expression<Long>? = this.positiveInteger,
        string: Expression<String>? = this.string,
        url: Expression<Url>? = this.url,
    ): EntityWithSimpleProperties = EntityWithSimpleProperties(
        boolean = boolean,
        booleanInt = booleanInt,
        color = color,
        double = double,
        id = id,
        integer = integer,
        positiveInteger = positiveInteger,
        string = string,
        url = url,
    )

    public companion object {
        public const val TYPE: String = "entity_with_simple_properties"

        private val ID_DEFAULT_VALUE: Long = 0L
        private val INTEGER_DEFAULT_VALUE: Expression<Long> = Expression.constant(0L)
    }
}
