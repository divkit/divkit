@file:Suppress("unused")

package com.yandex.div.parser.json

import com.yandex.div.core.json.JsonObject
import com.yandex.div.data.EntityTemplate
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.internal.template.Field
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.serialization.Deserializer
import com.yandex.div.serialization.ParsingContext
import com.yandex.div.serialization.TemplateResolver

internal object JsonFieldResolver {

    @JvmStatic
    fun <V : Any> resolve(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
    ): V {
        return resolve(context, field, data, key, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolve(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
    ): V {
        return resolve(context, field, data, key, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolve(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
        validator: ValueValidator<V>,
    ): V {
        return resolve(context, field, data, key, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> resolve(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): V {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.read(context, data, key, transform, validator)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonPropertyParser.read(context, data, field.reference, transform, validator)
                }
            }
            else -> throw missingValue(data, key)
        }
    }

    @JvmStatic
    fun <T : EntityTemplate<V>, V : Any> resolve(
        context: ParsingContext,
        field: Field<T>,
        data: JsonObject,
        key: String,
        resolver: Lazy<TemplateResolver<JsonObject, T, V>>,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): V {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.read(context, data, key, deserializer)
        }
        return when (field) {
            is Field.Value -> resolveDependency(context, field.value, data, key, resolver.value)
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    resolveDependency(context, fallbackValue, data, key, resolver.value)
                } else {
                    JsonPropertyParser.read(context, data, field.reference, deserializer)
                }
            }
            else -> throw missingValue(data, key)
        }
    }

    @JvmStatic
    fun <V : Any> resolveOptional(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
    ): V? {
        return resolveOptional(context, field, data, key, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveOptional(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
    ): V? {
        return resolveOptional(context, field, data, key, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveOptional(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
        validator: ValueValidator<V>,
    ): V? {
        return resolveOptional(context, field, data, key, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> resolveOptional(
        context: ParsingContext,
        field: Field<V>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): V? {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptional(context, data, key, transform, validator)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonPropertyParser.readOptional(context, data, field.reference, transform, validator)
                }
            }
            else -> null
        }
    }

    @JvmStatic
    fun <T : EntityTemplate<V>, V : Any> resolveOptional(
        context: ParsingContext,
        field: Field<T>,
        data: JsonObject,
        key: String,
        resolver: Lazy<TemplateResolver<JsonObject, T, V>>,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): V? {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptional(context, data, key, deserializer)
        }
        return when (field) {
            is Field.Value -> resolveOptionalDependency(context, field.value, data, resolver.value)
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    resolveOptionalDependency(context, fallbackValue, data, resolver.value)
                } else {
                    JsonPropertyParser.readOptional(context, data, field.reference, deserializer)
                }
            }
            else -> null
        }
    }

