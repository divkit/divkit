package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.json.JSONSerializable;
import com.yandex.div.json.ParsingEnvironment;
import com.yandex.div.json.ParsingErrorLogger;
import com.yandex.div.json.ParsingException;
import com.yandex.div.json.ParsingExceptionKt;
import com.yandex.div.json.expressions.ConstantExpressionList;
import com.yandex.div.json.expressions.Expression;
import com.yandex.div.json.expressions.ExpressionList;
import com.yandex.div.json.expressions.MutableExpressionList;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yandex.div.internal.parser.TypeHelpersKt.TYPE_HELPER_JSON_ARRAY;
import static com.yandex.div.internal.parser.TypeHelpersKt.TYPE_HELPER_STRING;
import static com.yandex.div.json.ParsingExceptionKt.dependencyFailed;
import static com.yandex.div.json.ParsingExceptionKt.invalidValue;
import static com.yandex.div.json.ParsingExceptionKt.missingValue;
import static com.yandex.div.json.ParsingExceptionKt.typeMismatch;

/**
 * A Java-version of JsonParser.kt that is faster because it generates less garbage during parsing
 * and skips unnecessary checks (like nullability).
 *
 * NOTE! Please do not change Function1 and Function2 with readable java-interfaces.
 * This will only make parsing slower.
 */
public class JsonParser {
    @NonNull
    private static final ValueValidator<?> ALWAYS_VALID = (any) -> true;
    @NonNull
    private static final ValueValidator<String> IS_STRING = (any) -> true;
    @NonNull
    private static final ListValidator<?> ALWAYS_VALID_LIST = (any) -> true;
    @NonNull
    private static final Function1<?,?> AS_IS = (any) -> any;
    private static final ExpressionList<?> EMPTY_EXPRESSION_LIST = new ConstantExpressionList<>(Collections.emptyList());

