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

sealed class EnumWithDefaultTypeTemplate : JSONSerializable, JsonTemplate<EnumWithDefaultType> {
    class WithDefaultCase(val value: WithDefaultTemplate) : EnumWithDefaultTypeTemplate()
    class WithoutDefaultCase(val value: WithoutDefaultTemplate) : EnumWithDefaultTypeTemplate()

    fun value(): Any {
        return when (this) {
            is WithDefaultCase -> value
            is WithoutDefaultCase -> value
        }
    }

    override fun writeToJSON(): JSONObject {
        return when (this) {
            is WithDefaultCase -> value.writeToJSON()
            is WithoutDefaultCase -> value.writeToJSON()
        }
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EnumWithDefaultType {
        return when (this) {
            is WithDefaultCase -> EnumWithDefaultType.WithDefaultCase(value.resolve(env, data))
            is WithoutDefaultCase -> EnumWithDefaultType.WithoutDefaultCase(value.resolve(env, data))
        }
    }

    val type: String
        get() {
            return when (this) {
                is WithDefaultCase -> WithDefaultTemplate.TYPE
                is WithoutDefaultCase -> WithoutDefaultTemplate.TYPE
            }
        }

    companion object {

        @Throws(ParsingException::class)
        operator fun invoke(
            env: ParsingEnvironment,
            topLevel: Boolean = false,
            json: JSONObject
        ): EnumWithDefaultTypeTemplate {
            val logger = env.logger
            val receivedType: String = json.readOptional("type", logger = logger, env = env) ?: WithDefaultTemplate.TYPE
            val parent = env.templates[receivedType] as? EnumWithDefaultTypeTemplate
            val type = parent?.type ?: receivedType
            when (type) {
                WithDefaultTemplate.TYPE -> return WithDefaultCase(WithDefaultTemplate(env, parent?.value() as WithDefaultTemplate?, topLevel, json))
                WithoutDefaultTemplate.TYPE -> return WithoutDefaultCase(WithoutDefaultTemplate(env, parent?.value() as WithoutDefaultTemplate?, topLevel, json))
                else -> throw typeMismatch(json = json, key = "type", value = type)
            }
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EnumWithDefaultTypeTemplate(env, json = it) }
    }
}
