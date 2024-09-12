package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.json.ParsingErrorLogger;
import com.yandex.div.json.ParsingException;
import com.yandex.div.json.expressions.ConstantExpressionList;
import com.yandex.div.json.expressions.Expression;
import com.yandex.div.json.expressions.ExpressionList;
import com.yandex.div.json.expressions.MutableExpressionList;
import com.yandex.div.serialization.ParsingContext;
import kotlin.jvm.functions.Function1;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yandex.div.internal.parser.JsonParsers.alwaysValid;
import static com.yandex.div.internal.parser.JsonParsers.alwaysValidList;
import static com.yandex.div.internal.parser.JsonParsers.alwaysValidString;
import static com.yandex.div.internal.parser.JsonParsers.doNotConvert;
import static com.yandex.div.internal.parser.JsonParsers.optSafe;
import static com.yandex.div.internal.parser.TypeHelpersKt.TYPE_HELPER_STRING;
import static com.yandex.div.json.ParsingExceptionKt.invalidValue;
import static com.yandex.div.json.ParsingExceptionKt.missingValue;
import static com.yandex.div.json.ParsingExceptionKt.typeMismatch;

/**
 * Dedicated parser of properties containing expressions.
 *
 * NOTE! Please do not change Function1 and Function2 with readable java-interfaces.
 * This will only make parsing slower.
 */
@SuppressWarnings({"unused", "unchecked"})
public class JsonExpressionParser {

    private static final ExpressionList<?> EMPTY_EXPRESSION_LIST = new ConstantExpressionList<>(Collections.emptyList());

    private JsonExpressionParser() {
        /* do not initialize */
    }