    @Nullable
    public static <T> Expression<T> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readOptionalExpression(jsonObject, key, doNotConvert(), validator, logger, env, typeHelper);
    }

    @Nullable
    public static <T> Expression<T> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @Nullable final Expression<T> defaultValue,
            @NonNull final TypeHelper<T> typeHelper) {
        return readOptionalExpression(jsonObject, key, doNotConvert(), alwaysValid(), logger, env, defaultValue, typeHelper);
    }

    @Nullable
    public static Expression<String> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<String> typeHelper) {
        return readOptionalExpression(jsonObject, key, doNotConvert(), IS_STRING, logger, env, typeHelper);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readOptionalExpression(jsonObject, key, converter, alwaysValid(), logger, env, typeHelper);
    }

    @NonNull
    public static Expression<JSONArray> readExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readExpression(jsonObject, key, doNotConvert(), logger, env, TYPE_HELPER_JSON_ARRAY);
    }

    @NonNull
    public static Expression<String> readExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<String> typeHelper) {
        return readExpression(jsonObject, key, doNotConvert(), IS_STRING, logger, env, typeHelper);
    }

    @NonNull
    public static <T> Expression<T> readExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readExpression(jsonObject, key, doNotConvert(), validator, logger, env, typeHelper);
    }

    @NonNull
    public static <R, T> Expression<T> readExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readExpression(jsonObject, key, converter, alwaysValid(), logger, env, typeHelper);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readOptionalExpression(jsonObject, key, converter, validator, logger, env, null, typeHelper);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @Nullable final Expression<T> defaultValue,
            @NonNull final TypeHelper<T> typeHelper) {
        return readOptionalExpression(jsonObject, key, converter, alwaysValid(), logger, env, defaultValue, typeHelper);

    }

    @Nullable
    public static <T> Expression<T> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @Nullable final Expression<T> defaultValue,
            @NonNull final TypeHelper<T> typeHelper) {
        return readOptionalExpression(jsonObject, key, doNotConvert(), validator, logger, env, defaultValue, typeHelper);
    }

    @Nullable
    public static <R, T> Expression<T> readOptionalExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @Nullable final Expression<T> defaultValue,
            @NonNull final TypeHelper<T> typeHelper) {
        final Object intermediate = optSafe(jsonObject, key);

        if (intermediate == null) {
            return null;
        }

        if (Expression.mayBeExpression(intermediate)) {
            return new Expression.MutableExpression<>(key, intermediate.toString(), converter, validator, logger,
                                                      typeHelper, defaultValue);
        } else {
            T value;
            try {
                value = converter.invoke((R) intermediate);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, intermediate));
                return null;
            } catch (Exception e) {
                logger.logError(invalidValue(jsonObject, key, intermediate, e));
                return null;
            }

            if (value == null) {
                logger.logError(invalidValue(jsonObject, key, intermediate));
                return null;
            }

            try {
                if (!validator.isValid(value)) {
                    logger.logError(invalidValue(jsonObject, key, intermediate));
                    return null;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, intermediate));
                return null;
            }
            return Expression.constant(value);
        }
    }

    @NonNull
    public static <R, T> Expression<T> readExpression(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        final Object intermediate = optSafe(jsonObject, key);
        if (intermediate == null) {
            throw missingValue(jsonObject, key);
        }

        if (Expression.mayBeExpression(intermediate)) {
            return new Expression.MutableExpression<>(key, intermediate.toString(), converter, validator, logger,
                                                      typeHelper, null);
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
    public static <T> T readOptional(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readOptional(jsonObject, key, doNotConvert(), validator, logger, env);
    }

    @Nullable
    public static <T> T readOptional(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readOptional(jsonObject, key, doNotConvert(), alwaysValid(), logger, env);
    }

    @Nullable
    public static <R, T> T readOptional(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readOptional(jsonObject, key, converter, alwaysValid(), logger, env);
    }

    @Nullable
    public static <T extends JSONSerializable> T readOptional(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        JSONObject json = jsonObject.optJSONObject(key);

        if (json == null) {
            return null;
        }

        try {
            return creator.invoke(env, json);
        } catch (ParsingException e) {
            logger.logError(e);
            return null;
        }
    }

    @Nullable
    public static <R, T> T readOptional(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        final Object intermediate = optSafe(jsonObject, key);
        if (intermediate == null) {
            return null;
        }

        T result = null;
        try {
            result = converter.invoke((R) intermediate);
        } catch (ClassCastException castException) {
            logger.logError(typeMismatch(jsonObject, key, intermediate));
            return null;
        } catch (Exception e) {
            logger.logError(invalidValue(jsonObject, key, intermediate, e));
            return null;
        }

        if (result == null) {
            logger.logError(ParsingExceptionKt.invalidValue(jsonObject, key, intermediate));
            return null;
        }

        try {
            if (!validator.isValid(result)) {
                logger.logError(invalidValue(jsonObject, key, intermediate));
                return null;
            }
        } catch (ClassCastException castException) {
            logger.logError(typeMismatch(jsonObject, key, intermediate));
            return null;
        }

        return result;
    }

    @Nullable
    public static <T> T readOptional(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> converter,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {

        final JSONObject intermediate = jsonObject.optJSONObject(key);
        if (intermediate == null) {
            return null;
        }

        T result;
        try {
            result = converter.invoke(env, intermediate);
        } catch (ClassCastException castException) {
            logger.logError(typeMismatch(jsonObject, key, intermediate));
            return null;
        } catch (Exception e) {
            logger.logError(invalidValue(jsonObject, key, intermediate, e));
            return null;
        }

        if (result == null) {
            logger.logError(invalidValue(jsonObject, key, intermediate));
            return null;
        }

        try {
            if (!validator.isValid(result)) {
                logger.logError(invalidValue(jsonObject, key, intermediate));
                return null;
            }
        } catch (ClassCastException castException) {
            logger.logError(typeMismatch(jsonObject, key, intermediate));
            return null;
        }

        return result;
    }

    @NonNull
    public static <T> T read(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return read(jsonObject, key, doNotConvert(), validator, logger, env);
    }

    @NonNull
    public static <T> T read(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return read(jsonObject, key, doNotConvert(), alwaysValid(), logger, env);
    }

    @NonNull
    public static <R, T> T read(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return read(jsonObject, key, converter, alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> T read(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return read(jsonObject, key, creator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> T read(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        JSONObject json = jsonObject.optJSONObject(key);

        if (json == null) {
            throw missingValue(jsonObject, key);
        }

        T result;
        try {
            result = creator.invoke(env, json);
        } catch (ParsingException e) {
            throw dependencyFailed(jsonObject, key, e);
        }

        if (result == null) {
            throw invalidValue(jsonObject, key, null);
        }

        try {
            if (!validator.isValid(result)) {
                throw invalidValue(jsonObject, key, result);
            }
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, result);
        }

        return result;
    }

    @NonNull
    public static <R, T> T read(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        final Object intermediate = optSafe(jsonObject, key);
        if (intermediate == null) {
            throw missingValue(jsonObject, key);
        }

        T result;
        try {
            result = converter.invoke((R) intermediate);
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, intermediate);
        } catch (Exception e) {
            throw invalidValue(jsonObject, key, intermediate, e);
        }

        if (result == null) {
            throw invalidValue(jsonObject, key, intermediate);
        }

        try {
            if (!validator.isValid(result)) {
                throw invalidValue(jsonObject, key, result);
            }
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, result);
        }

        return result;
    }

    @Nullable
    public static <R, T> List<T> readOptionalList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, R, T> creator,
            @NonNull final ListValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readOptionalList(jsonObject, key, creator, validator, alwaysValid(), logger, env);
    }

    @Nullable
    public static <R, T> List<T> readOptionalList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readOptionalList(jsonObject, key, converter, validator, alwaysValid(), logger, env);
    }

    @Nullable
    public static <R, T> List<T> readOptionalList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        JSONArray optJSONArray = jsonObject.optJSONArray(key);
        if (optJSONArray == null) {
            return null;
        }

        int length = optJSONArray.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!validator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return null;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return null;
            }
            return emptyList;
        }
        List<T> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            Object preResult = optJSONArray.opt(i);
            final Object intermediate = Intrinsics.areEqual(preResult, JSONObject.NULL) ? null : preResult;

            if (intermediate == null) {
                continue;
            }

            T item;
            try {
                item = converter.invoke((R) intermediate);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, intermediate));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(optJSONArray, key, i, intermediate, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            try {
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(optJSONArray, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, item));
                continue;
            }

            list.add(item);
        }

        try {
            if (!validator.isValid(list)) {
                logger.logError(invalidValue(jsonObject, key, list));
                return null;
            }
        } catch (ClassCastException castException) {
            logger.logError(typeMismatch(jsonObject, key, list));
            return null;
        }

        return list;
    }

    @Nullable
    public static <R, T> List<T> readOptionalList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, R, T> creator,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        JSONArray optJSONArray = jsonObject.optJSONArray(key);
        if (optJSONArray == null) {
            return null;
        }

        int length = optJSONArray.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!validator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return null;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return null;
            }
            return emptyList;
        }
        List<T> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Object intermediate = optSafe(optJSONArray.optJSONObject(i));

            if (intermediate == null) {
                continue;
            }

            T item;
            try {
                item = creator.invoke(env, (R) intermediate);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, intermediate));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(optJSONArray, key, i, intermediate, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            try {
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(optJSONArray, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, item));
                continue;
            }

            list.add(item);
        }

        try {
            if (!validator.isValid(list)) {
                logger.logError(invalidValue(jsonObject, key, list));
                return null;
            }
        } catch (ClassCastException castException) {
            logger.logError(typeMismatch(jsonObject, key, list));
            return null;
        }

        return list;
    }

    @NonNull
    public static <T> List<T> readList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull final ListValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readList(jsonObject, key, creator, validator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> List<T> readList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        JSONArray optJSONArray = jsonObject.optJSONArray(key);
        if (optJSONArray == null) {
            throw missingValue(jsonObject, key);
        }

        int length = optJSONArray.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!validator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return emptyList;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return emptyList;
            }
            return emptyList;
        }
        List<T> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final JSONObject intermediate = optSafe(optJSONArray.optJSONObject(i));

            if (intermediate == null) {
                continue;
            }

            T item;
            try {
                item = creator.invoke(env, intermediate);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, intermediate));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(optJSONArray, key, i, intermediate, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            try {
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(optJSONArray, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, item));
                continue;
            }

            list.add(item);
        }

        try {
            if (!validator.isValid(list)) {
                throw invalidValue(jsonObject, key, list);
            }
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, list);
        }

        return list;
    }

    @NonNull
    public static List<String> readList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<String> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readList(jsonObject, key, doNotConvert(), validator, IS_STRING, logger, env);
    }

    @NonNull
    public static <R, T> List<T> readList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readList(jsonObject, key, converter, validator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <R, T> List<T> readList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        JSONArray optJSONArray = jsonObject.optJSONArray(key);
        if (optJSONArray == null) {
            throw missingValue(jsonObject, key);
        }

        int length = optJSONArray.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!validator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return emptyList;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return emptyList;
            }
            return emptyList;
        }
        List<T> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Object intermediate = optSafe(optJSONArray.opt(i));

            if (intermediate == null) {
                continue;
            }

            T item;
            try {
                item = converter.invoke((R) intermediate);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, intermediate));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(optJSONArray, key, i, intermediate, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            try {
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(optJSONArray, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(optJSONArray, key, i, item));
                continue;
            }

            list.add(item);
        }

        try {
            if (!validator.isValid(list)) {
                throw invalidValue(jsonObject, key, list);
            }
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, list);
        }

        return list;
    }

    @NonNull
    public static ExpressionList<String> readExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<String> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<String> typeHelper) {
        return readExpressionList(jsonObject, key, doNotConvert(), validator, IS_STRING, logger, env, typeHelper);
    }

    @NonNull
    public static ExpressionList<String> readExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<String> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env) {
        return readExpressionList(jsonObject, key, doNotConvert(), validator, IS_STRING, logger, env, TYPE_HELPER_STRING);
    }

    @NonNull
    public static <R, T> ExpressionList<T> readExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readExpressionList(jsonObject, key, converter, validator, alwaysValid(), logger, env, typeHelper);
    }

    @NonNull
    public static <T> ExpressionList<T> readExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readExpressionList(jsonObject, key, doNotConvert(), validator, itemValidator, logger, env, typeHelper);
    }

    @Nullable
    public static <T> ExpressionList<T> readOptionalExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readOptionalExpressionList(jsonObject, key, doNotConvert(), validator,
                itemValidator, logger, env, typeHelper);
    }

    @Nullable
    public static <R, T> ExpressionList<T> readOptionalExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readExpressionList(
                jsonObject, key, converter, validator, itemValidator, logger, env, typeHelper, ErrorHandler.IGNORE);
    }

    @NonNull
    public static <R, T> ExpressionList<T> readExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        ExpressionList<T> result = readExpressionList(
                jsonObject, key, converter, validator, itemValidator, logger, env, typeHelper, ErrorHandler.FAIL_FAST);
        if (result == null) {
            throw invalidValue(key, jsonObject);
        }

        return result;
    }

    @Nullable
    private static <R, T> ExpressionList readExpressionList(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper,
            @NonNull final ErrorHandler errorHandler) {
        JSONArray optJSONArray = jsonObject.optJSONArray(key);
        if (optJSONArray == null) {
            errorHandler.process(missingValue(jsonObject, key));
            return null;
        }

        int length = optJSONArray.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!validator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return EMPTY_EXPRESSION_LIST;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return EMPTY_EXPRESSION_LIST;
            }
            return EMPTY_EXPRESSION_LIST;
        }

        // This list may have content of mixed types (raw values and expressions) intentionally.
        // At the end of this method we'll transform it's elements to single type.
        List untypedList = new ArrayList(length);
        boolean containsExpressions = false;

        for (int i = 0; i < length; i++) {
            final Object intermediate = optSafe(optJSONArray.opt(i));

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
                    logger.logError(typeMismatch(optJSONArray, key, i, intermediate));
                    continue;
                } catch (Exception e) {
                    logger.logError(invalidValue(optJSONArray, key, i, intermediate, e));
                    continue;
                }

                if (item == null) {
                    continue;
                }

                try {
                    if (!itemValidator.isValid(item)) {
                        logger.logError(invalidValue(optJSONArray, key, i, item));
                        continue;
                    }
                } catch (ClassCastException castException) {
                    logger.logError(typeMismatch(optJSONArray, key, i, item));
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
            return new MutableExpressionList<T>(key, list, validator, env.getLogger());
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
    public static <T> List<T> readStrictList(
            @NonNull JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull ListValidator<T> listValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readStrictList(jsonObject, key, creator, listValidator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> List<T> readStrictList(
            @NonNull JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull ListValidator<T> listValidator,
            @NonNull ValueValidator<T> itemValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        JSONArray optJSONArray = jsonObject.optJSONArray(key);
        if (optJSONArray == null) {
            throw missingValue(jsonObject, key);
        }

        int length = optJSONArray.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!listValidator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return emptyList;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return emptyList;
            }
            return emptyList;
        }
        List<T> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            JSONObject intermediate = optSafe(optJSONArray.optJSONObject(i));

            if (intermediate == null) {
                throw missingValue(optJSONArray, key, i);
            }

            T item;
            try {
                item = creator.invoke(env, intermediate);
            } catch (ClassCastException castException) {
                throw typeMismatch(optJSONArray, key, i, intermediate);
            } catch (Exception e) {
                throw invalidValue(optJSONArray, key, i, intermediate, e);
            }

            if (item == null) {
                throw invalidValue(optJSONArray, key, i, intermediate);
            }

            try {
                if (!itemValidator.isValid(item)) {
                    throw invalidValue(optJSONArray, key, i, intermediate);
                }
            } catch (ClassCastException castException) {
                throw typeMismatch(optJSONArray, key, i, item);
            }

            list.add(item);
        }


        try {
            if (!listValidator.isValid(list)) {
                throw invalidValue(jsonObject, key, list);
            }
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, list);
        }

        return list;
    }

    @NonNull
    public static <R, T> List<T> readStrictList(
            @NonNull JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator,
            @NonNull ParsingErrorLogger logger) {
        JSONArray optJSONArray = jsonObject.optJSONArray(key);
        if (optJSONArray == null) {
            throw missingValue(jsonObject, key);
        }

        int length = optJSONArray.length();
        if (length == 0) {
            List<T> emptyList = Collections.emptyList();
            try {
                if (!listValidator.isValid(emptyList)) {
                    logger.logError(invalidValue(jsonObject, key, emptyList));
                    return emptyList;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(jsonObject, key, emptyList));
                return emptyList;
            }
            return emptyList;
        }
        List<T> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            JSONObject intermediate = optSafe(optJSONArray.optJSONObject(i));

            if (intermediate == null) {
                throw missingValue(optJSONArray, key, i);
            }

            T item;
            try {
                item = converter.invoke((R) intermediate);
            } catch (ClassCastException castException) {
                throw typeMismatch(optJSONArray, key, i, intermediate);
            } catch (Exception e) {
                throw invalidValue(optJSONArray, key, i, intermediate, e);
            }

            if (item == null) {
                throw invalidValue(optJSONArray, key, i, intermediate);
            }

            try {
                if (!itemValidator.isValid(item)) {
                    throw invalidValue(optJSONArray, key, i, item);
                }
            } catch (ClassCastException castException) {
                throw typeMismatch(optJSONArray, key, i, item);
            }

            list.add(item);
        }


        try {
            if (!listValidator.isValid(list)) {
                throw invalidValue(jsonObject, key, list);
            }
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, list);
        }

        return list;
    }

    @NonNull
    static <T> ValueValidator<T> alwaysValid() {
        //noinspection unchecked
        return (ValueValidator<T>) ALWAYS_VALID;
    }

    @NonNull
    static <T> ListValidator<T> alwaysValidList() {
        //noinspection unchecked
        return (ListValidator<T>) ALWAYS_VALID_LIST;
    }

    @NonNull
    static <T> Function1<T, T> doNotConvert() {
        //noinspection unchecked
        return (Function1<T, T>) AS_IS;
    }

    @Nullable
    private static <T> T optSafe(@Nullable T json) {
        if (json == null || json == JSONObject.NULL) {
            return null;
        }

        return json;
    }

    @Nullable
    private static Object optSafe(JSONObject json, String key) {
        Object value = json.opt(key);
        if (value == null || value == JSONObject.NULL) {
            return null;
        }

        return value;
    }

    private interface ErrorHandler {
        void process(ParsingException e);

        ErrorHandler FAIL_FAST = (e) -> {
            throw e;
        };

        ErrorHandler IGNORE = (e) -> { /*Do nothing*/ };
    }
}
