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

internal class EntityWithStringArrayPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithStringArrayProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithStringArrayProperty {
            return EntityWithStringArrayProperty(
                array = JsonExpressionParser.readExpressionList(context, data, "array", TYPE_HELPER_STRING, ARRAY_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringArrayProperty): JSONObject {
            val data = JSONObject()
            data.writeExpressionList(key = "array", value = value.array)
            data.write(key = "type", value = EntityWithStringArrayProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithStringArrayPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithStringArrayPropertyTemplate?, data: JSONObject): EntityWithStringArrayPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithStringArrayPropertyTemplate(
                array = JsonFieldParser.readExpressionListField(context, data, "array", TYPE_HELPER_STRING, allowOverride, parent?.array, ARRAY_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringArrayPropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeExpressionListField(key = "array", field = value.array)
            data.write(key = "type", value = EntityWithStringArrayProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithStringArrayPropertyTemplate, EntityWithStringArrayProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithStringArrayPropertyTemplate, data: JSONObject): EntityWithStringArrayProperty {
            return EntityWithStringArrayProperty(
                array = JsonFieldResolver.resolveExpressionList(context, template.array, data, "array", TYPE_HELPER_STRING, ARRAY_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ARRAY_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
    }
}
