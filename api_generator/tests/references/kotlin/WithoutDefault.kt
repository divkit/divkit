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

class WithoutDefault() : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = this::class.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: WithoutDefault?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        return other != null
    }

    fun copy() = WithoutDefault()

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "non_default"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): WithoutDefault {
            val logger = env.logger
            return WithoutDefault(
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> WithoutDefault(env, json = it) }
    }
}
