@file:Suppress("unused")

package com.yandex.div.parser.json

import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.getRawExpressions
import com.yandex.div.json.expressions.mutableExpressionList
import com.yandex.div.parser.json.util.addValue
import com.yandex.div.parser.json.util.putValue
import com.yandex.div.serialization.ParsingContext

internal object JsonExpressionParser {

    @JvmStatic
    fun <V : Any> readExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
    ): Expression<V> {
        return readExpression(context, jsonObject, key, typeHelper, doNotConvert(), alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): Expression<V> {
        return readExpression(context, jsonObject, key, typeHelper, transform, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        validator: ValueValidator<V>,
    ): Expression<V> {
        return readExpression(context, jsonObject, key, typeHelper, doNotConvert(), validator)
    }

    @JvmStatic
    fun <R, V : Any> readExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Expression<V> {
        val intermediate = jsonObject.opt(key) ?: throw missingValue(jsonObject, key)
        if (Expression.mayBeExpression(intermediate)) {
            return Expression.MutableExpression(
                key, intermediate.toString(), transform, validator, context.logger, typeHelper, null)
        }

        val value = try {
            transform(intermediate.cast())
        } catch (e: ClassCastException) {
            throw typeMismatch(jsonObject, key, intermediate)
        } catch (e: Exception) {
            throw invalidValue(jsonObject, key, intermediate, e)
        } ?: throw invalidValue(jsonObject, key, intermediate)

        if (!typeHelper.isTypeValid(value)) {
            throw typeMismatch(jsonObject, key, intermediate)
        }
        try {
            if (!validator.isValid(value)) {
                throw invalidValue(jsonObject, key, intermediate)
            }
        } catch (e: ClassCastException) {
            throw typeMismatch(jsonObject, key, intermediate)
        }
        return Expression.constant(value, context.logger)
    }

    @JvmStatic
    fun <V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
    ): Expression<V>? {
        return readOptionalExpression(context, jsonObject, key, typeHelper, doNotConvert(), alwaysValid(), null)
    }

    @JvmStatic
    fun <V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        return readOptionalExpression(
            context, jsonObject, key, typeHelper, doNotConvert(), alwaysValid(), defaultValue)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): Expression<V>? {
        return readOptionalExpression(context, jsonObject, key, typeHelper, transform, alwaysValid(), null)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        return readOptionalExpression(context, jsonObject, key, typeHelper, transform, alwaysValid(), defaultValue)
    }

    @JvmStatic
    fun <V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        validator: ValueValidator<V>,
    ): Expression<V>? {
        return readOptionalExpression(context, jsonObject, key, typeHelper, doNotConvert(), validator, null)
    }

    @JvmStatic
    fun <V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        validator: ValueValidator<V>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        return readOptionalExpression(context, jsonObject, key, typeHelper, doNotConvert(), validator, defaultValue)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
    ): Expression<V>? {
        return readOptionalExpression(context, jsonObject, key, typeHelper, transform, validator, null)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpression(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        validator: ValueValidator<V>,
        defaultValue: Expression<V>?,
    ): Expression<V>? {
        val intermediate = jsonObject.opt(key) ?: return null
        if (Expression.mayBeExpression(intermediate)) {
            return Expression.MutableExpression(
                key, intermediate.toString(), transform, validator, context.logger, typeHelper, defaultValue)
        }

        val value = try {
            transform(intermediate.cast())
        } catch (e: ClassCastException) {
            context.logger.logError(typeMismatch(jsonObject, key, intermediate))
            return null
        } catch (e: Exception) {
            context.logger.logError(invalidValue(jsonObject, key, intermediate, e))
            return null
        }
        if (value == null) {
            context.logger.logError(invalidValue(jsonObject, key, intermediate))
            return null
        }
        if (!typeHelper.isTypeValid(value)) {
            context.logger.logError(typeMismatch(jsonObject, key, intermediate))
            return null
        }
        try {
            if (!validator.isValid(value)) {
                context.logger.logError(invalidValue(jsonObject, key, intermediate))
                return null
            }
        } catch (e: ClassCastException) {
            context.logger.logError(typeMismatch(jsonObject, key, intermediate))
            return null
        }
        return Expression.constant(value, context.logger)
    }

    @JvmStatic
    fun readExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        listValidator: ListValidator<String>,
    ): ExpressionList<String> {
        return readExpressionList(
            context, jsonObject, key, TYPE_HELPER_STRING, doNotConvert<String>(), listValidator, alwaysValidString())
    }

    @JvmStatic
    fun <R, V : Any> readExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): ExpressionList<V> {
        return readExpressionList(context, jsonObject, key, typeHelper, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun readExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<String>,
        listValidator: ListValidator<String>,
    ): ExpressionList<String> {
        return readExpressionList(
            context, jsonObject, key, typeHelper, doNotConvert<String>(), listValidator, alwaysValidString())
    }

    @JvmStatic
    fun <R, V : Any> readExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): ExpressionList<V> {
        return readExpressionList(context, jsonObject, key, typeHelper, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V> {
        return readExpressionList(context, jsonObject, key, typeHelper, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> readExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V> {
        val array = jsonObject.optArray(key) ?: throw missingValue(jsonObject, key)
        val length = array.length()
        if (length == 0) {
            validateList(context, jsonObject, key, emptyList(), listValidator)
            return emptyExpressionList()
        }
        return readExpressionItems(context, jsonObject, array, key, length, typeHelper, transform, listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
    ): ExpressionList<V>? {
        return readOptionalExpressionList(
            context, jsonObject, key, typeHelper, transform, alwaysValidList(), alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptionalExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        listValidator: ListValidator<V>,
    ): ExpressionList<V>? {
        return readOptionalExpressionList(
            context, jsonObject, key, typeHelper, doNotConvert(), listValidator, alwaysValid())
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
    ): ExpressionList<V>? {
        return readOptionalExpressionList(context, jsonObject, key, typeHelper, transform, listValidator, alwaysValid())
    }

    @JvmStatic
    fun <V : Any> readOptionalExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V>? {
        return readOptionalExpressionList(
            context, jsonObject, key, typeHelper, doNotConvert(), listValidator, itemValidator)
    }

    @JvmStatic
    fun <R, V : Any> readOptionalExpressionList(
        context: ParsingContext,
        jsonObject: JsonObject,
        key: String,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V>? {
        val array = jsonObject.optArray(key) ?: return null
        val length = array.length()
        if (length == 0) {
            validateList(context, jsonObject, key, emptyList(), listValidator)
            return emptyExpressionList()
        }
        return try {
            readExpressionItems(context, jsonObject, array, key, length, typeHelper, transform, listValidator, itemValidator)
        } catch (e: ParsingException) {
            context.logger.logError(e)
            null
        }
    }

    /**
     * Reads non-empty [array]: a mutable list if any item is an expression, a constant one otherwise.
     * Failed items are logged and skipped. Throws if [listValidator] rejects the constant list.
     */
    private fun <R, V : Any> readExpressionItems(
        context: ParsingContext,
        jsonObject: JsonObject,
        array: JsonArray,
        key: String,
        length: Int,
        typeHelper: TypeHelper<V>,
        transform: Transform<R, V?>,
        listValidator: ListValidator<V>,
        itemValidator: ValueValidator<V>,
    ): ExpressionList<V> {
        val logger = context.logger
        // Holds both raw values and expressions; converted to a single type below.
        val items = ArrayList<Any>(length)
        var containsExpressions = false
        for (i in 0 until length) {
            val intermediate = array.opt(i) ?: continue
            if (Expression.mayBeExpression(intermediate)) {
                containsExpressions = true
                items.add(Expression.MutableExpression(
                    "$key[$i]", intermediate.toString(), transform, itemValidator, logger, typeHelper, null))
                continue
            }

            val item = try {
                transform(intermediate.cast())
            } catch (e: ClassCastException) {
                logger.logError(typeMismatch(array, key, i, intermediate))
                continue
            } catch (e: Exception) {
                logger.logError(invalidValue(array, key, i, intermediate, e))
                continue
            } ?: continue

            if (!typeHelper.isTypeValid(item)) {
                logger.logError(typeMismatch(array, key, i, intermediate))
                continue
            }
            try {
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(array, key, i, item))
                    continue
                }
            } catch (e: ClassCastException) {
                logger.logError(typeMismatch(array, key, i, item))
                continue
            }
            items.add(item)
        }

        if (containsExpressions) {
            for (i in 0 until items.size) {
                val item = items[i]
                if (item !is Expression<*>) {
                    items[i] = Expression.constant(item, logger)
                }
            }
            return mutableExpressionList(key, items.cast<List<Expression<V>>>(), listValidator, logger)
        }

        val list: List<V> = items.cast()
        try {
            if (!listValidator.isValid(list)) {
                throw invalidValue(jsonObject, key, list)
            }
        } catch (e: ClassCastException) {
            throw typeMismatch(jsonObject, key, list)
        }
        return ConstantExpressionList(list)
    }


    @JvmStatic
    fun <V : Any> writeExpression(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        expression: Expression<V>?,
    ) {
        writeExpression(context, builder, key, expression, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> writeExpression(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        expression: Expression<V>?,
        transform: Transform<V, R>,
    ) {
        if (expression == null) return
        try {
            if (expression is Expression.MutableExpression<*, *>) {
                builder.put(key, expression.rawValue)
            } else {
                builder.putValue(key, transform(expression.rawValue.cast()))
            }
        } catch (e: RuntimeException) {
            context.logger.logError(e)
        }
    }

    @JvmStatic
    fun <V : Any> writeExpressionList(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        expressionList: ExpressionList<V>?,
    ) {
        writeExpressionList(context, builder, key, expressionList, doNotConvert())
    }

    @JvmStatic
    fun <R, V : Any> writeExpressionList(
        context: ParsingContext,
        builder: JsonObject.Builder,
        key: String,
        expressionList: ExpressionList<V>?,
        transform: Transform<V, R>,
    ) {
        if (expressionList == null) return
        val array: JsonArray
        if (expressionList is ConstantExpressionList) {
            val values = expressionList.evaluate(ExpressionResolver.EMPTY)
            val arrayBuilder = JsonArray.builder()
            for (i in 0 until values.size) {
                arrayBuilder.addValue(transform(values[i]))
            }
            array = arrayBuilder.build()
        } else {
            val expressions = expressionList.getRawExpressions()
            if (expressions.isEmpty()) return
            val arrayBuilder = JsonArray.builder()
            for (i in 0 until expressions.size) {
                val expression = expressions[i]
                if (expression is Expression.ConstantExpression) {
                    arrayBuilder.addValue(transform(expression.evaluate(ExpressionResolver.EMPTY)))
                } else {
                    arrayBuilder.addValue(expression.rawValue)
                }
            }
            array = arrayBuilder.build()
        }
        try {
            builder.put(key, array)
        } catch (e: RuntimeException) {
            context.logger.logError(e)
        }
    }

    private val EMPTY_EXPRESSION_LIST = ConstantExpressionList(emptyList())

    private fun <V : Any> emptyExpressionList(): ExpressionList<V> = EMPTY_EXPRESSION_LIST.cast()
}
