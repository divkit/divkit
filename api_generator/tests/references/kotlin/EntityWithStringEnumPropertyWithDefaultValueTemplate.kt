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
class EntityWithStringEnumPropertyWithDefaultValueTemplate : JSONSerializable, JsonTemplate<EntityWithStringEnumPropertyWithDefaultValue> {
    @JvmField final val value: Field<Expression<EntityWithStringEnumPropertyWithDefaultValue.Value>> // default value: second

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithStringEnumPropertyWithDefaultValueTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        value = JsonTemplateParser.readOptionalFieldWithExpression(json, "value", topLevel, parent?.value, EntityWithStringEnumPropertyWithDefaultValue.Value.FROM_STRING, logger, env, TYPE_HELPER_VALUE)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithStringEnumPropertyWithDefaultValue {
        return EntityWithStringEnumPropertyWithDefaultValue(
            value = value.resolveOptional(env = env, key = "value", data = rawData, reader = VALUE_READER) ?: VALUE_DEFAULT_VALUE
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "type", value = TYPE)
        json.writeFieldWithExpression(key = "value", field = value, converter = EntityWithStringEnumPropertyWithDefaultValue.Value.TO_STRING)
        return json
    }

    companion object {
        const val TYPE = "entity_with_string_enum_property_with_default_value"

        private val VALUE_DEFAULT_VALUE = Expression.constant(EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND)

        private val TYPE_HELPER_VALUE = TypeHelper.from(default = EntityWithStringEnumPropertyWithDefaultValue.Value.values().first()) { it is EntityWithStringEnumPropertyWithDefaultValue.Value }

        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }
        val VALUE_READER: Reader<Expression<EntityWithStringEnumPropertyWithDefaultValue.Value>> = { key, json, env -> JsonParser.readOptionalExpression(json, key, EntityWithStringEnumPropertyWithDefaultValue.Value.FROM_STRING, env.logger, env, VALUE_DEFAULT_VALUE, TYPE_HELPER_VALUE) ?: VALUE_DEFAULT_VALUE }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringEnumPropertyWithDefaultValueTemplate(env, json = it) }
    }
}
