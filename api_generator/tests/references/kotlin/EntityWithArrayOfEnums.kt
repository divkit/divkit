// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*

@Mockable
class EntityWithArrayOfEnums(
    @JvmField final val items: List<Item>, // at least 1 elements
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "items", value = items, converter = { v: Item -> Item.toString(v) })
        json.write(key = "type", value = TYPE)
        return json
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        other ?: return false
        if (other !is EntityWithArrayOfEnums) {
            return false
        }
        if (items != other.items) {
            return false
        }
        return true
    }

    fun equalsExceptArray(other: EntityWithArrayOfEnums): Boolean {
        if (this === other) {
            return true
        }
        return true
    }

    fun copyWithNewArray(
        items: List<EntityWithArrayOfEnums.Item>,
    ) = EntityWithArrayOfEnums(
        items,
    )

    companion object {
        const val TYPE = "entity_with_array_of_enums"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArrayOfEnums {
            val logger = env.logger
            return EntityWithArrayOfEnums(
                items = JsonParser.readList(json, "items", Item.Converter.FROM_STRING, ITEMS_VALIDATOR, logger, env)
            )
        }

        private val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfEnums.Item> { it: List<*> -> it.size >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfEnums(env, json = it) }
    }


    enum class Item(private val value: String) {
        FIRST("first"),
        SECOND("second");

        companion object Converter {
            fun toString(obj: Item): String {
                return obj.value
            }

            fun fromString(string: String): Item? {
                return when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            val FROM_STRING = { string: String ->
                when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }
        }
    }
}
