package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.internal.template.Field;
import com.yandex.div.internal.template.FieldKt;
import com.yandex.div.json.ParsingErrorLogger;
import com.yandex.div.json.ParsingException;
import com.yandex.div.json.expressions.Expression;
import com.yandex.div.json.expressions.ExpressionList;
import com.yandex.div.serialization.Deserializer;
import com.yandex.div.serialization.ParsingContext;
import kotlin.jvm.functions.Function1;
import org.json.JSONObject;

import java.util.List;

import static com.yandex.div.internal.parser.JsonParsers.alwaysValid;
import static com.yandex.div.internal.parser.JsonParsers.alwaysValidList;
import static com.yandex.div.internal.parser.JsonParsers.alwaysValidString;
import static com.yandex.div.internal.parser.JsonParsers.doNotConvert;
import static com.yandex.div.internal.parser.JsonTemplateParserKt.suppressMissingValueOrThrow;

/**
 * A Java-version of JsonTemplateParser.kt that is faster because it generates less garbage during parsing
 * and skips unnecessary checks (like nullability).
 *
 * NOTE! Please do not change Function1 and Function2 with readable java-interfaces.
 * This will only make parsing slower.
 */
@SuppressWarnings("unused")
public class JsonFieldParser {

    private static final ValueValidator<String> IS_NOT_EMPTY = value -> !value.isEmpty();

