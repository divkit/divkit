// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*
import org.json.JSONArray

@Mockable
class WithDefaultTemplate : JSONSerializable, JsonTemplate<WithDefault> {

    constructor (
        env: ParsingEnvironment,
        parent: WithDefaultTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): WithDefault {
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
