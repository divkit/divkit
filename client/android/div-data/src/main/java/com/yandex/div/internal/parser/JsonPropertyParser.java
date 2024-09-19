package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.json.ParsingErrorLogger;
import com.yandex.div.json.ParsingException;
import com.yandex.div.json.ParsingExceptionKt;
import com.yandex.div.serialization.Deserializer;
import com.yandex.div.serialization.ParsingContext;

import kotlin.OptIn;
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
@SuppressWarnings({"unused", "unchecked"})
@OptIn(markerClass = com.yandex.div.core.annotations.ExperimentalApi.class)
public class JsonPropertyParser {

    private JsonPropertyParser() {
        /* do not initialize */
    }

    @NonNull
    public static String readString(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key
    ) {
        Object value = optSafe(jsonObject, key);
        if (value == null) {
            throw missingValue(jsonObject, key);
        }

        String result;
        try {
            result = (String) value;
        } catch (ClassCastException castException) {
            throw typeMismatch(jsonObject, key, value);
        } catch (Exception e) {
            throw invalidValue(jsonObject, key, value, e);
        }

        return result;
    }

    @Nullable
    public static String readOptionalString(
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key
    ) {
        Object value = optSafe(jsonObject, key);
        if (value == null) {
            return null;
        }

        String result = null;
        try {
            result = (String) value;
        } catch (ClassCastException castException) {
            logger.logError(typeMismatch(jsonObject, key, value));
        } catch (Exception e) {
            logger.logError(invalidValue(jsonObject, key, value, e));
        }

        return result;
    }

    @NonNull
    public static <T> T read(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key
    ) {
        return read(context, logger, jsonObject, key, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, T> T read(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter
    ) {
        return read(context, logger, jsonObject, key, converter, alwaysValid());
    }

    @NonNull
    public static <T> T read(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<T> validator
    ) {
        return read(context, logger, jsonObject, key, doNotConvert(), validator);
    }

    @NonNull
    public static <R, T> T read(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
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

    @NonNull
    public static <T> T read(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        JSONObject json = jsonObject.optJSONObject(key);
        if (json == null) {
            throw missingValue(jsonObject, key);
        }

        T result;
        try {
            result = deserializer.deserialize(context, json);
        } catch (ParsingException e) {
            throw dependencyFailed(jsonObject, key, e);
        }

        if (result == null) {
            throw invalidValue(jsonObject, key, null);
        }

        return result;
    }

    @Nullable
    public static <T> T readOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key
    ) {
        return readOptional(context, logger, jsonObject, key, doNotConvert(), alwaysValid());
    }

    @Nullable
    public static <R, T> T readOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptional(context, logger, jsonObject, key, converter, alwaysValid());
    }

    @Nullable
    public static <T> T readOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<T> validator
    ) {
        return readOptional(context, logger, jsonObject, key, doNotConvert(), validator);
    }

    @Nullable
    public static <R, T> T readOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
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
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        JSONObject json = jsonObject.optJSONObject(key);
        if (json == null) {
            return null;
        }

        try {
            return deserializer.deserialize(context, json);
        } catch (ParsingException e) {
            logger.logError(e);
            return null;
        }
    }

    @NonNull
    public static <R, T> List<T> readList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter
    ) {
        return readList(context, logger, jsonObject, key, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static List<String> readList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<String> validator
    ) {
        return readList(context, logger, jsonObject, key, doNotConvert(), validator, alwaysValidString());
    }

    @NonNull
    public static <R, T> List<T> readList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator
    ) {
        return readList(context, logger, jsonObject, key, converter, validator, alwaysValid());
    }

    @NonNull
    public static <R, T> List<T> readList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            throw missingValue(jsonObject, key);
        }

        int length = array.length();
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
            final Object intermediate = optSafe(array.opt(i));

            if (intermediate == null) {
                continue;
            }

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

            try {
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(array, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(array, key, i, item));
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
    public static <T> List<T> readList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            throw missingValue(jsonObject, key);
        }

        int length = array.length();
        if (length == 0) {
            return Collections.emptyList();
        }

        List<T> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject json = optSafe(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            T item;
            try {
                item = deserializer.deserialize(context, json);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(array, key, i, json));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(array, key, i, json, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            list.add(item);
        }

        return list;
    }

    @NonNull
    public static <T> List<T> readList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Deserializer<T, JSONObject> deserializer,
            @NonNull final ListValidator<T> validator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            throw missingValue(jsonObject, key);
        }

        int length = array.length();
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
            final JSONObject json = optSafe(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            T item;
            try {
                item = deserializer.deserialize(context, json);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(array, key, i, json));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(array, key, i, json, e));
                continue;
            }

            if (item == null) {
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

    @Nullable
    public static <R, T> List<T> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptionalList(context, logger, jsonObject, key, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, T> List<T> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator
    ) {
        return readOptionalList(context, logger, jsonObject, key, converter, validator, alwaysValid());
    }

    @Nullable
    public static <R, T> List<T> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> validator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            return null;
        }

        int length = array.length();
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
            final Object intermediate = optSafe(array.opt(i));

            if (intermediate == null) {
                continue;
            }

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

            try {
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(array, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(array, key, i, item));
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
    public static <T> List<T> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            return null;
        }

        int length = array.length();
        if (length == 0) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final JSONObject json = optSafe(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            T item;
            try {
                item = deserializer.deserialize(context, json);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(array, key, i, json));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(array, key, i, json, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            list.add(item);
        }

        return list;
    }

    @Nullable
    public static <T> List<T> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Deserializer<T, JSONObject> deserializer,
            @NonNull final ListValidator<T> validator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            return null;
        }

        int length = array.length();
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
            final JSONObject json = optSafe(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            T item;
            try {
                item = deserializer.deserialize(context, json);
            } catch (ClassCastException castException) {
                logger.logError(typeMismatch(array, key, i, json));
                continue;
            } catch (Exception e) {
                logger.logError(invalidValue(array, key, i, json, e));
                continue;
            }

            if (item == null) {
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
}
