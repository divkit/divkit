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

class EntityWithEntityPropertyTemplate(
    @JvmField val entity: Field<EntityTemplate>,
) : JSONSerializable, JsonTemplate<EntityWithEntityProperty> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithEntityPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        entity = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithEntityProperty {
        return builtInParserComponent.entityWithEntityPropertyJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithEntityPropertyJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_entity_property"

        private val ENTITY_DEFAULT_VALUE = Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithEntityPropertyTemplate(env, json = it) }
    }
}
