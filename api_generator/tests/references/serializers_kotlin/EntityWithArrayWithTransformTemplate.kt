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

class EntityWithArrayWithTransformTemplate(
    @JvmField val array: Field<ExpressionList<Int>>,
) : JSONSerializable, JsonTemplate<EntityWithArrayWithTransform> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithArrayWithTransformTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        array = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayWithTransform {
        return builtInParserComponent.entityWithArrayWithTransformJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithArrayWithTransformJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_array_with_transform"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayWithTransformTemplate(env, json = it) }
    }
}
