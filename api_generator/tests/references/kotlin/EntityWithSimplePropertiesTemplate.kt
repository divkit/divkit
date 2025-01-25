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

class EntityWithSimplePropertiesTemplate : JSONSerializable, JsonTemplate<EntityWithSimpleProperties> {
    @JvmField val boolean: Field<Expression<Boolean>>
    @JvmField val booleanInt: Field<Expression<Boolean>>
    @JvmField val color: Field<Expression<Int>>
    @JvmField val double: Field<Expression<Double>>
    @JvmField val id: Field<Long>
    @JvmField val integer: Field<Expression<Long>>
    @JvmField val positiveInteger: Field<Expression<Long>>
    @JvmField val string: Field<Expression<String>>
    @JvmField val url: Field<Expression<Uri>>

    constructor(
        boolean: Field<Expression<Boolean>>,
        booleanInt: Field<Expression<Boolean>>,
        color: Field<Expression<Int>>,
        double: Field<Expression<Double>>,
        id: Field<Long>,
        integer: Field<Expression<Long>>,
        positiveInteger: Field<Expression<Long>>,
        string: Field<Expression<String>>,
        url: Field<Expression<Uri>>,
    ) {
        this.boolean = boolean
        this.booleanInt = booleanInt
        this.color = color
        this.double = double
        this.id = id
        this.integer = integer
        this.positiveInteger = positiveInteger
        this.string = string
        this.url = url
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithSimplePropertiesTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        boolean = JsonTemplateParser.readOptionalFieldWithExpression(json, "boolean", topLevel, parent?.boolean, ANY_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN)
        booleanInt = JsonTemplateParser.readOptionalFieldWithExpression(json, "boolean_int", topLevel, parent?.booleanInt, ANY_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN)
        color = JsonTemplateParser.readOptionalFieldWithExpression(json, "color", topLevel, parent?.color, STRING_TO_COLOR_INT, logger, env, TYPE_HELPER_COLOR)
        double = JsonTemplateParser.readOptionalFieldWithExpression(json, "double", topLevel, parent?.double, NUMBER_TO_DOUBLE, logger, env, TYPE_HELPER_DOUBLE)
        id = JsonTemplateParser.readOptionalField(json, "id", topLevel, parent?.id, NUMBER_TO_INT, logger, env)
        integer = JsonTemplateParser.readOptionalFieldWithExpression(json, "integer", topLevel, parent?.integer, NUMBER_TO_INT, logger, env, TYPE_HELPER_INT)
        positiveInteger = JsonTemplateParser.readOptionalFieldWithExpression(json, "positive_integer", topLevel, parent?.positiveInteger, NUMBER_TO_INT, POSITIVE_INTEGER_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_INT)
        string = JsonTemplateParser.readOptionalFieldWithExpression(json, "string", topLevel, parent?.string, logger, env, TYPE_HELPER_STRING)
        url = JsonTemplateParser.readOptionalFieldWithExpression(json, "url", topLevel, parent?.url, ANY_TO_URI, logger, env, TYPE_HELPER_URI)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithSimpleProperties {
        return EntityWithSimpleProperties(
            boolean = this.boolean.resolveOptional(env = env, key = "boolean", data = data, reader = BOOLEAN_READER),
            booleanInt = this.booleanInt.resolveOptional(env = env, key = "boolean_int", data = data, reader = BOOLEAN_INT_READER),
            color = this.color.resolveOptional(env = env, key = "color", data = data, reader = COLOR_READER),
            double = this.double.resolveOptional(env = env, key = "double", data = data, reader = DOUBLE_READER),
            id = this.id.resolveOptional(env = env, key = "id", data = data, reader = ID_READER) ?: ID_DEFAULT_VALUE,
            integer = this.integer.resolveOptional(env = env, key = "integer", data = data, reader = INTEGER_READER) ?: INTEGER_DEFAULT_VALUE,
            positiveInteger = this.positiveInteger.resolveOptional(env = env, key = "positive_integer", data = data, reader = POSITIVE_INTEGER_READER),
            string = this.string.resolveOptional(env = env, key = "string", data = data, reader = STRING_READER),
            url = this.url.resolveOptional(env = env, key = "url", data = data, reader = URL_READER)
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
        val URL_READER: Reader<Expression<Uri>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, ANY_TO_URI, env.logger, env, TYPE_HELPER_URI) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithSimplePropertiesTemplate(env, json = it) }
    }
}
