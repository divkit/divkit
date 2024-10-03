package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.data.EntityTemplate;
import com.yandex.div.internal.template.Field;
import com.yandex.div.json.ParsingErrorLogger;
import com.yandex.div.json.ParsingException;
import com.yandex.div.json.expressions.Expression;
import com.yandex.div.json.expressions.ExpressionList;
import com.yandex.div.serialization.Deserializer;
import com.yandex.div.serialization.ParsingContext;
import com.yandex.div.serialization.TemplateResolver;

import kotlin.OptIn;
import kotlin.Lazy;
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

@SuppressWarnings({"ForLoopReplaceableByForEach", "unused"})
@OptIn(markerClass = com.yandex.div.core.annotations.ExperimentalApi.class)
public class JsonFieldResolver {

    @NonNull
    public static <V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key
    ) {
        return resolve(context, logger, field, data, key, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolve(context, logger, field, data, key, converter, alwaysValid());
    }

    @NonNull
    public static <V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ValueValidator<V> validator
    ) {
        return resolve(context, logger, field, data, key, doNotConvert(), validator);
    }

    @NonNull
    public static <R, V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.read(context, logger, data, key, converter, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<V>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.read(context, logger, data, reference, converter, validator);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <T extends EntityTemplate<V>, V> V resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<T> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
            ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.read(context, logger, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            return resolveDependency(context, ((Field.Value<T>) field).value, data, key, resolver.getValue());
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.read(context, logger, data, reference, deserializer);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key
    ) {
        return resolveOptional(context, logger, field, data, key, doNotConvert(), alwaysValid());
    }

    @Nullable
    public static <R, V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveOptional(context, logger, field, data, key, converter, alwaysValid());
    }

    @Nullable
    public static <V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ValueValidator<V> validator
    ) {
        return resolveOptional(context, logger, field, data, key, doNotConvert(), validator);
    }

    @Nullable
    public static <R, V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<V> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptional(context, logger, data, key, converter, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<V>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readOptional(context, logger, data, reference, converter, validator);
        }

        return null;
    }

    @Nullable
    public static <T extends EntityTemplate<V>, V> V resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<T> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        if (field.overridable && data.has(key)) {
            return JsonPropertyParser.readOptional(context, logger, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            return resolveOptionalDependency(context, logger, ((Field.Value<T>) field).value, data, resolver.getValue());
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonPropertyParser.readOptional(context, logger, data, reference, deserializer);
        }

        return null;
    }

    @NonNull
    public static <V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(context, logger, data, reference, typeHelper);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <R, V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper, converter);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(context, logger, data, reference, typeHelper, converter);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(context, logger, data, reference, typeHelper, validator);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <R, V> Expression<V> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper, converter, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readExpression(
                    context, logger, data, reference, typeHelper, converter, validator);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, logger, data, key, typeHelper, doNotConvert());
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, logger, data, reference, typeHelper, doNotConvert());
        }

        return null;
    }

    @Nullable
    public static <R, V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, logger, data, key, typeHelper, converter);
        } else if (field.type == Field.TYPE_VALUE){
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(context, logger, data, reference, typeHelper, converter);
        }

        return null;
    }

    @Nullable
    public static <V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, logger, data, key, typeHelper, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(context, logger, data, reference, typeHelper, validator);
        }

        return null;
    }

    @Nullable
    public static <R, V> Expression<V> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        if (field.overridable && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                    context, logger, data, key, typeHelper, converter, validator);
        } else if (field.type == Field.TYPE_VALUE) {
            return ((Field.Value<Expression<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            return JsonExpressionParser.readOptionalExpression(
                    context, logger, data, reference, typeHelper, converter, validator);
        }

        return null;
    }

    @NonNull
    public static <R, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveList(context, logger, field, data, key, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveList(context, logger, field, data, key, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveList(context, logger, field, data, key, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return resolveList(context, logger, field, data, key, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        List<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonPropertyParser.readList(context, logger, data, key, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            result = ((Field.Value<List<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonPropertyParser.readList(
                    context, logger, data, reference, converter, listValidator, itemValidator);
        }

        if (result == null) {
            throw missingValue(data, key);
        } else if (!listValidator.isValid(result)) {
            throw invalidValue(data, key, result);
        } else if (itemValidator != alwaysValidList()) {
            int length = result.size();
            for (int i = 0; i < length; i++) {
                V item = result.get(i);
                if (!itemValidator.isValid(item)) {
                    throw invalidValue(data, key, item);
                }
            }
        }
        return result;
    }

    @NonNull
    public static <T extends EntityTemplate<V>, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        List<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonPropertyParser.readList(context, logger, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, logger, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonPropertyParser.readList(context, logger, data, reference, deserializer);
        }

        if (result == null) {
            throw missingValue(data, key);
        }
        return result;
    }

    @NonNull
    public static <T extends EntityTemplate<V>, V> List<V> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        List<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonPropertyParser.readList(context, logger, data, key, deserializer, listValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, logger, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonPropertyParser.readList(context, logger, data, reference, deserializer, listValidator);
        }

        if (result == null) {
            throw missingValue(data, key);
        } else if (!listValidator.isValid(result)) {
            throw invalidValue(data, key, result);
        }
        return result;
    }

    @Nullable
    public static <R, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveOptionalList(context, logger, field, data, key, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalList(context, logger, field, data, key, converter, listValidator, alwaysValid());
    }

    @Nullable
    public static <V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalList(context, logger, field, data, key, doNotConvert(), listValidator, alwaysValid());
    }

    @Nullable
    public static <V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return resolveOptionalList(context, logger, field, data, key, doNotConvert(), listValidator, itemValidator);
    }

    @Nullable
    public static <R, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        List<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonPropertyParser.readOptionalList(
                    context, logger, data, key, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            result = ((Field.Value<List<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonPropertyParser.readOptionalList(
                    context, logger, data, reference, converter, listValidator, itemValidator);
        }

        if (result == null) {
            return null;
        } else if (!listValidator.isValid(result)) {
            logger.logError(invalidValue(data, key, result));
            return null;
        } else if (itemValidator != alwaysValidList()) {
            int length = result.size();
            for (int i = 0; i < length; i++) {
                V item = result.get(i);
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(data, key, item));
                    return null;
                }
            }
        }
        return result;
    }

    @Nullable
    public static <T extends EntityTemplate<V>, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        List<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonPropertyParser.readOptionalList(context, logger, data, key, deserializer);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, logger, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonPropertyParser.readOptionalList(context, logger, data, reference, deserializer);
        }

        return result;
    }

    @Nullable
    public static <T extends EntityTemplate<V>, V> List<V> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Lazy<TemplateResolver<JSONObject, T, V>> resolver,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        List<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonPropertyParser.readOptionalList(context, logger, data, key, deserializer, listValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            List<T> templates = ((Field.Value<List<T>>) field).value;
            int length = templates.size();
            result = new ArrayList<V>(length);
            TemplateResolver<JSONObject, T, V> resolverLocal = resolver.getValue();
            for (int i = 0; i < length; i++) {
                T template = templates.get(i);
                V value = resolveOptionalDependency(context, logger, template, data, resolverLocal);
                if (value != null) {
                    result.add(value);
                }
            }
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonPropertyParser.readOptionalList(context, logger, data, reference, deserializer, listValidator);
        }

        if (result == null) {
            return null;
        } else if (!listValidator.isValid(result)) {
            logger.logError(invalidValue(data, key, result));
            return null;
        }
        return result;
    }

    @NonNull
    public static <V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        return resolveExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveExpressionList(context, logger, field, data, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveExpressionList(context, logger, field, data, key, typeHelper, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveExpressionList(context, logger, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <R, V> ExpressionList<V> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        ExpressionList<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonExpressionParser.readExpressionList(
                    context, logger, data, key, typeHelper, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            result = ((Field.Value<ExpressionList<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonExpressionParser.readExpressionList(
                    context, logger, data, reference, typeHelper, converter, listValidator, itemValidator);
        }

        if (result == null) {
            throw missingValue(data, key);
        }
        return result;
    }

    @Nullable
    public static <V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, converter, listValidator, alwaysValid());
    }

    @Nullable
    public static <V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ListValidator<V> listValidator
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid());
    }

    @Nullable
    public static <V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), listValidator, itemValidator);
    }

    @Nullable
    public static <R, V> ExpressionList<V> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<V>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        ExpressionList<V> result = null;
        if (field.overridable && data.has(key)) {
            result = JsonExpressionParser.readOptionalExpressionList(
                    context, logger, data, key, typeHelper, converter, listValidator, itemValidator);
        } else if (field.type == Field.TYPE_VALUE) {
            result = ((Field.Value<ExpressionList<V>>) field).value;
        } else if (field.type == Field.TYPE_REFERENCE) {
            String reference = ((Field.Reference<?>) field).reference;
            result = JsonExpressionParser.readOptionalExpressionList(
                    context, logger, data, reference, typeHelper, converter, listValidator, itemValidator);
        }

        return result;
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
            @NonNull final ParsingErrorLogger logger,
            @NonNull final T template,
            @NonNull final JSONObject data,
            @NonNull final TemplateResolver<JSONObject, T, V> resolver
    ) {
        try {
            return resolver.resolve(context, template, data);
        } catch (ParsingException e) {
            logger.logError(e);
            return null;
        }
    }
}
