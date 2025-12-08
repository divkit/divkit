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

class EntityWithArrayOfExpressionsTemplate(
    @JvmField val items: Field<ExpressionList<String>>,
) : JSONSerializable, JsonTemplate<EntityWithArrayOfExpressions> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithArrayOfExpressionsTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        items = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfExpressions {
        return builtInParserComponent.entityWithArrayOfExpressionsJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithArrayOfExpressionsJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_array_of_expressions"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfExpressionsTemplate(env, json = it) }
    }
}
