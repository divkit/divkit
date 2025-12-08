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

class EntityWithSimplePropertiesTemplate(
    @JvmField val boolean: Field<Expression<Boolean>>,
    @JvmField val booleanInt: Field<Expression<Boolean>>,
    @JvmField val color: Field<Expression<Int>>,
    @JvmField val double: Field<Expression<Double>>,
    @JvmField val id: Field<Long>,
    @JvmField val integer: Field<Expression<Long>>,
    @JvmField val positiveInteger: Field<Expression<Long>>,
    @JvmField val string: Field<Expression<String>>,
    @JvmField val url: Field<Expression<Uri>>,
) : JSONSerializable, JsonTemplate<EntityWithSimpleProperties> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithSimplePropertiesTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        boolean = Field.nullField(false),
        booleanInt = Field.nullField(false),
        color = Field.nullField(false),
        double = Field.nullField(false),
        id = Field.nullField(false),
        integer = Field.nullField(false),
        positiveInteger = Field.nullField(false),
        string = Field.nullField(false),
        url = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithSimpleProperties {
        return builtInParserComponent.entityWithSimplePropertiesJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithSimplePropertiesJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_simple_properties"

        private val ID_DEFAULT_VALUE = 0L
        private val INTEGER_DEFAULT_VALUE = Expression.constant(0L)

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithSimplePropertiesTemplate(env, json = it) }
    }
}
