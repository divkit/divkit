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

class EntityWithStringArrayPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithStringArrayProperty> {
    @JvmField val array: Field<ExpressionList<String>>

    constructor(
        array: Field<ExpressionList<String>>,
    ) {
        this.array = array
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithStringArrayPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        array = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithStringArrayProperty {
        return builtInParserComponent.entityWithStringArrayPropertyJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithStringArrayPropertyJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_string_array_property"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringArrayPropertyTemplate(env, json = it) }
    }
}
