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

public class EntityWithArrayOfEnums @InternalApi constructor (
    @JvmField public val items: List<Item>, // at least 1 elements
) {

    @InternalApi
    public fun copy(
        items: List<Item> = this.items,
    ): EntityWithArrayOfEnums = EntityWithArrayOfEnums(
        items = items,
    )

    public companion object {
        public const val TYPE: String = "entity_with_array_of_enums"
    }

    public enum class Item(private val value: String) {
        FIRST("first"),
        SECOND("second");

        @InternalApi
        public companion object Converter {

            public fun toString(obj: Item): String {
                return obj.value
            }

            public fun fromString(value: String): Item? {
                return when (value) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            @JvmField
            public val TO_STRING: (Item) -> String = { value: Item -> toString(value) }

            @JvmField
            public val FROM_STRING: (String) -> Item? = { value: String -> fromString(value) }
        }
    }
}
