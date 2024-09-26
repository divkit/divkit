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

class EntityWithStringEnumPropertyWithDefaultValueTemplate : JSONSerializable, JsonTemplate<EntityWithStringEnumPropertyWithDefaultValue> {
    @JvmField val value: Field<Expression<EntityWithStringEnumPropertyWithDefaultValue.Value>>

    constructor(
        value: Field<Expression<EntityWithStringEnumPropertyWithDefaultValue.Value>>,
    ) {
        this.value = value
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithStringEnumPropertyWithDefaultValueTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        value = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithStringEnumPropertyWithDefaultValue {
        return builtInParserComponent.entityWithStringEnumPropertyWithDefaultValueJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithStringEnumPropertyWithDefaultValueJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_string_enum_property_with_default_value"

        private val VALUE_DEFAULT_VALUE = Expression.constant(EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND)

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringEnumPropertyWithDefaultValueTemplate(env, json = it) }
    }
}
