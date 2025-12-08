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

class EntityWithArrayTemplate(
    @JvmField val array: Field<List<EntityTemplate>>,
) : JSONSerializable, JsonTemplate<EntityWithArray> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithArrayTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        array = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArray {
        return builtInParserComponent.entityWithArrayJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithArrayJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_array"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayTemplate(env, json = it) }
    }
}
