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
import org.json.JSONArray

@Mockable
class EntityWithEntityProperty(
    @JvmField final val entity: Entity = ENTITY_DEFAULT_VALUE, // default value: Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            entity.hash()
        _hash = hash
        return hash
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "entity", value = entity)
        json.write(key = "type", value = TYPE)
        return json
    }

    fun copy(
        entity: Entity = this.entity,
    ) = EntityWithEntityProperty(
        entity = entity,
    )

    companion object {
        const val TYPE = "entity_with_entity_property"

        private val ENTITY_DEFAULT_VALUE = Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithEntityProperty {
            val logger = env.logger
            var entity: Entity = ENTITY_DEFAULT_VALUE
            for (jsonKey in json.keys()) {
                when (jsonKey) {
                    "entity" -> entity = JsonParser.readOptional(json, "entity", Entity.CREATOR, logger, env) ?: ENTITY_DEFAULT_VALUE
                }
            }
            return EntityWithEntityProperty(
                entity = entity
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithEntityProperty(env, json = it) }
    }

}
