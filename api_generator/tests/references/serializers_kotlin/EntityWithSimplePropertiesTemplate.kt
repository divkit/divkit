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
