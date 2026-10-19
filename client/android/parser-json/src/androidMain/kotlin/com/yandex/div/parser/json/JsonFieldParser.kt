@file:Suppress("unused")

package com.yandex.div.parser.json

import com.yandex.div.core.json.JsonObject
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.template.Field
import com.yandex.div.internal.template.clone
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.parser.json.util.reference
import com.yandex.div.serialization.Deserializer
import com.yandex.div.serialization.ParsingContext
import com.yandex.div.serialization.Serializer

internal object JsonFieldParser {

    @JvmStatic
    fun <V : Any> readField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
    ): Field<V> {
        return readField(context, json, key, overridable, fallback, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        transform: Transform<R, V?>,
    ): Field<V> {
        return readField(context, json, key, overridable, fallback, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        validator: ValueValidator<V>,
    ): Field<V> {
        return readField(context, json, key, overridable, fallback, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> readField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Field<V> {
        return try {
            val value = JsonPropertyParser.read(context, json, key, transform, validator)
            valueOrReferenceWithFallback(context, json, key, overridable, value)
        } catch (e: ParsingException) {
            suppressMissingValueOrThrow(e)
            referenceOrFallback(overridable, readReference(context, json, key), fallback) ?: throw e
        }
    }

    @JvmStatic
    fun <V : Any> readField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): Field<V> {
        return try {
            val value = JsonPropertyParser.read(context, json, key, deserializer)
            valueOrReferenceWithFallback(context, json, key, overridable, value)
        } catch (e: ParsingException) {
            suppressMissingValueOrThrow(e)
            referenceOrFallback(overridable, readReference(context, json, key), fallback) ?: throw e
        }
    }

    @JvmStatic
    fun <V : Any> readOptionalField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
    ): Field<V> {
        return readOptionalField(context, json, key, overridable, fallback, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptionalField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        transform: Transform<R, V?>,
    ): Field<V> {
        return readOptionalField(context, json, key, overridable, fallback, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptionalField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        validator: ValueValidator<V>,
    ): Field<V> {
        return readOptionalField(context, json, key, overridable, fallback, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Field<V> {
        val value = JsonPropertyParser.readOptional(context, json, key, transform, validator)
        if (value != null) {
            return valueOrReferenceWithFallback(context, json, key, overridable, value)
        }
        return optionalFallback(context, json, key, overridable, fallback)
    }

    @JvmStatic
    fun <V : Any> readOptionalField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): Field<V> {
        val value = JsonPropertyParser.readOptional(context, json, key, deserializer)
        if (value != null) {
            return valueOrReferenceWithFallback(context, json, key, overridable, value)
        }
        return optionalFallback(context, json, key, overridable, fallback)
    }

    @JvmStatic
    fun <V : Any> readFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
    ): Field<Expression<V>> {
        return readFieldWithExpression(
            context, json, key, typeHelper, overridable, fallback, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
        transform: Transform<R, V?>,
    ): Field<Expression<V>> {
        return readFieldWithExpression(context, json, key, typeHelper, overridable, fallback, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
        validator: ValueValidator<V>,
    ): Field<Expression<V>> {
        return readFieldWithExpression(
            context, json, key, typeHelper, overridable, fallback, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> readFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Field<Expression<V>> {
        return try {
            val expression = JsonExpressionParser.readExpression(context, json, key, typeHelper, transform, validator)
            valueOrReferenceWithFallback(context, json, key, overridable, expression)
        } catch (e: ParsingException) {
            suppressMissingValueOrThrow(e)
            referenceOrFallback(overridable, readReference(context, json, key), fallback) ?: throw e
        }
    }

    @JvmStatic
    fun <V : Any> readOptionalFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
    ): Field<Expression<V>> {
        return readOptionalFieldWithExpression(
            context, json, key, typeHelper, overridable, fallback, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptionalFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
        transform: Transform<R, V?>,
    ): Field<Expression<V>> {
        return readOptionalFieldWithExpression(
            context, json, key, typeHelper, overridable, fallback, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptionalFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
        validator: ValueValidator<V>,
    ): Field<Expression<V>> {
        return readOptionalFieldWithExpression(
            context, json, key, typeHelper, overridable, fallback, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalFieldWithExpression(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<Expression<V>>?,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Field<Expression<V>> {
        val expression = JsonExpressionParser.readOptionalExpression(
            context, json, key, typeHelper, transform, validator, null)
        if (expression != null) {
            return valueOrReferenceWithFallback(context, json, key, overridable, expression)
        }
        return optionalFallback(context, json, key, overridable, fallback)
    }

    @JvmStatic
    fun <R, V : Any> readListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        transform: Transform<R, V?>,
    ): Field<List<V>> {
        return readListField(context, json, key, overridable, fallback, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): Field<List<V>> {
        return readListField(context, json, key, overridable, fallback, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): Field<List<V>> {
        return try {
            val list = JsonPropertyParser.readList(context, json, key, transform, listValidator, itemValidator)
            valueOrReferenceWithFallback(context, json, key, overridable, list)
        } catch (e: ParsingException) {
            suppressMissingValueOrThrow(e)
            referenceOrFallback(overridable, readReference(context, json, key), fallback) ?: throw e
        }
    }

    @JvmStatic
    fun <V : Any> readListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): Field<List<V>> {
        return try {
            val list = JsonPropertyParser.readList(context, json, key, deserializer)
            valueOrReferenceWithFallback(context, json, key, overridable, list)
        } catch (e: ParsingException) {
            suppressMissingValueOrThrow(e)
            referenceOrFallback(overridable, readReference(context, json, key), fallback) ?: throw e
        }
    }

    @JvmStatic
    fun <V : Any> readListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
        listValidator: ListValidator<V>,
    ): Field<List<V>> {
        return try {
            val list = JsonPropertyParser.readList(context, json, key, deserializer, listValidator)
            valueOrReferenceWithFallback(context, json, key, overridable, list)
        } catch (e: ParsingException) {
            suppressMissingValueOrThrow(e)
            referenceOrFallback(overridable, readReference(context, json, key), fallback) ?: throw e
        }
    }

    @JvmStatic
    fun <R, V : Any> readOptionalListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        transform: Transform<R, V?>,
    ): Field<List<V>> {
        return readOptionalListField(context, json, key, overridable, fallback, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptionalListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): Field<List<V>> {
        return readOptionalListField(context, json, key, overridable, fallback, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptionalListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): Field<List<V>> {
        return readOptionalListField(context, json, key, overridable, fallback, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): Field<List<V>> {
        val list = JsonPropertyParser.readOptionalList(context, json, key, transform, listValidator, itemValidator)
        if (list != null) {
            return valueOrReferenceWithFallback(context, json, key, overridable, list)
        }
        return optionalFallback(context, json, key, overridable, fallback)
    }

    @JvmStatic
    fun <V : Any> readOptionalListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): Field<List<V>> {
        val list = JsonPropertyParser.readOptionalList(context, json, key, deserializer)
        if (list != null) {
            return valueOrReferenceWithFallback(context, json, key, overridable, list)
        }
        return optionalFallback(context, json, key, overridable, fallback)
    }

    @JvmStatic
    fun <V : Any> readOptionalListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<List<V>>?,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
        listValidator: ListValidator<V>,
    ): Field<List<V>> {
        val list = JsonPropertyParser.readOptionalList(context, json, key, deserializer, listValidator)
        if (list != null) {
            return valueOrReferenceWithFallback(context, json, key, overridable, list)
        }
        return optionalFallback(context, json, key, overridable, fallback)
    }

    @JvmStatic
    fun <R, V : Any> readExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        transform: Transform<R, V?>,
    ): Field<ExpressionList<V>> {
        return readExpressionListField(context, json, key, typeHelper, overridable, fallback, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): Field<ExpressionList<V>> {
        return readExpressionListField(context, json, key, typeHelper, overridable, fallback, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        listValidator: ListValidator<V>,
    ): Field<ExpressionList<V>> {
        return readExpressionListField(context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): Field<ExpressionList<V>> {
        return readExpressionListField(context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> readExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): Field<ExpressionList<V>> {
        // Like in the Java parser, a missing or invalid required expression list is not an error for templates.
        return readOptionalExpressionListField(
            context, json, key, typeHelper, overridable, fallback, transform, listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        transform: Transform<R, V?>,
    ): Field<ExpressionList<V>> {
        return readOptionalExpressionListField(context, json, key, typeHelper, overridable, fallback, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): Field<ExpressionList<V>> {
        return readOptionalExpressionListField(context, json, key, typeHelper, overridable, fallback, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptionalExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        listValidator: ListValidator<V>,
    ): Field<ExpressionList<V>> {
        return readOptionalExpressionListField(context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptionalExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): Field<ExpressionList<V>> {
        return readOptionalExpressionListField(context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpressionListField(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        overridable: Boolean,
        fallback: Field<ExpressionList<V>>?,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): Field<ExpressionList<V>> {
        val list = JsonExpressionParser.readOptionalExpressionList(
            context, json, key, typeHelper, transform, listValidator, itemValidator)
        if (list != null) {
            return valueOrReferenceWithFallback(context, json, key, overridable, list)
        }
        return optionalFallback(context, json, key, overridable, fallback)
    }

    private fun readReference(context: ParsingContext, json: JsonObject, key: String): String? {
        return JsonPropertyParser.readOptional(context, json, key.reference(), isNotEmpty())
    }

    private fun <V> valueOrReferenceWithFallback(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        value: V,
    ): Field<V> {
        val reference = readReference(context, json, key)
        return if (reference != null) Field.Reference(overridable, reference, value) else Field.Value(overridable, value)
    }

    private fun <V> fallbackValueOf(field: Field<V>?): V? {
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> field.fallbackValue
            else -> null
        }
    }

    private fun <V> referenceOrFallback(overridable: Boolean, reference: String?, fallback: Field<V>?): Field<V>? {
        return when {
            reference != null -> Field.Reference(overridable, reference, fallbackValueOf(fallback))
            fallback != null -> fallback.clone(overridable)
            overridable -> Field.nullField(overridable)
            else -> null
        }
    }

    private fun <V> optionalFallback(
        context: ParsingContext,
        json: JsonObject,
        key: String,
        overridable: Boolean,
        fallback: Field<V>?,
    ): Field<V> {
        return referenceOrFallback(overridable, readReference(context, json, key), fallback) ?: Field.nullField(overridable)
    }


    @JvmStatic
    fun <V : Any> writeField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<V>?,
    ) {
        writeField(context, builder, key, field, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> writeField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<V>?,
        transform: Transform<V, R>,
    ) {
        when (field) {
            is Field.Value -> JsonPropertyParser.write(context, builder, key, field.value, transform)
            is Field.Reference -> {
                builder.put(key.reference(), field.reference)
                JsonPropertyParser.write(context, builder, key, field.fallbackValue, transform)
            }
            else -> Unit
        }
    }

    @JvmStatic
    fun <V : Any> writeField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<V>?,
        serializer: Lazy<Serializer<JsonObject, V>>,
    ) {
        when (field) {
            is Field.Value -> JsonPropertyParser.write(context, builder, key, field.value, serializer)
            is Field.Reference -> {
                builder.put(key.reference(), field.reference)
                JsonPropertyParser.write(context, builder, key, field.fallbackValue, serializer)
            }
            else -> Unit
        }
    }

    @JvmStatic
    fun <V : Any> writeListField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<List<V>>?,
    ) {
        writeListField(context, builder, key, field, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> writeListField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<List<V>>?,
        transform: Transform<V, R>,
    ) {
        when (field) {
            is Field.Value -> JsonPropertyParser.writeList(context, builder, key, field.value, transform)
            is Field.Reference -> {
                builder.put(key.reference(), field.reference)
                JsonPropertyParser.writeList(context, builder, key, field.fallbackValue, transform)
            }
            else -> Unit
        }
    }

    @JvmStatic
    fun <V : Any> writeListField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<List<V>>?,
        serializer: Lazy<Serializer<JsonObject, V>>,
    ) {
        when (field) {
            is Field.Value -> JsonPropertyParser.writeList(context, builder, key, field.value, serializer)
            is Field.Reference -> {
                builder.put(key.reference(), field.reference)
                JsonPropertyParser.writeList(context, builder, key, field.fallbackValue, serializer)
            }
            else -> Unit
        }
    }

    @JvmStatic
    fun <V : Any> writeExpressionField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<Expression<V>>?,
    ) {
        writeExpressionField(context, builder, key, field, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> writeExpressionField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<Expression<V>>?,
        transform: Transform<V, R>,
    ) {
        when (field) {
            is Field.Value -> JsonExpressionParser.writeExpression(context, builder, key, field.value, transform)
            is Field.Reference -> {
                builder.put(key.reference(), field.reference)
                JsonExpressionParser.writeExpression(context, builder, key, field.fallbackValue, transform)
            }
            else -> Unit
        }
    }

    @JvmStatic
    fun <V : Any> writeExpressionListField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<ExpressionList<V>>?,
    ) {
        writeExpressionListField(context, builder, key, field, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> writeExpressionListField(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        field: Field<ExpressionList<V>>?,
        transform: Transform<V, R>,
    ) {
        when (field) {
            is Field.Value -> JsonExpressionParser.writeExpressionList(context, builder, key, field.value, transform)
            is Field.Reference -> {
                builder.put(key.reference(), field.reference)
                JsonExpressionParser.writeExpressionList(context, builder, key, field.fallbackValue, transform)
            }
            else -> Unit
        }
    }
}
