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

internal class WithDefaultJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, WithDefault> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): WithDefault {
            return WithDefault()
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: WithDefault): JSONObject {
            val data = JSONObject()
            data.write(key = "type", value = WithDefault.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, WithDefaultTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: WithDefaultTemplate?, data: JSONObject): WithDefaultTemplate {
            return WithDefaultTemplate()
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: WithDefaultTemplate): JSONObject {
            val data = JSONObject()
            data.write(key = "type", value = WithDefault.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, WithDefaultTemplate, WithDefault> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: WithDefaultTemplate, data: JSONObject): WithDefault {
            return WithDefault()
        }
    }
}
