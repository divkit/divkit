package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.json.ParsingExceptionKt;
import com.yandex.div.serialization.Deserializer;
import com.yandex.div.serialization.ParsingContext;
import com.yandex.div.serialization.Serializer;
import kotlin.Lazy;
import kotlin.OptIn;
import kotlin.jvm.functions.Function1;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yandex.div.internal.parser.JsonParsers.alwaysValid;
import static com.yandex.div.internal.parser.JsonParsers.alwaysValidList;
import static com.yandex.div.internal.parser.JsonParsers.alwaysValidString;
import static com.yandex.div.internal.parser.JsonParsers.doNotConvert;
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
            @NonNull final ParsingContext context,
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
            @NonNull final ParsingContext context,
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
            context.getLogger().logError(typeMismatch(jsonObject, key, value));
        } catch (Exception e) {
            context.getLogger().logError(invalidValue(jsonObject, key, value, e));
        }

        return result;
    }

    @NonNull
    public static <V> V read(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key
    ) {
        return read(context, jsonObject, key, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, V> V read(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return read(context, jsonObject, key, converter, alwaysValid());
    }

    @NonNull
    public static <V> V read(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<V> validator
    ) {
        return read(context, jsonObject, key, doNotConvert(), validator);
    }

    @NonNull
    public static <R, V> V read(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        final R intermediate = optSafe(jsonObject, key);
        if (intermediate == null) {
            throw missingValue(jsonObject, key);
        }

        V result;
        try {
            result = converter.invoke(intermediate);
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
    public static <V> V read(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        JSONObject json = jsonObject.optJSONObject(key);
        if (json == null) {
            throw missingValue(jsonObject, key);
        }

        V result;
        try {
            result = deserializer.getValue().deserialize(context, json);
        } catch (Exception e) {
            throw dependencyFailed(jsonObject, key, e);
        }

        if (result == null) {
            throw invalidValue(jsonObject, key, null);
        }

        return result;
    }

    @Nullable
    public static <V> V readOptional(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key
    ) {
        return readOptional(context, jsonObject, key, doNotConvert(), alwaysValid());
    }

    @Nullable
    public static <R, V> V readOptional(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return readOptional(context, jsonObject, key, converter, alwaysValid());
    }

    @Nullable
    public static <V> V readOptional(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ValueValidator<V> validator
    ) {
        return readOptional(context, jsonObject, key, doNotConvert(), validator);
    }

    @Nullable
    public static <R, V> V readOptional(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        final R intermediate = optSafe(jsonObject, key);
        if (intermediate == null) {
            return null;
        }

        V result = null;
        try {
            result = converter.invoke(intermediate);
        } catch (ClassCastException castException) {
            context.getLogger().logError(typeMismatch(jsonObject, key, intermediate));
            return null;
        } catch (Exception e) {
            context.getLogger().logError(invalidValue(jsonObject, key, intermediate, e));
            return null;
        }

        if (result == null) {
            context.getLogger().logError(ParsingExceptionKt.invalidValue(jsonObject, key, intermediate));
            return null;
        }

        try {
            if (!validator.isValid(result)) {
                context.getLogger().logError(invalidValue(jsonObject, key, intermediate));
                return null;
            }
        } catch (ClassCastException castException) {
            context.getLogger().logError(typeMismatch(jsonObject, key, intermediate));
            return null;
        }

        return result;
    }

    @Nullable
    public static <V> V readOptional(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        JSONObject json = jsonObject.optJSONObject(key);
        if (json == null) {
            return null;
        }

        try {
            return deserializer.getValue().deserialize(context, json);
        } catch (Exception e) {
            context.getLogger().logError(dependencyFailed(jsonObject, key, e));
            return null;
        }
    }

    @NonNull
    public static <R, V> List<V> readList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return readList(context, jsonObject, key, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static List<String> readList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final ListValidator<String> listValidator
    ) {
        return readList(context, jsonObject, key, doNotConvert(), listValidator, alwaysValidString());
    }

    @NonNull
    public static <R, V> List<V> readList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readList(context, jsonObject, key, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <R, V> List<V> readList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            throw missingValue(jsonObject, key);
        }

        int length = array.length();
        if (length == 0) {
            List<V> emptyList = Collections.emptyList();
            try {
                if (!listValidator.isValid(emptyList)) {
                    context.getLogger().logError(invalidValue(jsonObject, key, emptyList));
                    return emptyList;
                }
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(jsonObject, key, emptyList));
                return emptyList;
            }
            return emptyList;
        }

        List<V> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            final R intermediate = optSafe(array, i);

            if (intermediate == null) {
                continue;
            }

            V item;
            try {
                item = converter.invoke(intermediate);
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(array, key, i, intermediate));
                continue;
            } catch (Exception e) {
                context.getLogger().logError(invalidValue(array, key, i, intermediate, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            try {
                if (!itemValidator.isValid(item)) {
                    context.getLogger().logError(invalidValue(array, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(array, key, i, item));
                continue;
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
    public static <V> List<V> readList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            throw missingValue(jsonObject, key);
        }

        int length = array.length();
        if (length == 0) {
            return Collections.emptyList();
        }

        List<V> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject json = nullable(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            V item;
            try {
                item = deserializer.getValue().deserialize(context, json);
            } catch (Exception e) {
                context.getLogger().logError(dependencyFailed(array, key, i, e));
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
    public static <V> List<V> readList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            throw missingValue(jsonObject, key);
        }

        int length = array.length();
        if (length == 0) {
            List<V> emptyList = Collections.emptyList();
            try {
                if (!listValidator.isValid(emptyList)) {
                    context.getLogger().logError(invalidValue(jsonObject, key, emptyList));
                    return emptyList;
                }
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(jsonObject, key, emptyList));
                return emptyList;
            }
            return emptyList;
        }

        List<V> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject json = nullable(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            V item;
            try {
                item = deserializer.getValue().deserialize(context, json);
            } catch (Exception e) {
                context.getLogger().logError(dependencyFailed(array, key, i, e));
                continue;
            }

            if (item == null) {
                continue;
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

    @Nullable
    public static <R, V> List<V> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return readOptionalList(context, jsonObject, key, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, V> List<V> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readOptionalList(context, jsonObject, key, converter, listValidator, alwaysValid());
    }

    @Nullable
    public static <R, V> List<V> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            return null;
        }

        int length = array.length();
        if (length == 0) {
            List<V> emptyList = Collections.emptyList();
            try {
                if (!listValidator.isValid(emptyList)) {
                    context.getLogger().logError(invalidValue(jsonObject, key, emptyList));
                    return null;
                }
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(jsonObject, key, emptyList));
                return null;
            }
            return emptyList;
        }
        List<V> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final R intermediate = optSafe(array, i);

            if (intermediate == null) {
                continue;
            }

            V item;
            try {
                item = converter.invoke(intermediate);
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(array, key, i, intermediate));
                continue;
            } catch (Exception e) {
                context.getLogger().logError(invalidValue(array, key, i, intermediate, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            try {
                if (!itemValidator.isValid(item)) {
                    context.getLogger().logError(invalidValue(array, key, i, item));
                    continue;
                }
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(array, key, i, item));
                continue;
            }

            list.add(item);
        }

        try {
            if (!listValidator.isValid(list)) {
                context.getLogger().logError(invalidValue(jsonObject, key, list));
                return null;
            }
        } catch (ClassCastException castException) {
            context.getLogger().logError(typeMismatch(jsonObject, key, list));
            return null;
        }

        return list;
    }

    @Nullable
    public static <V> List<V> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            return null;
        }

        int length = array.length();
        if (length == 0) {
            return Collections.emptyList();
        }
        List<V> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final JSONObject json = nullable(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            V item;
            try {
                item = deserializer.getValue().deserialize(context, json);
            } catch (Exception e) {
                context.getLogger().logError(dependencyFailed(array, key, i, e));
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
    public static <V> List<V> readOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        JSONArray array = jsonObject.optJSONArray(key);
        if (array == null) {
            return null;
        }

        int length = array.length();
        if (length == 0) {
            List<V> emptyList = Collections.emptyList();
            try {
                if (!listValidator.isValid(emptyList)) {
                    context.getLogger().logError(invalidValue(jsonObject, key, emptyList));
                    return null;
                }
            } catch (ClassCastException castException) {
                context.getLogger().logError(typeMismatch(jsonObject, key, emptyList));
                return null;
            }
            return emptyList;
        }
        List<V> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final JSONObject json = nullable(array.optJSONObject(i));

            if (json == null) {
                continue;
            }

            V item;
            try {
                item = deserializer.getValue().deserialize(context, json);
            } catch (Exception e) {
                context.getLogger().logError(dependencyFailed(array, key, i, e));
                continue;
            }

            if (item == null) {
                continue;
            }

            list.add(item);
        }

        try {
            if (!listValidator.isValid(list)) {
                context.getLogger().logError(invalidValue(jsonObject, key, list));
                return null;
            }
        } catch (ClassCastException castException) {
            context.getLogger().logError(typeMismatch(jsonObject, key, list));
            return null;
        }

        return list;
    }

    @Nullable
    private static JSONObject nullable(@Nullable JSONObject json) {
        if (json == null || json == JSONObject.NULL) {
            return null;
        }

        return json;
    }

    @Nullable
    private static <T> T optSafe(JSONObject json, String key) {
        Object value = json.opt(key);
        if (value == JSONObject.NULL) {
            return null;
        }

        return (T) value;
    }

    @Nullable
    private static <T> T optSafe(JSONArray json, int index) {
        Object value = json.opt(index);
        if (value == JSONObject.NULL) {
            return null;
        }

        return (T) value;
    }

    public static <V> void write(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final V value
    ) {
        write(context, jsonObject, key, value, doNotConvert());
    }

    public static <R, V> void write(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final V value,
            @NonNull final Function1<V, R> converter
    ) {
        if (value != null) {
            try {
                jsonObject.put(key, converter.invoke(value));
            } catch (JSONException e) {
                context.getLogger().logError(e);
            }
        }
    }

    public static <V> void write(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final V value,
            @NonNull final Lazy<Serializer<JSONObject, V>> serializer
    ) {
        if (value != null) {
            try {
                jsonObject.put(key, serializer.getValue().serialize(context, value));
            } catch (JSONException e) {
                context.getLogger().logError(e);
            }
        }
    }

    public static <V> void writeList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final List<V> list
    ) {
        writeList(context, jsonObject, key, list, (Function1<V, ?>) doNotConvert());
    }

    public static <R, V> void writeList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final List<V> list,
            @NonNull final Function1<V, R> converter
    ) {
        if (list != null && !list.isEmpty()) {
            int length = list.size();
            JSONArray array = new JSONArray();
            for (int i = 0; i < length; i++) {
                V item = list.get(i);
                array.put(converter.invoke(item));
            }
            try {
                jsonObject.put(key, array);
            } catch (JSONException e) {
                context.getLogger().logError(e);
            }
        }
    }

    public static <V> void writeList(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final List<V> list,
            @NonNull final Lazy<Serializer<JSONObject, V>> serializer
    ) {
        if (list != null && !list.isEmpty()) {
            int length = list.size();
            JSONArray array = new JSONArray();

            for (int i = 0; i < length; i++) {
                V item = list.get(i);
                array.put(serializer.getValue().serialize(context, item));
            }
            try {
                jsonObject.put(key, array);
            } catch (JSONException e) {
                context.getLogger().logError(e);
            }
        }
    }
}
