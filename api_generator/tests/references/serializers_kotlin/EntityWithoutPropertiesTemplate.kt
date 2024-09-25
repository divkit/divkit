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

class EntityWithoutPropertiesTemplate : JSONSerializable, JsonTemplate<EntityWithoutProperties> {

    constructor()

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithoutPropertiesTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this() {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithoutProperties {
        return builtInParserComponent.entityWithoutPropertiesJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithoutPropertiesJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_without_properties"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithoutPropertiesTemplate(env, json = it) }
    }
}
