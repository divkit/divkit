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

internal class EntityWithArrayOfEnumsJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithArrayOfEnums> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithArrayOfEnums {
            val logger = context.logger
            return EntityWithArrayOfEnums(
                items = JsonPropertyParser.readList(context, logger, data, "items", EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfEnums): JSONObject {
            val data = JSONObject()
            data.write(key = "items", value = value.items, converter = EntityWithArrayOfEnums.Item.TO_STRING)
            data.write(key = "type", value = EntityWithArrayOfEnums.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayOfEnumsTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayOfEnumsTemplate?, data: JSONObject): EntityWithArrayOfEnumsTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayOfEnumsTemplate(
                items = JsonFieldParser.readListField(context, logger, data, "items", allowOverride, parent?.items, EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfEnumsTemplate): JSONObject {
            val data = JSONObject()
            data.writeField(key = "items", field = value.items, converter = EntityWithArrayOfEnums.Item.TO_STRING)
            data.write(key = "type", value = EntityWithArrayOfEnums.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayOfEnumsTemplate, EntityWithArrayOfEnums> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayOfEnumsTemplate, data: JSONObject): EntityWithArrayOfEnums {
            val logger = context.logger
            return EntityWithArrayOfEnums(
                items = JsonFieldResolver.resolveList(context, logger, template.items, data, "items", EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfEnums.Item> { it: List<*> -> it.size >= 1 }
    }
}
