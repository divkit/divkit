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
            return EntityWithArrayOfEnums(
                items = JsonPropertyParser.readList(context, data, "items", EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfEnums): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.writeList(context, data, "items", value.items, EntityWithArrayOfEnums.Item.TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithArrayOfEnums.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayOfEnumsTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayOfEnumsTemplate?, data: JSONObject): EntityWithArrayOfEnumsTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayOfEnumsTemplate(
                items = JsonFieldParser.readListField(context, data, "items", allowOverride, parent?.items, EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfEnumsTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeListField(context, data, "items", value.items, EntityWithArrayOfEnums.Item.TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithArrayOfEnums.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayOfEnumsTemplate, EntityWithArrayOfEnums> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayOfEnumsTemplate, data: JSONObject): EntityWithArrayOfEnums {
            return EntityWithArrayOfEnums(
                items = JsonFieldResolver.resolveList(context, template.items, data, "items", EntityWithArrayOfEnums.Item.FROM_STRING, ITEMS_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfEnums.Item> { it: List<*> -> it.size >= 1 }
    }
}
