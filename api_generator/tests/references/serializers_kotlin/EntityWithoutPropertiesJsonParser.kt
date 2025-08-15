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

internal class EntityWithoutPropertiesJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithoutProperties> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithoutProperties {
            return EntityWithoutProperties()
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithoutProperties): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.write(context, data, "type", EntityWithoutProperties.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithoutPropertiesTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithoutPropertiesTemplate?, data: JSONObject): EntityWithoutPropertiesTemplate {
            return EntityWithoutPropertiesTemplate()
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithoutPropertiesTemplate): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.write(context, data, "type", EntityWithoutProperties.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithoutPropertiesTemplate, EntityWithoutProperties> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithoutPropertiesTemplate, data: JSONObject): EntityWithoutProperties {
            return EntityWithoutProperties()
        }
    }
}
