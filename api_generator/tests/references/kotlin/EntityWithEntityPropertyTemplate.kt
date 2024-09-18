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
class EntityWithEntityPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithEntityProperty> {
    @JvmField final val entity: Field<EntityTemplate> // default value: Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithEntityPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        entity = JsonTemplateParser.readOptionalField(json, "entity", topLevel, parent?.entity, EntityTemplate.CREATOR, logger, env)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithEntityProperty {
        return EntityWithEntityProperty(
            entity = entity.resolveOptionalTemplate(env = env, key = "entity", data = rawData, reader = ENTITY_READER) ?: ENTITY_DEFAULT_VALUE
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "entity", field = entity)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_entity_property"

        private val ENTITY_DEFAULT_VALUE = Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))

        val ENTITY_READER: Reader<Entity> = { key, json, env -> JsonParser.readOptional(json, key, Entity.CREATOR, env.logger, env) ?: ENTITY_DEFAULT_VALUE }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithEntityPropertyTemplate(env, json = it) }
    }
}
