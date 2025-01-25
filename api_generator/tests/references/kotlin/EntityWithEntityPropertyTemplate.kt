// Generated code. Do not modify.

package com.yandex.div.reference

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.data.*
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import org.json.JSONArray
import org.json.JSONObject

class EntityWithEntityPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithEntityProperty> {
    @JvmField val entity: Field<EntityTemplate>

    constructor(
        entity: Field<EntityTemplate>,
    ) {
        this.entity = entity
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithEntityPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        entity = JsonTemplateParser.readOptionalField(json, "entity", topLevel, parent?.entity, EntityTemplate.CREATOR, logger, env)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithEntityProperty {
        return EntityWithEntityProperty(
            entity = this.entity.resolveOptionalTemplate(env = env, key = "entity", data = data, reader = ENTITY_READER) ?: ENTITY_DEFAULT_VALUE
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
