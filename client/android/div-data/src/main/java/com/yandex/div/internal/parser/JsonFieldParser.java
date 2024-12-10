package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.internal.template.Field;
import com.yandex.div.internal.template.FieldKt;
import com.yandex.div.json.ParsingException;
import com.yandex.div.json.expressions.Expression;
import com.yandex.div.json.expressions.ExpressionList;
import com.yandex.div.serialization.Deserializer;
import com.yandex.div.serialization.ParsingContext;
import kotlin.Lazy;
import kotlin.OptIn;
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
 * <p>
 * NOTE! Please do not change Function1 and Function2 with readable java-interfaces.
 * This will only make parsing slower.
 */
@SuppressWarnings("unused")
@OptIn(markerClass = com.yandex.div.core.annotations.ExperimentalApi.class)
public class JsonFieldParser {

    private static final ValueValidator<String> IS_NOT_EMPTY = value -> !value.isEmpty();

    @NonNull
    public static <V> Field<V> readField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback
    ) {
        return readField(context, json, key, overridable, fallback, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, V> Field<V> readField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readField(context, json, key, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <V> Field<V> readField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final ValueValidator<V> validator
    ) {
        return readField(context, json, key, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R, V> Field<V> readField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        try {
            V value = JsonPropertyParser.read(context, json, key, converter, validator);
            return new Field.Value<>(overridable, value);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, json, key);
            Field<V> referenceOrFallback = referenceOrFallback(overridable, reference, fallback);
            if (referenceOrFallback != null) {
                return referenceOrFallback;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <V> Field<V> readField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        try {
            V result = JsonPropertyParser.read(context, json, key, deserializer);
            return new Field.Value<>(overridable, result);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, json, key);
            Field<V> referenceOrFallback = referenceOrFallback(overridable, reference, fallback);
            if (referenceOrFallback != null) {
                return referenceOrFallback;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <V> Field<V> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback
    ) {
        return readOptionalField(context, json, key, overridable, fallback, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, V> Field<V> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readOptionalField(context, json, key, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <V> Field<V> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final ValueValidator<V> validator
    ) {
        return readOptionalField(context, json, key, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R, V> Field<V> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        V opt = JsonPropertyParser.readOptional(context, json, key, converter, validator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        String reference = readReference(context, json, key);
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        } else if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        } else {
            return Field.Companion.nullField(overridable);
        }
    }

    @NonNull
    public static <V> Field<V> readOptionalField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<V> fallback,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        V opt = JsonPropertyParser.readOptional(context, json, key, deserializer);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        String reference = readReference(context, json, key);
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        } else if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        } else {
            return Field.Companion.nullField(overridable);
        }
    }

    @NonNull
    public static <V> Field<Expression<V>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<V>> fallback
    ) {
        return readFieldWithExpression(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), alwaysValid());
    }

    @NonNull
    public static <R, V> Field<Expression<V>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<V>> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readFieldWithExpression(
                context, json, key, typeHelper, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <V> Field<Expression<V>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<V>> fallback,
            @NonNull final ValueValidator<V> validator
    ) {
        return readFieldWithExpression(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R, V> Field<Expression<V>> readFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        try {
            Expression<V> expression = JsonExpressionParser.readExpression(
                    context, json, key, typeHelper, converter, validator);
            return new Field.Value<>(overridable, expression);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            Field<Expression<V>> referenceOrFallback = referenceOrFallback(
                    overridable,
                    readReference(context, json, key),
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
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<String> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<String>> fallback
    ) {
        return readOptionalFieldWithExpression(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), alwaysValidString());
    }

    @NonNull
    public static <R, V> Field<Expression<V>> readOptionalFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<V>> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readOptionalFieldWithExpression(
                context, json, key, typeHelper, overridable, fallback, converter, alwaysValid());
    }

    @NonNull
    public static <V> Field<Expression<V>> readOptionalFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<V>> fallback,
            @NonNull final ValueValidator<V> validator
    ) {
        return readOptionalFieldWithExpression(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), validator);
    }

    @NonNull
    public static <R, V> Field<Expression<V>> readOptionalFieldWithExpression(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<Expression<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ValueValidator<V> validator
    ) {
        Expression<V> opt = JsonExpressionParser.readOptionalExpression(
                context, json, key, typeHelper, converter, validator, null);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, json, key);
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
    public static <R, V> Field<List<V>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readListField(
                context, json, key, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> Field<List<V>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readListField(
                context, json, key, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <R, V> Field<List<V>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        try {
            List<V> opt = JsonPropertyParser.readList(
                    context, json, key, converter, listValidator, itemValidator);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, json, key);
            Field<List<V>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <V> Field<List<V>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        try {
            List<V> opt = JsonPropertyParser.readList(
                    context, json, key, deserializer);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, json, key);
            Field<List<V>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <V> Field<List<V>> readListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        try {
            List<V> opt = JsonPropertyParser.readList(
                    context, json, key, deserializer, listValidator);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(context, json, key);
            Field<List<V>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <R, V> Field<List<V>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readOptionalListField(
                context, json, key, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> Field<List<V>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readOptionalListField(
                context, json, key, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <V> Field<List<V>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return readOptionalListField(
                context, json, key, overridable, fallback, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, V> Field<List<V>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        List<V> opt = JsonPropertyParser.readOptionalList(
                context, json, key, converter, listValidator, itemValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, json, key);
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
    public static <V> Field<List<V>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer
    ) {
        List<V> opt = JsonPropertyParser.readOptionalList(context, json, key, deserializer);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, json, key);
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
    public static <V> Field<List<V>> readOptionalListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            final boolean overridable,
            @Nullable final Field<List<V>> fallback,
            @NonNull final Lazy<Deserializer<JSONObject, V>> deserializer,
            @NonNull final ListValidator<V> listValidator
    ) {
        List<V> opt = JsonPropertyParser.readOptionalList(
                context, json, key, deserializer, listValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, json, key);
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
    public static <R, V> Field<ExpressionList<V>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readExpressionListField(
                context, json, key, typeHelper, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> Field<ExpressionList<V>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readExpressionListField(
                context, json, key, typeHelper, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <V> Field<ExpressionList<V>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readExpressionListField(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <V> Field<ExpressionList<V>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return readExpressionListField(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, V> Field<ExpressionList<V>> readExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        ExpressionList<V> opt = JsonExpressionParser.readOptionalExpressionList(
                context, json, key, typeHelper, converter, listValidator, itemValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, json, key);
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
    public static <R, V> Field<ExpressionList<V>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final Function1<R, V> converter
    ) {
        return readOptionalExpressionListField(
                context, json, key, typeHelper, overridable, fallback, converter, alwaysValidList(), alwaysValid());
    }

    @NonNull
    public static <R, V> Field<ExpressionList<V>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readOptionalExpressionListField(
                context, json, key, typeHelper, overridable, fallback, converter, listValidator, alwaysValid());
    }

    @NonNull
    public static <V> Field<ExpressionList<V>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final ListValidator<V> listValidator
    ) {
        return readOptionalExpressionListField(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, alwaysValid());
    }

    @NonNull
    public static <V> Field<ExpressionList<V>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        return readOptionalExpressionListField(
                context, json, key, typeHelper, overridable, fallback, doNotConvert(), listValidator, itemValidator);
    }

    @NonNull
    public static <R, V> Field<ExpressionList<V>> readOptionalExpressionListField(
            @NonNull final ParsingContext context,
            @NonNull final JSONObject json,
            @NonNull final String key,
            @NonNull final TypeHelper<V> typeHelper,
            final boolean overridable,
            @Nullable final Field<ExpressionList<V>> fallback,
            @NonNull final Function1<R, V> converter,
            @NonNull final ListValidator<V> listValidator,
            @NonNull final ValueValidator<V> itemValidator
    ) {
        ExpressionList<V> opt = JsonExpressionParser.readOptionalExpressionList(
                context, json, key, typeHelper, converter, listValidator, itemValidator);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(context, json, key);
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
            @NonNull final JSONObject json,
            @NonNull final String key
    ) {
        return JsonPropertyParser.readOptional(context, json, '$' + key, IS_NOT_EMPTY);
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static <V> Field<V> referenceOrFallback(
            final boolean overridable,
            @Nullable final String reference,
            @Nullable final Field<V> fallback
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
