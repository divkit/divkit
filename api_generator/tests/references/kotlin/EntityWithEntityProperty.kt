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
class EntityWithEntityProperty(
    @JvmField final val entity: Entity = ENTITY_DEFAULT_VALUE, // default value: Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "entity", value = entity)
        json.write(key = "type", value = TYPE)
        return json
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        other ?: return false
        if (other !is EntityWithEntityProperty) {
            return false
        }
        if (entity != other.entity) {
            return false
        }
        return true
    }

    companion object {
        const val TYPE = "entity_with_entity_property"

        private val ENTITY_DEFAULT_VALUE = Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithEntityProperty {
            val logger = env.logger
            return EntityWithEntityProperty(
                entity = JsonParser.readOptional(json, "entity", Entity.CREATOR, logger, env) ?: ENTITY_DEFAULT_VALUE
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithEntityProperty(env, json = it) }
    }

}
