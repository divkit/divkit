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

class EntityWithArrayOfEnumsTemplate : JSONSerializable, JsonTemplate<EntityWithArrayOfEnums> {
    @JvmField val items: Field<List<EntityWithArrayOfEnums.Item>>

    constructor(
        items: Field<List<EntityWithArrayOfEnums.Item>>,
    ) {
        this.items = items
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithArrayOfEnumsTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        items = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfEnums {
        return builtInParserComponent.entityWithArrayOfEnumsJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithArrayOfEnumsJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_array_of_enums"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfEnumsTemplate(env, json = it) }
    }
}
