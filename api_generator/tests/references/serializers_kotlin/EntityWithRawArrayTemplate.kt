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

class EntityWithRawArrayTemplate : JSONSerializable, JsonTemplate<EntityWithRawArray> {
    @JvmField val array: Field<Expression<JSONArray>>

    constructor(
        array: Field<Expression<JSONArray>>,
    ) {
        this.array = array
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithRawArrayTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        array = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithRawArray {
        return builtInParserComponent.entityWithRawArrayJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithRawArrayJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_raw_array"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithRawArrayTemplate(env, json = it) }
    }
}
