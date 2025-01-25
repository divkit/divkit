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

internal class EntityWithArrayJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithArray> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithArray {
            return EntityWithArray(
                array = JsonPropertyParser.readList(context, data, "array", component.entityJsonEntityParser, ARRAY_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArray): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.writeList(context, data, "array", value.array, component.entityJsonEntityParser)
            JsonPropertyParser.write(context, data, "type", EntityWithArray.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayTemplate?, data: JSONObject): EntityWithArrayTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayTemplate(
                array = JsonFieldParser.readListField(context, data, "array", allowOverride, parent?.array, component.entityJsonTemplateParser, ARRAY_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeListField(context, data, "array", value.array, component.entityJsonTemplateParser)
            JsonPropertyParser.write(context, data, "type", EntityWithArray.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayTemplate, EntityWithArray> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayTemplate, data: JSONObject): EntityWithArray {
            return EntityWithArray(
                array = JsonFieldResolver.resolveList(context, template.array, data, "array", component.entityJsonTemplateResolver, component.entityJsonEntityParser, ARRAY_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ARRAY_VALIDATOR = ListValidator<Entity> { it: List<*> -> it.size >= 1 }
    }
}
