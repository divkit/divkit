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

@SuppressWarnings({"ForLoopReplaceableByForEach", "StringEquality", "unused"})
public class JsonFieldResolver {

    @NonNull
    public static <D> D resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key
    ) {
        return resolve(context, logger, field, data, key, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, D> D resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter
    ) {
        return resolve(context, logger, field, data, key, converter, alwaysValid());
    }

    @NonNull
    public static <D> D resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ValueValidator<D> validator
    ) {
        return resolve(context, logger, field, data, key, doNotConvert(), validator);
    }

    @NonNull
    public static <R, D> D resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter,
            @NonNull final ValueValidator<D> validator
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonPropertyParser.read(context, logger, data, key, converter, validator);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<D>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonPropertyParser.read(context, logger, data, reference, converter, validator);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <D, T extends EntityTemplate<D>> D resolve(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<T> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver,
            @NonNull final Deserializer<D, JSONObject> deserializer
            ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonPropertyParser.read(context, logger, data, key, deserializer);
        } else if (field instanceof Field.Value) {
            return resolveDependency(context, ((Field.Value<T>) field).getValue(), data, key, resolver);
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonPropertyParser.read(context, logger, data, reference, deserializer);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <D> D resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key
    ) {
        return resolveOptional(context, logger, field, data, key, doNotConvert(), alwaysValid());
    }

    @Nullable
    public static <R, D> D resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter
    ) {
        return resolveOptional(context, logger, field, data, key, converter, alwaysValid());
    }

    @Nullable
    public static <D> D resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ValueValidator<D> validator
    ) {
        return resolveOptional(context, logger, field, data, key, doNotConvert(), validator);
    }

    @Nullable
    public static <R, D> D resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<D> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter,
            @NonNull final ValueValidator<D> validator
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonPropertyParser.read(context, logger, data, key, converter, validator);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<D>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonPropertyParser.read(context, logger, data, reference, converter, validator);
        }

        return null;
    }

    @Nullable
    public static <D, T extends EntityTemplate<D>> D resolveOptional(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<T> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver,
            @NonNull final Deserializer<D, JSONObject> deserializer
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonPropertyParser.read(context, logger, data, key, deserializer);
        } else if (field instanceof Field.Value) {
            return resolveOptionalDependency(context, logger, ((Field.Value<T>) field).getValue(), data, resolver);
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonPropertyParser.read(context, logger, data, reference, deserializer);
        }

        return null;
    }

    @NonNull
    public static <D> Expression<D> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readExpression(context, logger, data, reference, typeHelper);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <R, D> Expression<D> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper, converter);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readExpression(context, logger, data, reference, typeHelper, converter);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <D> Expression<D> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final ValueValidator<D> validator
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper, validator);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readExpression(context, logger, data, reference, typeHelper, validator);
        }

        throw missingValue(data, key);
    }

    @NonNull
    public static <R, D> Expression<D> resolveExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter,
            @NonNull final ValueValidator<D> validator
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readExpression(context, logger, data, key, typeHelper, converter, validator);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readExpression(
                    context, logger, data, reference, typeHelper, converter, validator);
        }

        throw missingValue(data, key);
    }

    @Nullable
    public static <D> Expression<D> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, logger, data, key, typeHelper, doNotConvert());
        } else if (field instanceof Field.Value) {
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readOptionalExpression(
                    context, logger, data, reference, typeHelper, doNotConvert());
        }

        return null;
    }

    @Nullable
    public static <R, D> Expression<D> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, logger, data, key, typeHelper, converter);
        } else if (field instanceof Field.Value){
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readOptionalExpression(context, logger, data, reference, typeHelper, converter);
        }

        return null;
    }

    @Nullable
    public static <D> Expression<D> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final ValueValidator<D> validator
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(context, logger, data, key, typeHelper, validator);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readOptionalExpression(context, logger, data, reference, typeHelper, validator);
        }

        return null;
    }

    @Nullable
    public static <R, D> Expression<D> resolveOptionalExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<Expression<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter,
            @NonNull final ValueValidator<D> validator
    ) {
        if (field.getOverridable() && data.has(key)) {
            return JsonExpressionParser.readOptionalExpression(
                    context, logger, data, key, typeHelper, converter, validator);
        } else if (field instanceof Field.Value) {
            return ((Field.Value<Expression<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            return JsonExpressionParser.readOptionalExpression(
                    context, logger, data, reference, typeHelper, converter, validator);
        }

        return null;
    }

    @NonNull
    public static <R, D> List<D> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter
    ) {
        return resolveList(context, logger, field, data, key, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, D> List<D> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveList(context, logger, field, data, key, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <D> List<D> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveList(context, logger, field, data, key, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <D> List<D> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<D> listValidator,
            @NonNull final ValueValidator<D> itemValidator
    ) {
        return resolveList(context, logger, field, data, key, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, D> List<D> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator,
            @NonNull final ValueValidator<D> itemValidator
    ) {
        List<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonPropertyParser.readList(context, logger, data, key, converter, listValidator, itemValidator);
        } else if (field instanceof Field.Value) {
            result = ((Field.Value<List<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            result = JsonPropertyParser.readList(
                    context, logger, data, reference, converter, listValidator, itemValidator);
        }

        if (result == null) {
            throw missingValue(data, key);
        } else if (!listValidator.isValid(result)) {
            throw invalidValue(data, key, result);
        } else if (itemValidator != alwaysValidList()) {
            for (int i = 0; i < result.size(); i++) {
                D item = result.get(i);
                if (!itemValidator.isValid(item)) {
                    throw invalidValue(data, key, item);
                }
            }
        }
        return result;
    }

    @NonNull
    public static <D, T extends EntityTemplate<D>> List<D> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver,
            @NonNull final Deserializer<D, JSONObject> deserializer
    ) {
        List<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonPropertyParser.readList(context, logger, data, key, deserializer);
        } else if (field instanceof Field.Value) {
            List<T> templates = ((Field.Value<List<T>>) field).getValue();
            result = new ArrayList<D>(templates.size());
            for (int i = 0; i < templates.size(); i++) {
                T template = templates.get(i);
                result.add(resolveOptionalDependency(context, logger, template, data, resolver));
            }
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            result = JsonPropertyParser.readList(context, logger, data, reference, deserializer);
        }

        if (result == null) {
            throw missingValue(data, key);
        }
        return result;
    }

    @NonNull
    public static <D, T extends EntityTemplate<D>> List<D> resolveList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver,
            @NonNull final Deserializer<D, JSONObject> deserializer,
            @NonNull final ListValidator<D> listValidator
    ) {
        List<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonPropertyParser.readList(context, logger, data, key, deserializer, listValidator);
        } else if (field instanceof Field.Value) {
            List<T> templates = ((Field.Value<List<T>>) field).getValue();
            result = new ArrayList<D>(templates.size());
            for (int i = 0; i < templates.size(); i++) {
                T template = templates.get(i);
                result.add(resolveOptionalDependency(context, logger, template, data, resolver));
            }
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
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
    public static <R, D> List<D> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter
    ) {
        return resolveOptionalList(context, logger, field, data, key, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, D> List<D> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveOptionalList(context, logger, field, data, key, converter, listValidator, alwaysValid());
    }

    @Nullable
    public static <D> List<D> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveOptionalList(context, logger, field, data, key, doNotConvert(), listValidator, alwaysValid());
    }

    @Nullable
    public static <D> List<D> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final ListValidator<D> listValidator,
            @NonNull final ValueValidator<D> itemValidator
    ) {
        return resolveOptionalList(context, logger, field, data, key, doNotConvert(), listValidator, itemValidator);
    }

    @Nullable
    public static <R, D> List<D> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator,
            @NonNull final ValueValidator<D> itemValidator
    ) {
        List<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonPropertyParser.readOptionalList(
                    context, logger, data, key, converter, listValidator, itemValidator);
        } else if (field instanceof Field.Value) {
            result = ((Field.Value<List<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            result = JsonPropertyParser.readOptionalList(
                    context, logger, data, reference, converter, listValidator, itemValidator);
        }

        if (result == null) {
            return null;
        } else if (!listValidator.isValid(result)) {
            logger.logError(invalidValue(data, key, result));
            return null;
        } else if (itemValidator != alwaysValidList()) {
            for (int i = 0; i < result.size(); i++) {
                D item = result.get(i);
                if (!itemValidator.isValid(item)) {
                    logger.logError(invalidValue(data, key, item));
                    return null;
                }
            }
        }
        return result;
    }

    @Nullable
    public static <D, T extends EntityTemplate<D>> List<D> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver,
            @NonNull final Deserializer<D, JSONObject> deserializer
    ) {
        List<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonPropertyParser.readOptionalList(context, logger, data, key, deserializer);
        } else if (field instanceof Field.Value) {
            List<T> templates = ((Field.Value<List<T>>) field).getValue();
            result = new ArrayList<D>(templates.size());
            for (int i = 0; i < templates.size(); i++) {
                T template = templates.get(i);
                result.add(resolveOptionalDependency(context, logger, template, data, resolver));
            }
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            result = JsonPropertyParser.readOptionalList(context, logger, data, reference, deserializer);
        }

        return result;
    }

    @Nullable
    public static <D, T extends EntityTemplate<D>> List<D> resolveOptionalList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<List<T>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver,
            @NonNull final Deserializer<D, JSONObject> deserializer,
            @NonNull final ListValidator<D> listValidator
    ) {
        List<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonPropertyParser.readOptionalList(context, logger, data, key, deserializer, listValidator);
        } else if (field instanceof Field.Value) {
            List<T> templates = ((Field.Value<List<T>>) field).getValue();
            result = new ArrayList<D>(templates.size());
            for (int i = 0; i < templates.size(); i++) {
                T template = templates.get(i);
                result.add(resolveOptionalDependency(context, logger, template, data, resolver));
            }
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
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
    public static <D> ExpressionList<D> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper
    ) {
        return resolveExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, D> ExpressionList<D> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter
    ) {
        return resolveExpressionList(context, logger, field, data, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, D> ExpressionList<D> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveExpressionList(context, logger, field, data, key, typeHelper, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <D> ExpressionList<D> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveExpressionList(context, logger, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <R, D> ExpressionList<D> resolveExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator,
            @NonNull final ValueValidator<D> itemValidator
    ) {
        ExpressionList<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonExpressionParser.readExpressionList(
                    context, logger, data, key, typeHelper, converter, listValidator, itemValidator);
        } else if (field instanceof Field.Value) {
            result = ((Field.Value<ExpressionList<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            result = JsonExpressionParser.readExpressionList(
                    context, logger, data, reference, typeHelper, converter, listValidator, itemValidator);
        }

        if (result == null) {
            throw missingValue(data, key);
        }
        return result;
    }

    @Nullable
    public static <D> ExpressionList<D> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, D> ExpressionList<D> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, converter, alwaysValidList(), alwaysValid());
    }

    @Nullable
    public static <R, D> ExpressionList<D> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, converter, listValidator, alwaysValid());
    }

    @Nullable
    public static <D> ExpressionList<D> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final ListValidator<D> listValidator
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), listValidator, alwaysValid());
    }

    @Nullable
    public static <D> ExpressionList<D> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final ListValidator<D> listValidator,
            @NonNull final ValueValidator<D> itemValidator
    ) {
        return resolveOptionalExpressionList(
                context, logger, field, data, key, typeHelper, doNotConvert(), listValidator, itemValidator);
    }

    @Nullable
    public static <R, D> ExpressionList<D> resolveOptionalExpressionList(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final Field<ExpressionList<D>> field,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TypeHelper<D> typeHelper,
            @NonNull final Function1<R, D> converter,
            @NonNull final ListValidator<D> listValidator,
            @NonNull final ValueValidator<D> itemValidator
    ) {
        ExpressionList<D> result = null;
        if (field.getOverridable() && data.has(key)) {
            result = JsonExpressionParser.readOptionalExpressionList(
                    context, logger, data, key, typeHelper, converter, listValidator, itemValidator);
        } else if (field instanceof Field.Value) {
            result = ((Field.Value<ExpressionList<D>>) field).getValue();
        } else if (field instanceof Field.Reference) {
            String reference = ((Field.Reference<?>) field).getReference();
            result = JsonExpressionParser.readOptionalExpressionList(
                    context, logger, data, reference, typeHelper, converter, listValidator, itemValidator);
        }

        return result;
    }

    @NonNull
    private static <D, T extends EntityTemplate<D>> D resolveDependency(
            @NonNull final ParsingContext context,
            @NonNull final T template,
            @NonNull final JSONObject data,
            @NonNull final String key,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver
    ) {
        try {
            return resolver.resolve(context, template, data);
        } catch (ParsingException e) {
            throw dependencyFailed(data, key, e);
        }
    }

    @Nullable
    private static <D, T extends EntityTemplate<D>> D resolveOptionalDependency(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final T template,
            @NonNull final JSONObject data,
            @NonNull final TemplateResolver<D, T, JSONObject> resolver
    ) {
        try {
            return resolver.resolve(context, template, data);
        } catch (ParsingException e) {
            logger.logError(e);
            return null;
        }
    }
}
