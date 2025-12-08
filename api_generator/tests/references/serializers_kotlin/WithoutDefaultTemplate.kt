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

class WithoutDefaultTemplate() : JSONSerializable, JsonTemplate<WithoutDefault> {

    constructor(
        env: ParsingEnvironment,
        parent: WithoutDefaultTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this()

    override fun resolve(env: ParsingEnvironment, data: JSONObject): WithoutDefault {
        return builtInParserComponent.withoutDefaultJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.withoutDefaultJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "non_default"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> WithoutDefaultTemplate(env, json = it) }
    }
}
