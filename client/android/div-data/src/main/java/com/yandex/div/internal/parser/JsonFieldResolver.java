package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.data.EntityTemplate;
import com.yandex.div.internal.template.Field;
import com.yandex.div.json.ParsingException;
import com.yandex.div.json.expressions.Expression;
import com.yandex.div.json.expressions.ExpressionList;
import com.yandex.div.serialization.Deserializer;
import com.yandex.div.serialization.ParsingContext;
import com.yandex.div.serialization.TemplateResolver;
import kotlin.Lazy;
import kotlin.OptIn;
import kotlin.jvm.functions.Function1;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.yandex.div.internal.parser.JsonParsers.alwaysValid;
import static com.yandex.div.internal.parser.JsonParsers.alwaysValidList;
import static com.yandex.div.internal.parser.JsonParsers.doNotConvert;
import static com.yandex.div.json.ParsingExceptionKt.dependencyFailed;
import static com.yandex.div.json.ParsingExceptionKt.invalidValue;
import static com.yandex.div.json.ParsingExceptionKt.missingValue;

@SuppressWarnings({"unused"})
@OptIn(markerClass = com.yandex.div.core.annotations.ExperimentalApi.class)
public class JsonFieldResolver {

    @NonNull
    public static <V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key
    ) {
        return resolve(context, field, data, key, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolve(context, field, data, key, converter, alwaysValid());
    }

    @NonNull
    public static <V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ValueValidator<V> validator
    ) {
        return resolve(context, field, data, key, doNotConvert(), validator);
    }

    @NonNull
    public static <R, V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.read(context, data, key, converter, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<V>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.read(context, data, reference, converter, validator);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <T extends EntityTemplate<V>, V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final Field<T> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
            ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.read(context, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            return resolveDependency(context, ((Field.Value<T>) field).value, data, key, resolver.getValue());
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.read(context, data, reference, deserializer);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key
    ) {
        return resolveOptional(context, field, data, key, doNotConvert(), alwaysValid());
    }

    @Nullable
    public static <R, V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveOptional(context, field, data, key, converter, alwaysValid());
    }

    @Nullable
    public static <V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ValueValidator<V> validator
    ) {
        return resolveOptional(context, field, data, key, doNotConvert(), validator);
    }

    @Nullable
    public static <R, V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptional(context, data, key, converter, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<V>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readOptional(context, data, reference, converter, validator);
        }

        return null;
    }

    @Nullable
    public static <T extends EntityTemplate<V>, V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final Field<T> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptional(context, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            return resolveOptionalDependency(context, ((Field.Value<T>) field).value, data, resolver.getValue());
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readOptional(context, data, reference, deserializer);
        }

        return null;
    }

    @NonNull
    public static <V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, data, key, typeHelper);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(context, data, reference, typeHelper);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <R, V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, data, key, typeHelper, converter);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(context, data, reference, typeHelper, converter);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, data, key, typeHelper, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(context, data, reference, typeHelper, validator);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <R, V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, data, key, typeHelper, converter, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(
                    context, data, reference, typeHelper, converter, validator);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, data, key, typeHelper, doNotConvert());
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, data, reference, typeHelper, doNotConvert());
        }

        return null;
    }

    @Nullable
    public static <V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @Nullable final Expression<V> defaultValue
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                    context, data, key, typeHelper, doNotConvert(), defaultValue);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, data, reference, typeHelper, doNotConvert(), defaultValue);
        }

        return null;
    }

    @Nullable
    public static <R, V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, data, key, typeHelper, converter);
        } else if (field.type == Field.TYPE_VALUE){
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(context, data, reference, typeHelper, converter);
        }

        return null;
    }

    @Nullable
    public static <R, V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @Nullable final Expression<V> defaultValue
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                    context, data, key, typeHelper, converter, defaultValue);
        } else if (field.type == Field.TYPE_VALUE){
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, data, reference, typeHelper, converter, defaultValue);
        }

        return null;
    }

    @Nullable
    public static <V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, data, key, typeHelper, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(context, data, reference, typeHelper, validator);
        }

        return null;
    }

    @Nullable
    public static <V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ValueValidator<V> validator,
            @Nullable final Expression<V> defaultValue
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                    context, data, key, typeHelper, validator, defaultValue);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, data, reference, typeHelper, validator, defaultValue);
        }

        return null;
    }

    @Nullable
    public static <R, V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                    context, data, key, typeHelper, converter, validator, null);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, data, reference, typeHelper, converter, validator, null);
        }

        return null;
    }

    @Nullable
    public static <R, V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator,
            @Nullable final Expression<V> defaultValue
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                    context, data, key, typeHelper, converter, validator, defaultValue);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, data, reference, typeHelper, converter, validator, defaultValue);
        }

        return null;
    }

    @NonNull
    public static <R, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveList(context, field, data, key, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveList(context, field, data, key, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveList(context, field, data, key, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return resolveList(context, field, data, key, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readList(context, data, key, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<List<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readList(context, data, reference, converter, listValidator, itemValidator);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <T extends EntityTemplate<V>, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readList(context, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            List<V> result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
            return result;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readList(context, data, reference, deserializer);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <T extends EntityTemplate<V>, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readList(context, data, key, deserializer, listValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            List<V> result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
            if (!listValidator.isValid(result)) {
                throw invalidValue(data, key, result);
            }
            return result;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readList(context, data, reference, deserializer, listValidator);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <R, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveOptionalList(context, field, data, key, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalList(context, field, data, key, converter, listValidator, alwaysValid());
    }

    @Nullable
    public static <V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalList(context, field, data, key, doNotConvert(), listValidator, alwaysValid());
    }

    @Nullable
    public static <V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return resolveOptionalList(context, field, data, key, doNotConvert(), listValidator, itemValidator);
    }

    @Nullable
    public static <R, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        List<V> result = null;
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptionalList(context, data, key, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            return  ((Field.Value<List<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readOptionalList(context, data, reference, converter, listValidator, itemValidator);
        }

        return null;
    }

    @Nullable
    public static <T extends EntityTemplate<V>, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptionalList(context, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            List<V> result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
            return result;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readOptionalList(context, data, reference, deserializer);
        }

        return null;
    }

    @Nullable
    public static <T extends EntityTemplate<V>, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptionalList(context, data, key, deserializer, listValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            List<V> result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
            if (!listValidator.isValid(result)) {
                context.getLogger().logError(invalidValue(data, key, result));
                return null;
            }
            return result;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readOptionalList(context, data, reference, deserializer, listValidator);
        }

        return null;
    }

    @NonNull
    public static <V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        return resolveExpressionList(
                context, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveExpressionList(context, field, data, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveExpressionList(context, field, data, key, typeHelper, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveExpressionList(context, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <R, V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpressionList(
                    context, data, key, typeHelper, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            return  ((Field.Value<ExpressionList<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpressionList(
                    context, data, reference, typeHelper, converter, listValidator, itemValidator);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        return resolveOptionalExpressionList(
                context, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveOptionalExpressionList(
                context, field, data, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalExpressionList(
                context, field, data, key, typeHelper, converter, listValidator, alwaysValid());
    }

    @Nullable
    public static <V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalExpressionList(
                context, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid());
    }

    @Nullable
    public static <V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return resolveOptionalExpressionList(
                context, field, data, key, typeHelper, doNotConvert(), listValidator, itemValidator);
    }

    @Nullable
    public static <R, V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpressionList(
                    context, data, key, typeHelper, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<ExpressionList<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpressionList(
                    context, data, reference, typeHelper, converter, listValidator, itemValidator);
        }

        return null;
    }

    @NonNull
    private static <T extends EntityTemplate<V>, V> V resolveDependency(
            @NonNull final ParsingContext context,
            @NonNull final T template,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<JSONObject, T, V> resolver
    ) {
        try {
            return resolver.resolve(context, template, data);
        } catch (ParsingException e) {
            throw dependencyFailed(data, key, e);
        }
    }

    @Nullable
    private static <T extends EntityTemplate<V>, V> V resolveOptionalDependency(
            @NonNull final ParsingContext context,
            @NonNull final T template,
            @NonNull final JSONObject data,
            @NonNull final TemplateResolver<JSONObject, T, V> resolver
    ) {
        try {
            return resolver.resolve(context, template, data);
        } catch (ParsingException e) {
            context.getLogger().logError(e);
            return null;
        }
    }
}
