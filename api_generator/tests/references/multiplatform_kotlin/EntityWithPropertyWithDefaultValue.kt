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

public class EntityWithPropertyWithDefaultValue @InternalApi constructor (
    @JvmField public val colorAarrggbb: Expression<Color> = COLOR_AARRGGBB_DEFAULT_VALUE, // default value: #80ff0000
    @JvmField public val colorRrggbb: Expression<Color> = COLOR_RRGGBB_DEFAULT_VALUE, // default value: #ff0000
    @JvmField public val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
    @JvmField public val nested: Nested? = null,
    @JvmField public val url: Expression<Url> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
) {

    @InternalApi
    public fun copy(
        colorAarrggbb: Expression<Color> = this.colorAarrggbb,
        colorRrggbb: Expression<Color> = this.colorRrggbb,
        int: Expression<Long> = this.int,
        nested: Nested? = this.nested,
        url: Expression<Url> = this.url,
    ): EntityWithPropertyWithDefaultValue = EntityWithPropertyWithDefaultValue(
        colorAarrggbb = colorAarrggbb,
        colorRrggbb = colorRrggbb,
        int = int,
        nested = nested,
        url = url,
    )

    public companion object {
        public const val TYPE: String = "entity_with_property_with_default_value"

        private val COLOR_AARRGGBB_DEFAULT_VALUE: Expression<Color> = Expression.constant(Color.create(0x80FF0000.toInt()))
        private val COLOR_RRGGBB_DEFAULT_VALUE: Expression<Color> = Expression.constant(Color.create(0xFFFF0000.toInt()))
        private val INT_DEFAULT_VALUE: Expression<Long> = Expression.constant(0L)
        private val URL_DEFAULT_VALUE: Expression<Url> = Expression.constant(Url.create("https://yandex.ru"))
    }

    public class Nested @InternalApi constructor (
        @JvmField public val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
        @JvmField public val nonOptional: Expression<String>,
        @JvmField public val url: Expression<Url> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
    ) {

        @InternalApi
        public fun copy(
            int: Expression<Long> = this.int,
            nonOptional: Expression<String> = this.nonOptional,
            url: Expression<Url> = this.url,
        ): Nested = Nested(
            int = int,
            nonOptional = nonOptional,
            url = url,
        )

        public companion object {
            private val INT_DEFAULT_VALUE: Expression<Long> = Expression.constant(0L)
            private val URL_DEFAULT_VALUE: Expression<Url> = Expression.constant(Url.create("https://yandex.ru"))
        }
    }
}