    @NonNull
    public static <T> Field<T> readField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback
    ) {
        return readField(context, logger, json, key, overridable, fallback, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, T> Field<T> readField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final Function1<R, T> converter
    ) {
        return readField(context, logger, json, key, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <T> Field<T> readField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final ValueValidator<T> validator
    ) {
        return readField(context, logger, json, key, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R, T> Field<T> readField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
        try {
            T value = JsonPropertyParser.read(context, logger, json, key, converter, validator);
            return new Field.Value<>(overridable, value);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, logger, json, key);
            Field<T> referenceOrFallback = referenceOrFallback(overridable, reference, fallback);
            if (referenceOrFallback != null) {
                return referenceOrFallback;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <T> Field<T> readField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        try {
            T result = JsonPropertyParser.read(context, logger, json, key, deserializer);
            return new Field.Value<>(overridable, result);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, logger, json, key);
            Field<T> referenceOrFallback = referenceOrFallback(overridable, reference, fallback);
            if (referenceOrFallback != null) {
                return referenceOrFallback;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <T> Field<T> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback
    ) {
        return readOptionalField(context, logger, json, key, overridable, fallback, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, T> Field<T> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptionalField(context, logger, json, key, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <T> Field<T> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final ValueValidator<T> validator
    ) {
        return readOptionalField(context, logger, json, key, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R, T> Field<T> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
        T opt = JsonPropertyParser.readOptional(context, logger, json, key, converter, validator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        String reference = readReference(context, logger, json, key);
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        } else if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        } else {
            return Field.Companion.nullField(overridable);
        }
    }

    @NonNull
    public static <T> Field<T> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<T> fallback,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        T opt = JsonPropertyParser.readOptional(context, logger, json, key, deserializer);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        String reference = readReference(context, logger, json, key);
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        } else if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        } else {
            return Field.Companion.nullField(overridable);
        }
    }

    @NonNull
    public static <T> Field<Expression<T>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<T>> fallback
    ) {
        return readFieldWithExpression(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, T> Field<Expression<T>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<T>> fallback,
            @NonNull final Function1<R, T> converter
    ) {
        return readFieldWithExpression(
                context, logger, json, key, typeHelper, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <T> Field<Expression<T>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<T>> fallback,
            @NonNull final ValueValidator<T> validator
    ) {
        return readFieldWithExpression(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R, T> Field<Expression<T>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
        try {
            Expression<T> expression = JsonExpressionParser.readExpression(
                    context, logger, json, key, typeHelper, converter, validator);
            return new Field.Value<>(overridable, expression);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            Field<Expression<T>> referenceOrFallback = referenceOrFallback(
                    overridable,
                    readReference(context, logger, json, key),
                    fallback);
            if (referenceOrFallback != null) {
                return referenceOrFallback;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static Field<Expression<String>> readOptionalFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<String> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<String>> fallback
    ) {
        return readOptionalFieldWithExpression(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), alwaysValidString());
    }

    @NonNull
    public static <R, T> Field<Expression<T>> readOptionalFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<T>> fallback,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptionalFieldWithExpression(
                context, logger, json, key, typeHelper, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <T> Field<Expression<T>> readOptionalFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<T>> fallback,
            @NonNull final ValueValidator<T> validator
    ) {
        return readOptionalFieldWithExpression(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R,T> Field<Expression<T>> readOptionalFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ValueValidator<T> validator
    ) {
        Expression<T> opt = JsonExpressionParser.readOptionalExpression(
                context, logger, json, key, typeHelper, converter, validator, null);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, logger, json, key);
            if (reference != null) {
                return new Field.Reference<>(overridable, reference);
            } else if (fallback != null) {
                return FieldKt.clone(fallback, overridable);
            } else {
                return Field.Companion.nullField(overridable);
            }
        }
    }

    @NonNull
    public static <R, T> Field<List<T>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Function1<R,T> converter
    ) {
        return readListField(
                context, logger, json, key, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, T> Field<List<T>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Function1<R,T> converter,
            @NonNull final ListValidator<T> listValidator
    ) {
        return readListField(
                context, logger, json, key, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <R, T> Field<List<T>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Function1<R,T> converter,
            @NonNull final ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        try {
            List<T> opt = JsonPropertyParser.readList(
                    context, logger, json, key, converter, listValidator, itemValidator);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, logger, json, key);
            Field<List<T>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <T> Field<List<T>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        try {
            List<T> opt = JsonPropertyParser.readList(
                    context, logger, json, key, deserializer);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, logger, json, key);
            Field<List<T>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <T> Field<List<T>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Deserializer<T, JSONObject> deserializer,
            @NonNull final ListValidator<T> listValidator
    ) {
        try {
            List<T> opt = JsonPropertyParser.readList(
                    context, logger, json, key, deserializer, listValidator);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, logger, json, key);
            Field<List<T>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptionalListField(
                context, logger, json, key, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> listValidator
    ) {
        return readOptionalListField(
                context, logger, json, key, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <T> Field<List<T>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        return readOptionalListField(
                context, logger, json, key, overridable, fallback, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        List<T> opt = JsonPropertyParser.readOptionalList(
                context, logger, json, key, converter, listValidator, itemValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, logger, json, key);
            if (reference != null) {
                return new Field.Reference<>(overridable, reference);
            } else if (fallback != null) {
                return FieldKt.clone(fallback, overridable);
            } else {
                return Field.Companion.nullField(overridable);
            }
        }
    }

    @NonNull
    public static <T> Field<List<T>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Deserializer<T, JSONObject> deserializer
    ) {
        List<T> opt = JsonPropertyParser.readOptionalList(context, logger, json, key, deserializer);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, logger, json, key);
            if (reference != null) {
                return new Field.Reference<>(overridable, reference);
            } else if (fallback != null) {
                return FieldKt.clone(fallback, overridable);
            } else {
                return Field.Companion.nullField(overridable);
            }
        }
    }

    @NonNull
    public static <T> Field<List<T>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<T>> fallback,
            @NonNull final Deserializer<T, JSONObject> deserializer,
            @NonNull final ListValidator<T> listValidator
    ) {
        List<T> opt = JsonPropertyParser.readOptionalList(
                context, logger, json, key, deserializer, listValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, logger, json, key);
            if (reference != null) {
                return new Field.Reference<>(overridable, reference);
            } else if (fallback != null) {
                return FieldKt.clone(fallback, overridable);
            } else {
                return Field.Companion.nullField(overridable);
            }
        }
    }

    @NonNull
    public static <R, T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final Function1<R, T> converter
    ) {
        return readExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> listValidator
    ) {
        return readExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final ListValidator<T> listValidator
    ) {
        return readExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        return readExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        ExpressionList<T> opt = JsonExpressionParser.readOptionalExpressionList(
                context, logger, json, key, typeHelper, converter, listValidator, itemValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, logger, json, key);
            if (reference != null) {
                return new Field.Reference<>(overridable, reference);
            } else if (fallback != null) {
                return FieldKt.clone(fallback, overridable);
            } else {
                return Field.Companion.nullField(overridable);
            }
        }
    }

    @NonNull
    public static <R, T> Field<ExpressionList<T>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final Function1<R, T> converter
    ) {
        return readOptionalExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, T> Field<ExpressionList<T>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> listValidator
    ) {
        return readOptionalExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <T> Field<ExpressionList<T>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final ListValidator<T> listValidator
    ) {
        return readOptionalExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <T> Field<ExpressionList<T>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        return readOptionalExpressionListField(
                context, logger, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, T> Field<ExpressionList<T>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<T> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<T>> fallback,
            @NonNull final Function1<R, T> converter,
            @NonNull final ListValidator<T> listValidator,
            @NonNull final ValueValidator<T> itemValidator
    ) {
        ExpressionList<T> opt = JsonExpressionParser.readOptionalExpressionList(
                context, logger, json, key, typeHelper, converter, listValidator, itemValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, logger, json, key);
            if (reference != null) {
                return new Field.Reference<>(overridable, reference);
            } else if (fallback != null) {
                return FieldKt.clone(fallback, overridable);
            } else {
                return Field.Companion.nullField(overridable);
            }
        }
    }

    @Nullable
    public static String readReference(
            @NonNull final ParsingContext context,
            @NonNull final ParsingErrorLogger logger,
            @NonNull final JSONObject json,
            @NonNull final String key
    ) {
        return JsonPropertyParser.readOptional(context, logger, json, '$' + key, IS_NOT_EMPTY);
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static <T> Field<T> referenceOrFallback(
            final boolean overridable,
            @Nullable final String reference,
            @Nullable final Field<T> fallback
    ) {
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        } else if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        } else {
            return overridable ? Field.Companion.nullField(overridable) : null;
        }
    }
}