    @NonNull
    public static <T> Expression<T> readExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper
    ) {
        return readExpression(context, logger, jsonObject, key, typeHelper, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, T> Expression<T> readExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter
    ) {
        return readExpression(context, logger, jsonObject, key, typeHelper, converter, alwaysValid());
    }

    @NonNull
    public static <T> Expression<T> readExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final ValueValidator<T> validator
    ) {
        return readExpression(context, logger, jsonObject, key, typeHelper, doNotConvert(), validator);
    }

    @NonNull
    public static <R, T> Expression<T> readExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
        final Object intermediate = optSafe(jsonObject, key);
        if (intermediate == null) {
            throw missingValue(jsonObject, key);
        }

        if (Expression.mayBeExpression(intermediate)) {
            return new Expression.MutableExpression<>(
                    key, intermediate.toString(), converter, validator, logger, typeHelper, null);
        } else {
            T value;
            try {
                value = converter.invoke((R) intermediate);
            } catch (ClassCastException castException) {
                throw typeMismatch(jsonObject, key, intermediate);
            } catch (Exception e) {
                throw invalidValue(jsonObject, key, intermediate, e);
            }
            if (value == null) {
                throw invalidValue(jsonObject, key, intermediate);
            }

            if (!typeHelper.isTypeValid(value)) {
                throw typeMismatch(jsonObject, key, intermediate);
            }

            try {
                if (!validator.isValid(value)) {
                    throw invalidValue(jsonObject, key, intermediate);
                }
            } catch (ClassCastException castException) {
                throw typeMismatch(jsonObject, key, intermediate);
            }

            return Expression.constant(value);
        }
    }

    @Nullable
    public static Expression<String> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<String> typeHelper
    ) {
        return readOptionalExpression(context, logger, jsonObject, key, typeHelper, doNotConvert(), alwaysValidString(), null);
    }

    @Nullable
    public static <T> Expression<T> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @Nullable final Expression<T> defaultValue
    ) {
        return readOptionalExpression(context, logger, jsonObject, key, typeHelper, doNotConvert(), alwaysValid(), defaultValue);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptionalExpression(context, logger, jsonObject, key, typeHelper, converter, alwaysValid(), null);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @Nullable final Expression<T> defaultValue
    ) {
        return readOptionalExpression(context, logger, jsonObject, key, typeHelper, converter, alwaysValid(), defaultValue);

    }

    @Nullable
    public static <T> Expression<T> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final ValueValidator<T> validator
    ) {
        return readOptionalExpression(context, logger, jsonObject, key, typeHelper, doNotConvert(), validator, null);
    }

    @Nullable
    public static <T> Expression<T> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final ValueValidator<T> validator,
            @Nullable final Expression<T> defaultValue
        ) {
        return readOptionalExpression(context, logger, jsonObject, key, typeHelper, doNotConvert(), validator, defaultValue);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
        return readOptionalExpression(context, logger, jsonObject, key, typeHelper, converter, validator, null);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator,
            @Nullable final Expression<T> defaultValue
    ) {
        final Object intermediate = optSafe(jsonObject, key);

        if (intermediate == null
        ) {
            return null;
        }

        if (Expression.mayBeExpression(intermediate)
        ) {
            return new Expression.MutableExpression<>(
                    key, intermediate.toString(), converter, validator, logger, typeHelper, defaultValue);
        } else {
            T value;
            try {
                value = converter.invoke((R) intermediate);
            } catch (ClassCastException castException
            ) {
                logger.logError(typeMismatch(jsonObject, key, intermediate));
                return null;
            } catch (Exception e
            ) {
                logger.logError(invalidValue(jsonObject, key, intermediate, e));
                return null;
            }

            if (value == null
            ) {
                logger.logError(invalidValue(jsonObject, key, intermediate));
                return null;
            }

            if (!typeHelper.isTypeValid(value)
            ) {
                logger.logError(typeMismatch(jsonObject, key, intermediate));
                return null;
            }

            try {
                if (!validator.isValid(value)
                ) {
                    logger.logError(invalidValue(jsonObject, key, intermediate));
                    return null;
                }
            } catch (ClassCastException castException
            ) {
                logger.logError(typeMismatch(jsonObject, key, intermediate));
                return null;
            }
            return Expression.constant(value);
        }
    }

    @NonNull
    public static ExpressionList<String> readExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<String> validator
    ) {
        return readExpressionList(context, logger, jsonObject, key, TYPE_HELPER_STRING, doNotConvert(), validator, alwaysValidString());
    }

    @NonNull
    public static <R, T> ExpressionList<T> readExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter
    ) {
        return readExpressionList(context, logger, jsonObject, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static ExpressionList<String> readExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<String> typeHelper,
            @NonNull final ListValidator<String> validator
    ) {
        return readExpressionList(context, logger, jsonObject, key, typeHelper, doNotConvert(), validator, alwaysValidString());
    }

    @NonNull
    public static <R, T> ExpressionList<T> readExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator
    ) {
        return readExpressionList(context, logger, jsonObject, key, typeHelper, converter, validator, alwaysValid());
    }

    @NonNull
    public static <T> ExpressionList<T> readExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        return readExpressionList(context, logger, jsonObject, key, typeHelper, doNotConvert(), validator, itemValidator);
    }

    @NonNull
    public static <R, T> ExpressionList<T> readExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        ExpressionList<T> result = readOptionalExpressionList(
                context, logger, jsonObject, key, typeHelper, converter, validator, itemValidator, failFastHandler());
        if (result == null) {
            throw invalidValue(key, jsonObject);
        }
        return result;
    }

    @Nullable
    public static <R, T> ExpressionList<T> readOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptionalExpressionList(
                context, logger, jsonObject, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <T> ExpressionList<T> readOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final ListValidator<T> validator
    ) {
        return readOptionalExpressionList(
                context, logger, jsonObject, key, typeHelper, doNotConvert(), validator, alwaysValid());
    }

    @Nullable
    public static <T> ExpressionList<T> readOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        return readOptionalExpressionList(
                context, logger, jsonObject, key, typeHelper, doNotConvert(), validator, itemValidator);
    }

    @Nullable
    public static <R, T> ExpressionList<T> readOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        return readOptionalExpressionList(
                context, logger, jsonObject, key, typeHelper, converter, validator, itemValidator, ignoreErrorHandler());
    }

    @Nullable
    private static <R, T> ExpressionList<T> readOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ErrorHandler errorHandler
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            errorHandler.process(missingValue(jsonObject, key));
            return null;
        }

        int length = array.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!validator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return emptyExpressionList();
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return emptyExpressionList();
            }
            return emptyExpressionList();
        }

        // This list may have content of mixed types (raw values and expressions) intentionally.
        // At the end of this method we'll transform it's elements to single type.
        List untypedList = new ArrayList(length);
        boolean containsExpressions = false;

        for (int i = 0; i < length; i++) {
            final Object intermediate = optSafe(array.opt(i));

            if (intermediate == null) {
                continue;
            }

            if (Expression.mayBeExpression(intermediate)) {
                containsExpressions = true;
                untypedList.add(new Expression.MutableExpression<>(
                        /*expressionKey*/ key + "[" + i + "]",
                        /*rawExpression*/ intermediate.toString(),
                        /*converter*/ converter,
                        /*valueValidator*/ itemValidator,
                        /*logger*/ logger,
                        /*typeHelper*/ typeHelper,
                        /*fieldDefaultValue*/ null));
            } else {
                T item;
                try {
                    item = converter.invoke((R) intermediate);
                } catch (ClassCastException castException) {
                    logger.logError(typeMismatch(array, key, i, intermediate));
                    continue;
                } catch (Exception e) {
                    logger.logError(invalidValue(array, key, i, intermediate, e));
                    continue;
                }

                if (item == null) {
                    continue;
                }

                if (!typeHelper.isTypeValid(item)) {
                    logger.logError(typeMismatch(array, key, i, intermediate));
                    continue;
                }

                try {
                    if (!itemValidator.isValid(item)) {
                        logger.logError(invalidValue(array, key, i, item));
                        continue;
                    }
                } catch (ClassCastException castException) {
                    logger.logError(typeMismatch(array, key, i, item));
                    continue;
                }

                untypedList.add(item);
            }
        }

        if (containsExpressions) {
            for (int i = 0; i < untypedList.size(); i++) {
                Object item = untypedList.get(i);
                if (item instanceof Expression) {
                    continue;
                }

                untypedList.set(i, Expression.constant(item));
            }

            List<Expression<T>> list = (List<Expression<T>>) untypedList;
            return new MutableExpressionList<T>(key, list, validator, logger);
        } else {
            List<T> list = (List<T>) untypedList;
            try {
                if (!validator.isValid(list)) {
                    errorHandler.process(invalidValue(jsonObject, key, list));
                    return null;
                }
            } catch (ClassCastException castException) {
                errorHandler.process(typeMismatch(jsonObject, key, list));
                return null;
            }

            return new ConstantExpressionList<>(list);
        }
    }

    @NonNull
    private static <T> ExpressionList<T> emptyExpressionList() {
        return (ExpressionList<T>) EMPTY_EXPRESSION_LIST;
    }

    @NonNull
    private static ErrorHandler failFastHandler() {
        return ErrorHandler.FAIL_FAST;
    }

    @NonNull
    private static ErrorHandler ignoreErrorHandler() {
        return ErrorHandler.IGNORE;
    }

    private interface ErrorHandler {
        void process(ParsingException e);

        ErrorHandler FAIL_FAST = (e) -> {
            throw e;
        };

        ErrorHandler IGNORE = (e) -> { /* do nothing */ };
    }
}
