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
        return builtInParserComponent.enumWithDefaultTypeJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EnumWithDefaultType {
        return builtInParserComponent.enumWithDefaultTypeJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
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
            return builtInParserComponent.enumWithDefaultTypeJsonTemplateParser
                .value
                .deserialize(context = env, data = json)
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EnumWithDefaultTypeTemplate(env, json = it) }
    }
}
