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

class WithDefaultTemplate : JSONSerializable, JsonTemplate<WithDefault> {

    constructor()

    constructor(
        env: ParsingEnvironment,
        parent: WithDefaultTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this() {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): WithDefault {
        return builtInParserComponent.withDefaultJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.withDefaultJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "default"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> WithDefaultTemplate(env, json = it) }
    }
}
