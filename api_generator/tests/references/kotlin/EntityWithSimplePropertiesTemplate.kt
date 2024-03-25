// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*
import org.json.JSONArray

@Mockable
class EntityWithSimplePropertiesTemplate : JSONSerializable, JsonTemplate<EntityWithSimpleProperties> {
    @JvmField final val boolean: Field<Expression<Boolean>>
    @JvmField final val booleanInt: Field<Expression<Boolean>>
    @JvmField final val color: Field<Expression<Int>>
    @JvmField final val double: Field<Expression<Double>>
    @JvmField final val id: Field<Long> // default value: 0
    @JvmField final val integer: Field<Expression<Long>> // default value: 0
    @JvmField final val positiveInteger: Field<Expression<Long>> // constraint: number > 0
    @JvmField final val string: Field<Expression<String>>
    @JvmField final val url: Field<Expression<Uri>>

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithSimplePropertiesTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        var boolean: Field<Expression<Boolean>>? = null
        var booleanInt: Field<Expression<Boolean>>? = null
        var color: Field<Expression<Int>>? = null
        var double: Field<Expression<Double>>? = null
        var id: Field<Long>? = null
        var integer: Field<Expression<Long>>? = null
        var positiveInteger: Field<Expression<Long>>? = null
        var string: Field<Expression<String>>? = null
        var url: Field<Expression<Uri>>? = null
        for (jsonKey in json.keys()) {
            when (jsonKey) {
                "boolean" -> boolean = JsonTemplateParser.readOptionalFieldWithExpression(json, "boolean", topLevel, parent?.boolean, ANY_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN)
                "\$boolean" -> boolean = boolean ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "boolean", topLevel, logger, env)
                "boolean_int" -> booleanInt = JsonTemplateParser.readOptionalFieldWithExpression(json, "boolean_int", topLevel, parent?.booleanInt, ANY_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN)
                "\$boolean_int" -> booleanInt = booleanInt ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "boolean_int", topLevel, logger, env)
                "color" -> color = JsonTemplateParser.readOptionalFieldWithExpression(json, "color", topLevel, parent?.color, STRING_TO_COLOR_INT, logger, env, TYPE_HELPER_COLOR)
                "\$color" -> color = color ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "color", topLevel, logger, env)
                "double" -> double = JsonTemplateParser.readOptionalFieldWithExpression(json, "double", topLevel, parent?.double, NUMBER_TO_DOUBLE, logger, env, TYPE_HELPER_DOUBLE)
                "\$double" -> double = double ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "double", topLevel, logger, env)
                "id" -> id = JsonTemplateParser.readOptionalField(json, "id", topLevel, parent?.id, NUMBER_TO_INT, logger, env)
                "\$id" -> id = id ?: JsonTemplateParser.readOptionalReferenceField(json, "id", topLevel, logger, env)
                "integer" -> integer = JsonTemplateParser.readOptionalFieldWithExpression(json, "integer", topLevel, parent?.integer, NUMBER_TO_INT, logger, env, TYPE_HELPER_INT)
                "\$integer" -> integer = integer ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "integer", topLevel, logger, env)
                "positive_integer" -> positiveInteger = JsonTemplateParser.readOptionalFieldWithExpression(json, "positive_integer", topLevel, parent?.positiveInteger, NUMBER_TO_INT, POSITIVE_INTEGER_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_INT)
                "\$positive_integer" -> positiveInteger = positiveInteger ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "positive_integer", topLevel, logger, env)
                "string" -> string = JsonTemplateParser.readOptionalFieldWithExpression(json, "string", topLevel, parent?.string, logger, env, TYPE_HELPER_STRING)
                "\$string" -> string = string ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "string", topLevel, logger, env)
                "url" -> url = JsonTemplateParser.readOptionalFieldWithExpression(json, "url", topLevel, parent?.url, STRING_TO_URI, logger, env, TYPE_HELPER_URI)
                "\$url" -> url = url ?: JsonTemplateParser.readOptionalReferenceFieldWithExpression(json, "url", topLevel, logger, env)
            }
        }
        this.boolean = boolean ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.boolean)
        this.booleanInt = booleanInt ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.booleanInt)
        this.color = color ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.color)
        this.double = double ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.double)
        this.id = id ?: JsonTemplateParser.readOptionalFallbackField(topLevel, parent?.id)
        this.integer = integer ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.integer)
        this.positiveInteger = positiveInteger ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.positiveInteger)
        this.string = string ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.string)
        this.url = url ?: JsonTemplateParser.readOptionalFallbackFieldWithExpression(topLevel, parent?.url)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithSimpleProperties {
        return EntityWithSimpleProperties(
            boolean = boolean.resolveOptional(env = env, key = "boolean", data = rawData, reader = BOOLEAN_READER),
            booleanInt = booleanInt.resolveOptional(env = env, key = "boolean_int", data = rawData, reader = BOOLEAN_INT_READER),
            color = color.resolveOptional(env = env, key = "color", data = rawData, reader = COLOR_READER),
            double = double.resolveOptional(env = env, key = "double", data = rawData, reader = DOUBLE_READER),
            id = id.resolveOptional(env = env, key = "id", data = rawData, reader = ID_READER) ?: ID_DEFAULT_VALUE,
            integer = integer.resolveOptional(env = env, key = "integer", data = rawData, reader = INTEGER_READER) ?: INTEGER_DEFAULT_VALUE,
            positiveInteger = positiveInteger.resolveOptional(env = env, key = "positive_integer", data = rawData, reader = POSITIVE_INTEGER_READER),
            string = string.resolveOptional(env = env, key = "string", data = rawData, reader = STRING_READER),
            url = url.resolveOptional(env = env, key = "url", data = rawData, reader = URL_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "boolean", field = boolean)
        json.writeFieldWithExpression(key = "boolean_int", field = booleanInt)
        json.writeFieldWithExpression(key = "color", field = color, converter = COLOR_INT_TO_STRING)
        json.writeFieldWithExpression(key = "double", field = double)
        json.writeField(key = "id", field = id)
        json.writeFieldWithExpression(key = "integer", field = integer)
        json.writeFieldWithExpression(key = "positive_integer", field = positiveInteger)
        json.writeFieldWithExpression(key = "string", field = string)
        json.write(key = "type", value = TYPE)
        json.writeFieldWithExpression(key = "url", field = url, converter = URI_TO_STRING)
        return json
    }

    companion object {
        const val TYPE = "entity_with_simple_properties"

        private val ID_DEFAULT_VALUE = 0L
        private val INTEGER_DEFAULT_VALUE = Expression.constant(0L)

        private val POSITIVE_INTEGER_TEMPLATE_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
        private val POSITIVE_INTEGER_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }

        val BOOLEAN_READER: Reader<Expression<Boolean>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, ANY_TO_BOOLEAN, env.logger, env, TYPE_HELPER_BOOLEAN) }
        val BOOLEAN_INT_READER: Reader<Expression<Boolean>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, ANY_TO_BOOLEAN, env.logger, env, TYPE_HELPER_BOOLEAN) }
        val COLOR_READER: Reader<Expression<Int>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, STRING_TO_COLOR_INT, env.logger, env, TYPE_HELPER_COLOR) }
        val DOUBLE_READER: Reader<Expression<Double>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_DOUBLE, env.logger, env, TYPE_HELPER_DOUBLE) }
        val ID_READER: Reader<Long> = { key, json, env -> JsonParser.readOptional(json, key, NUMBER_TO_INT, env.logger, env) ?: ID_DEFAULT_VALUE }
        val INTEGER_READER: Reader<Expression<Long>> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_INT, env.logger, env, INTEGER_DEFAULT_VALUE, TYPE_HELPER_INT) ?: INTEGER_DEFAULT_VALUE }
        val POSITIVE_INTEGER_READER: Reader<Expression<Long>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_INT, POSITIVE_INTEGER_VALIDATOR, env.logger, env, TYPE_HELPER_INT) }
        val STRING_READER: Reader<Expression<String>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, env.logger, env, TYPE_HELPER_STRING) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }
        val URL_READER: Reader<Expression<Uri>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, STRING_TO_URI, env.logger, env, TYPE_HELPER_URI) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithSimplePropertiesTemplate(env, json = it) }
    }

}
