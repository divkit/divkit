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

internal class WithoutDefaultJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, WithoutDefault> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): WithoutDefault {
            return WithoutDefault()
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: WithoutDefault): JSONObject {
            val data = JSONObject()
            data.write(key = "type", value = WithoutDefault.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, WithoutDefaultTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: WithoutDefaultTemplate?, data: JSONObject): WithoutDefaultTemplate {
            return WithoutDefaultTemplate()
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: WithoutDefaultTemplate): JSONObject {
            val data = JSONObject()
            data.write(key = "type", value = WithoutDefault.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, WithoutDefaultTemplate, WithoutDefault> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: WithoutDefaultTemplate, data: JSONObject): WithoutDefault {
            return WithoutDefault()
        }
    }
}
