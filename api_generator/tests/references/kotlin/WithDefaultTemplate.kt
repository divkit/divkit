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

class WithDefaultTemplate() : JSONSerializable, JsonTemplate<WithDefault> {

    constructor(
        env: ParsingEnvironment,
        parent: WithDefaultTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this()

    override fun resolve(env: ParsingEnvironment, data: JSONObject): WithDefault {
        return WithDefault()
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "default"

        val TYPE_READER: Reader<String?> = { key, json, env -> JsonParser.readOptional(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> WithDefaultTemplate(env, json = it) }
    }
}
