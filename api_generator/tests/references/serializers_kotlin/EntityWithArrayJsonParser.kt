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
            val logger = context.logger
            return EntityWithArray(
                array = JsonPropertyParser.readList(context, logger, data, "array", component.entityJsonEntityParser, ARRAY_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArray): JSONObject {
            val data = JSONObject()
            data.write(key = "array", value = component.entityJsonEntityParser.value.serialize(context, value.array))
            data.write(key = "type", value = EntityWithArray.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayTemplate?, data: JSONObject): EntityWithArrayTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayTemplate(
                array = JsonFieldParser.readListField(context, logger, data, "array", allowOverride, parent?.array, component.entityJsonTemplateParser, ARRAY_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayTemplate): JSONObject {
            val data = JSONObject()
            data.writeField(key = "array", field = value.array, converter = component.entityJsonTemplateParser.value.asConverter(context))
            data.write(key = "type", value = EntityWithArray.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayTemplate, EntityWithArray> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayTemplate, data: JSONObject): EntityWithArray {
            val logger = context.logger
            return EntityWithArray(
                array = JsonFieldResolver.resolveList(context, logger, template.array, data, "array", component.entityJsonTemplateResolver, component.entityJsonEntityParser, ARRAY_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ARRAY_VALIDATOR = ListValidator<Entity> { it: List<*> -> it.size >= 1 }
    }
}
