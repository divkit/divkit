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

@Mockable
class EntityWithSimplePropertiesTemplate : JSONSerializable, JsonTemplate<EntityWithSimpleProperties> {
    @JvmField final val boolean: Field<Expression<Boolean>>
    @JvmField final val booleanInt: Field<Expression<Boolean>>
    @JvmField final val color: Field<Expression<Int>>
    @JvmField final val double: Field<Expression<Double>>
    @JvmField final val id: Field<Int>
    @JvmField final val integer: Field<Expression<Int>>
    @JvmField final val positiveInteger: Field<Expression<Int>> // constraint: number > 0
    @JvmField final val string: Field<Expression<String>> // at least 1 char
    @JvmField final val url: Field<Expression<Uri>>

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithSimplePropertiesTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        boolean = JsonTemplateParser.readOptionalFieldWithExpression(json, "boolean", topLevel, parent?.boolean, NUMBER_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN)
        booleanInt = JsonTemplateParser.readOptionalFieldWithExpression(json, "boolean_int", topLevel, parent?.booleanInt, NUMBER_TO_BOOLEAN, logger, env, TYPE_HELPER_BOOLEAN)
        color = JsonTemplateParser.readOptionalFieldWithExpression(json, "color", topLevel, parent?.color, STRING_TO_COLOR_INT, logger, env, TYPE_HELPER_COLOR)
        double = JsonTemplateParser.readOptionalFieldWithExpression(json, "double", topLevel, parent?.double, NUMBER_TO_DOUBLE, logger, env, TYPE_HELPER_DOUBLE)
        id = JsonTemplateParser.readOptionalField(json, "id", topLevel, parent?.id, NUMBER_TO_INT, logger, env)
        integer = JsonTemplateParser.readOptionalFieldWithExpression(json, "integer", topLevel, parent?.integer, NUMBER_TO_INT, logger, env, TYPE_HELPER_INT)
        positiveInteger = JsonTemplateParser.readOptionalFieldWithExpression(json, "positive_integer", topLevel, parent?.positiveInteger, NUMBER_TO_INT, POSITIVE_INTEGER_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_INT)
        string = JsonTemplateParser.readOptionalFieldWithExpression(json, "string", topLevel, parent?.string, STRING_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_STRING)
        url = JsonTemplateParser.readOptionalFieldWithExpression(json, "url", topLevel, parent?.url, STRING_TO_URI, logger, env, TYPE_HELPER_URI)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithSimpleProperties {
        return EntityWithSimpleProperties(
            boolean = boolean.resolveOptional(env = env, key = "boolean", data = data, reader = BOOLEAN_READER),
            booleanInt = booleanInt.resolveOptional(env = env, key = "boolean_int", data = data, reader = BOOLEAN_INT_READER),
            color = color.resolveOptional(env = env, key = "color", data = data, reader = COLOR_READER),
            double = double.resolveOptional(env = env, key = "double", data = data, reader = DOUBLE_READER),
            id = id.resolveOptional(env = env, key = "id", data = data, reader = ID_READER),
            integer = integer.resolveOptional(env = env, key = "integer", data = data, reader = INTEGER_READER),
            positiveInteger = positiveInteger.resolveOptional(env = env, key = "positive_integer", data = data, reader = POSITIVE_INTEGER_READER),
            string = string.resolveOptional(env = env, key = "string", data = data, reader = STRING_READER),
            url = url.resolveOptional(env = env, key = "url", data = data, reader = URL_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "boolean", field = boolean, converter = BOOLEAN_TO_INT)
        json.writeFieldWithExpression(key = "boolean_int", field = booleanInt, converter = BOOLEAN_TO_INT)
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

        private val POSITIVE_INTEGER_TEMPLATE_VALIDATOR = ValueValidator<Int> { it: Int -> it > 0 }
        private val POSITIVE_INTEGER_VALIDATOR = ValueValidator<Int> { it: Int -> it > 0 }
        private val STRING_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val STRING_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val BOOLEAN_READER: Reader<Expression<Boolean>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_BOOLEAN, env.logger, env, TYPE_HELPER_BOOLEAN) }
        val BOOLEAN_INT_READER: Reader<Expression<Boolean>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_BOOLEAN, env.logger, env, TYPE_HELPER_BOOLEAN) }
        val COLOR_READER: Reader<Expression<Int>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, STRING_TO_COLOR_INT, env.logger, env, TYPE_HELPER_COLOR) }
        val DOUBLE_READER: Reader<Expression<Double>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_DOUBLE, env.logger, env, TYPE_HELPER_DOUBLE) }
        val ID_READER: Reader<Int?> = { key, json, env -> JsonParser.readOptional(json, key, NUMBER_TO_INT, env.logger, env) }
        val INTEGER_READER: Reader<Expression<Int>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_INT, env.logger, env, TYPE_HELPER_INT) }
        val POSITIVE_INTEGER_READER: Reader<Expression<Int>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_INT, POSITIVE_INTEGER_VALIDATOR, env.logger, env, TYPE_HELPER_INT) }
        val STRING_READER: Reader<Expression<String>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, STRING_VALIDATOR, env.logger, env, TYPE_HELPER_STRING) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }
        val URL_READER: Reader<Expression<Uri>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, STRING_TO_URI, env.logger, env, TYPE_HELPER_URI) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithSimplePropertiesTemplate(env, json = it) }
    }

}