    @JvmStatic
    fun <V : Any> resolveExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
    ): Expression<V> {
        return resolveExpression(context, field, data, key, typeHelper, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): Expression<V> {
        return resolveExpression(context, field, data, key, typeHelper, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        validator: ValueValidator<V>,
    ): Expression<V> {
        return resolveExpression(context, field, data, key, typeHelper, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> resolveExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Expression<V> {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, data, key, typeHelper, transform, validator)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonExpressionParser.readExpression(context, data, field.reference, typeHelper, transform, validator)
                }
            }
            else -> throw missingValue(data, key)
        }
    }

    @JvmStatic
    fun <V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
    ): Expression<V>? {
        return resolveOptionalExpression(
            context, field, data, key, typeHelper, doNotConvert(), alwaysValid(), null)
    }

    @JvmStatic
    fun <V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        return resolveOptionalExpression(
            context, field, data, key, typeHelper, doNotConvert(), alwaysValid(), defaultValue)
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): Expression<V>? {
        return resolveOptionalExpression(
            context, field, data, key, typeHelper, transform, alwaysValid(), null)
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        return resolveOptionalExpression(
            context, field, data, key, typeHelper, transform, alwaysValid(), defaultValue)
    }

    @JvmStatic
    fun <V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        validator: ValueValidator<V>,
    ): Expression<V>? {
        return resolveOptionalExpression(
            context, field, data, key, typeHelper, doNotConvert(), validator, null)
    }

    @JvmStatic
    fun <V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        validator: ValueValidator<V>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        return resolveOptionalExpression(
            context, field, data, key, typeHelper, doNotConvert(), validator, defaultValue)
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Expression<V>? {
        return resolveOptionalExpression(
            context, field, data, key, typeHelper, transform, validator, null)
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalExpression(
        context: ParsingContext,
        field: Field<Expression<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                context, data, key, typeHelper, transform, validator, defaultValue)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonExpressionParser.readOptionalExpression(
                        context, data, field.reference, typeHelper, transform, validator, defaultValue)
                }
            }
            else -> null
        }
    }

    @JvmStatic
    fun <R, V : Any> resolveList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
    ): List<V> {
        return resolveList(context, field, data, key, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): List<V> {
        return resolveList(context, field, data, key, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        listValidator: ListValidator<V>,
    ): List<V> {
        return resolveList(context, field, data, key, doNotConvert(), listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): List<V> {
        return resolveList(context, field, data, key, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> resolveList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): List<V> {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readList(context, data, key, transform, listValidator, itemValidator)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonPropertyParser.readList(context, data, field.reference, transform, listValidator, itemValidator)
                }
            }
            else -> throw missingValue(data, key)
        }
    }

    @JvmStatic
    fun <T : EntityTemplate<V>, V : Any> resolveList(
        context: ParsingContext,
        field: Field<List<T>>,
        data: JsonObject,
        key: String,
        resolver: Lazy<TemplateResolver<JsonObject, T, V>>,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): List<V> {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readList(context, data, key, deserializer)
        }
        return when (field) {
            is Field.Value -> resolveTemplates(context, field.value, data, resolver.value)
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    resolveTemplates(context, fallbackValue, data, resolver.value)
                } else {
                    JsonPropertyParser.readList(context, data, field.reference, deserializer)
                }
            }
            else -> throw missingValue(data, key)
        }
    }

    @JvmStatic
    fun <T : EntityTemplate<V>, V : Any> resolveList(
        context: ParsingContext,
        field: Field<List<T>>,
        data: JsonObject,
        key: String,
        resolver: Lazy<TemplateResolver<JsonObject, T, V>>,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
        listValidator: ListValidator<V>,
    ): List<V> {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readList(context, data, key, deserializer, listValidator)
        }
        val templates = when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue == null || data.has(field.reference)) {
                    return JsonPropertyParser.readList(context, data, field.reference, deserializer, listValidator)
                }
                fallbackValue
            }
            else -> throw missingValue(data, key)
        }
        val list = resolveTemplates(context, templates, data, resolver.value)
        if (!listValidator.isValid(list)) {
            throw invalidValue(data, key, list)
        }
        return list
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
    ): List<V>? {
        return resolveOptionalList(context, field, data, key, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): List<V>? {
        return resolveOptionalList(context, field, data, key, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveOptionalList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        listValidator: ListValidator<V>,
    ): List<V>? {
        return resolveOptionalList(context, field, data, key, doNotConvert(), listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveOptionalList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): List<V>? {
        return resolveOptionalList(context, field, data, key, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalList(
        context: ParsingContext,
        field: Field<List<V>>,
        data: JsonObject,
        key: String,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): List<V>? {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptionalList(context, data, key, transform, listValidator, itemValidator)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonPropertyParser.readOptionalList(context, data, field.reference, transform, listValidator, itemValidator)
                }
            }
            else -> null
        }
    }

    @JvmStatic
    fun <T : EntityTemplate<V>, V : Any> resolveOptionalList(
        context: ParsingContext,
        field: Field<List<T>>,
        data: JsonObject,
        key: String,
        resolver: Lazy<TemplateResolver<JsonObject, T, V>>,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
    ): List<V>? {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptionalList(context, data, key, deserializer)
        }
        return when (field) {
            is Field.Value -> resolveTemplates(context, field.value, data, resolver.value)
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    resolveTemplates(context, fallbackValue, data, resolver.value)
                } else {
                    JsonPropertyParser.readOptionalList(context, data, field.reference, deserializer)
                }
            }
            else -> null
        }
    }

    @JvmStatic
    fun <T : EntityTemplate<V>, V : Any> resolveOptionalList(
        context: ParsingContext,
        field: Field<List<T>>,
        data: JsonObject,
        key: String,
        resolver: Lazy<TemplateResolver<JsonObject, T, V>>,
        deserializer: Lazy<Deserializer<JsonObject, V>>,
        listValidator: ListValidator<V>,
    ): List<V>? {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptionalList(context, data, key, deserializer, listValidator)
        }
        val templates = when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue == null || data.has(field.reference)) {
                    return JsonPropertyParser.readOptionalList(context, data, field.reference, deserializer, listValidator)
                }
                fallbackValue
            }
            else -> return null
        }
        val list = resolveTemplates(context, templates, data, resolver.value)
        if (!listValidator.isValid(list)) {
            context.logger.logError(invalidValue(data, key, list))
            return null
        }
        return list
    }

    @JvmStatic
    fun <V : Any> resolveExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
    ): ExpressionList<V> {
        return resolveExpressionList(context, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): ExpressionList<V> {
        return resolveExpressionList(context, field, data, key, typeHelper, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): ExpressionList<V> {
        return resolveExpressionList(context, field, data, key, typeHelper, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        listValidator: ListValidator<V>,
    ): ExpressionList<V> {
        return resolveExpressionList(context, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V> {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpressionList(
                context, data, key, typeHelper, transform, listValidator, itemValidator)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonExpressionParser.readExpressionList(
                        context, data, field.reference, typeHelper, transform, listValidator, itemValidator)
                }
            }
            else -> throw missingValue(data, key)
        }
    }

    @JvmStatic
    fun <V : Any> resolveOptionalExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
    ): ExpressionList<V>? {
        return resolveOptionalExpressionList(context, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): ExpressionList<V>? {
        return resolveOptionalExpressionList(context, field, data, key, typeHelper, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): ExpressionList<V>? {
        return resolveOptionalExpressionList(context, field, data, key, typeHelper, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveOptionalExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        listValidator: ListValidator<V>,
    ): ExpressionList<V>? {
        return resolveOptionalExpressionList(context, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> resolveOptionalExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V>? {
        return resolveOptionalExpressionList(context, field, data, key, typeHelper, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> resolveOptionalExpressionList(
        context: ParsingContext,
        field: Field<ExpressionList<V>>,
        data: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V>? {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpressionList(
                context, data, key, typeHelper, transform, listValidator, itemValidator)
        }
        return when (field) {
            is Field.Value -> field.value
            is Field.Reference -> {
                val fallbackValue = field.fallbackValue
                if (fallbackValue != null && !data.has(field.reference)) {
                    fallbackValue
                } else {
                    JsonExpressionParser.readOptionalExpressionList(
                        context, data, field.reference, typeHelper, transform, listValidator, itemValidator)
                }
            }
            else -> null
        }
    }

    /** Resolves [templates] against [data]; templates that fail to resolve are logged and skipped. */
    private fun <T : EntityTemplate<V>, V : Any> resolveTemplates(
        context: ParsingContext,
        templates: List<T>,
        data: JsonObject,
        resolver: TemplateResolver<JsonObject, T, V>,
    ): MutableList<V> {
        val length = templates.size
        val result = ArrayList<V>(length)
        for (i in 0 until length) {
            val value = resolveOptionalDependency(context, templates[i], data, resolver) ?: continue
            result.add(value)
        }
        return result
    }

    private fun <T : EntityTemplate<V>, V : Any> resolveDependency(
        context: ParsingContext,
        template: T,
        data: JsonObject,
        key: String,
        resolver: TemplateResolver<JsonObject, T, V>,
    ): V {
        return try {
            resolver.resolve(context, template, data)
        } catch (e: ParsingException) {
            throw dependencyFailed(data, key, e)
        }
    }

    private fun <T : EntityTemplate<V>, V : Any> resolveOptionalDependency(
        context: ParsingContext,
        template: T,
        data: JsonObject,
        resolver: TemplateResolver<JsonObject, T, V>,
    ): V? {
        return try {
            resolver.resolve(context, template, data)
        } catch (e: ParsingException) {
            context.logger.logError(e)
            null
        }
    }
}
