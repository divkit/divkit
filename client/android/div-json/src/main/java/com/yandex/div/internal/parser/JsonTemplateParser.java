package com.yandex.div.internal.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.json.expressions.Expression;
import com.yandex.div.json.expressions.ExpressionList;
import com.yandex.div.internal.template.Field;
import com.yandex.div.internal.template.FieldKt;
import com.yandex.div.json.ParsingEnvironment;
import com.yandex.div.json.ParsingErrorLogger;
import com.yandex.div.json.ParsingException;
import kotlin.PublishedApi;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import org.json.JSONObject;

import java.util.List;

import static com.yandex.div.internal.parser.JsonParser.alwaysValid;
import static com.yandex.div.internal.parser.JsonParser.alwaysValidList;
import static com.yandex.div.internal.parser.JsonParser.alwaysValidString;
import static com.yandex.div.internal.parser.JsonParser.doNotConvert;
import static com.yandex.div.internal.parser.JsonTemplateParserKt.suppressMissingValueOrThrow;

/**
 * A Java-version of JsonTemplateParser.kt that is faster because it generates less garbage during parsing
 * and skips unnecessary checks (like nullability).
 *
 * NOTE! Please do not change Function1 and Function2 with readable java-interfaces.
 * This will only make parsing slower.
 */
public class JsonTemplateParser {
    private static final ValueValidator<String> IS_NOT_EMPTY = value -> !value.isEmpty();

    @NonNull
    public static <T> Field<T> readOptionalField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readOptionalField(json, key, overridable, fallback, doNotConvert(), alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> Field<T> readOptionalField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull ValueValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readOptionalField(json, key, overridable, fallback, doNotConvert(), validator, logger, env);
    }

    @NonNull
    public static <R, T> Field<T> readOptionalField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readOptionalField(json, key, overridable, fallback, converter, alwaysValid(), logger, env);
    }

    @NonNull
    public static <R, T> Field<T> readOptionalField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ValueValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        T opt = JsonParser.readOptional(json, key, converter, validator, logger, env);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<T> readOptionalReferenceField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        String reference = readReference(json, key, logger, env);
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<T> readOptionalField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readOptionalField(json, key, overridable, fallback, creator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> Field<T> readOptionalField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull ValueValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        T opt = JsonParser.readOptional(json, key, creator, validator, logger, env);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<T> readOptionalFallbackField(
            boolean overridable,
            @Nullable Field<T> fallback) {
        if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<T> readField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readField(json, key, overridable, fallback, doNotConvert(), alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> Field<T> readField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull ValueValidator<T> valueValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readField(json, key, overridable, fallback, doNotConvert(), valueValidator, logger, env);
    }

    @NonNull
    public static <R, T> Field<T> readField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readField(json, key, overridable, fallback, converter, alwaysValid(), logger, env);

    }

    @NonNull
    public static <R, T> Field<T> readField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ValueValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        try {
            T value = JsonParser.read(json, key, converter, validator, logger, env);
            return new Field.Value<>(overridable, value);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(json, key, logger, env);
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
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readField(json, key, overridable, fallback, creator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> Field<T> readField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<T> fallback,
            @NonNull Function2<ParsingEnvironment, JSONObject, T> creator,
            @NonNull ValueValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        try {
            T result = JsonParser.read(json, key, creator, validator, logger, env);
            return new Field.Value<>(overridable, result);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(json, key, logger, env);
            Field<T> referenceOrFallback = referenceOrFallback(overridable, reference, fallback);
            if (referenceOrFallback != null) {
                return referenceOrFallback;
            } else {
                throw e;
            }
        }
    }

    @PublishedApi
    @Nullable
    public static String readReference(@NonNull JSONObject json,
                                       @NonNull String key,
                                       @NonNull ParsingErrorLogger logger,
                                       @NonNull ParsingEnvironment env) {
        return JsonParser.readOptional(json, '$' + key, IS_NOT_EMPTY, logger, env);
    }

    @NonNull
    public static <R, T> Field<Expression<T>> readOptionalFieldWithExpression(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<Expression<T>> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull TypeHelper<T> typeHelper) {
        return readOptionalFieldWithExpression(
                json, key, overridable, fallback, converter, alwaysValid(), logger, env, typeHelper);
    }

    @NonNull
    public static Field<Expression<String>> readOptionalFieldWithExpression(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<Expression<String>> fallback,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull TypeHelper<String> typeHelper) {
        return readOptionalFieldWithExpression(
                json, key, overridable, fallback, doNotConvert(), alwaysValidString(), logger, env, typeHelper);
    }

    @NonNull
    public static <T> Field<Expression<T>> readOptionalFieldWithExpression(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<Expression<T>> fallback,
            @NonNull ValueValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull TypeHelper<T> typeHelper) {
        return readOptionalFieldWithExpression(
                json, key, overridable, fallback, doNotConvert(), validator, logger, env, typeHelper);
    }

    @NonNull
    public static <R,T> Field<Expression<T>> readOptionalFieldWithExpression(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<Expression<T>> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ValueValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull TypeHelper<T> typeHelper) {
        Expression<T> opt = JsonParser.readOptionalExpression(json, key, converter, validator, logger, env, null, typeHelper);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<Expression<T>> readOptionalReferenceFieldWithExpression(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        String reference = readReference(json, key, logger, env);
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<Expression<T>> readOptionalFallbackFieldWithExpression(
            boolean overridable,
            @Nullable Field<Expression<T>> fallback) {
        if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        } else {
            return Field.Companion.nullField(overridable);
        }
    }

    @NonNull
    public static <R, T> Field<List<T>> readListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function1<R,T> converter,
            @NonNull ListValidator<T> listValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        try {
            List<T> opt = JsonParser.readList(json, key, converter, listValidator, alwaysValid(), logger, env);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(json, key, logger, env);
            Field<List<T>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @NonNull
    public static <R, T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<ExpressionList<T>> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ListValidator<T> listValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        ExpressionList<T> opt = JsonParser.readOptionalExpressionList(json, key, converter, listValidator, alwaysValid(), logger, env, typeHelper);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(json, key, logger, env);
            if (reference != null) {
                return new Field.Reference<>(overridable, reference);
            } else if (fallback != null) {
                return FieldKt.clone(fallback, overridable);
            } else {
                return Field.Companion.nullField(overridable);
            }
        }
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<ExpressionList<T>> fallback,
            @NonNull ListValidator<T> listValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readExpressionListField(json, key, overridable, fallback, doNotConvert(), listValidator, logger, env,
                                        typeHelper);
    }

    @NonNull
    public static <T> Field<ExpressionList<T>> readExpressionListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<ExpressionList<T>> fallback,
            @NonNull ListValidator<T> listValidator,
            @NonNull ValueValidator<T> itemValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        ExpressionList<T> opt = JsonParser.readOptionalExpressionList(json, key, doNotConvert(),
                listValidator, itemValidator, logger, env, typeHelper);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(json, key, logger, env);
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
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readOptionalListField(
                json, key, overridable, fallback, converter, alwaysValidList(), alwaysValid(), logger, env);
    }

    @NonNull
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ListValidator<T> listValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readOptionalListField(
                json, key, overridable, fallback, converter, listValidator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ListValidator<T> listValidator,
            @NonNull ValueValidator<T> itemValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        List<T> opt = JsonParser.readOptionalList(json, key, converter, listValidator, itemValidator, logger, env);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<List<T>> readOptionalListReferenceField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        String reference = readReference(json, key, logger, env);
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<List<T>> readOptionalListFallbackField(
            boolean overridable,
            @Nullable Field<List<T>> fallback) {
        if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function2<ParsingEnvironment, R, T> creator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readOptionalListField(json, key, overridable, fallback, creator, alwaysValidList(), logger, env);
    }

    @NonNull
    public static <R, T> Field<List<T>> readOptionalListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function2<ParsingEnvironment, R, T> creator,
            @NonNull ListValidator<T> validator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        List<T> opt = JsonParser.readOptionalList(json, key, creator, validator, logger, env);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        }
        return Field.Companion.nullField(overridable);
    }

    @NonNull
    public static <T> Field<List<T>> readOptionalListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull ListValidator<T> validator,
            @NonNull ValueValidator<T> itemValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        List<T> opt = JsonParser.readOptionalList(json, key, doNotConvert(), validator, itemValidator, logger, env);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(json, key, logger, env);
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
    @SuppressWarnings("unused")
    public static <R, T> Field<ExpressionList<T>> readOptionalExpressionListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<ExpressionList<T>> fallback,
            @NonNull Function1<R, T> converter,
            @NonNull ListValidator<T> listValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        return readExpressionListField(json, key, overridable, fallback, converter, listValidator,
                logger, env, typeHelper);
    }

    @NonNull
    @SuppressWarnings("unused")
    public static <T> Field<ExpressionList<T>> readOptionalExpressionListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<ExpressionList<T>> fallback,
            @NonNull ListValidator<T> listValidator,
            @NonNull ValueValidator<T> itemValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env,
            @NonNull final TypeHelper<T> typeHelper) {
        ExpressionList<T> opt = JsonParser.readOptionalExpressionList(json, key, doNotConvert(),
                listValidator, itemValidator, logger, env, typeHelper);
        if (opt != null) {
            return new Field.Value<>(overridable, opt);
        } else {
            String reference = readReference(json, key, logger, env);
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
    public static <T> Field<List<T>> readListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function2<ParsingEnvironment, JSONObject,T> creator,
            @NonNull ListValidator<T> listValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        return readListField(json, key, overridable, fallback, creator, listValidator, alwaysValid(), logger, env);
    }

    @NonNull
    public static <T> Field<List<T>> readListField(
            @NonNull JSONObject json,
            @NonNull String key,
            boolean overridable,
            @Nullable Field<List<T>> fallback,
            @NonNull Function2<ParsingEnvironment, JSONObject,T> creator,
            @NonNull ListValidator<T> listValidator,
            @NonNull ValueValidator<T> itemValidator,
            @NonNull ParsingErrorLogger logger,
            @NonNull ParsingEnvironment env) {
        try {
            List<T> opt = JsonParser.readList(json, key, creator, listValidator, itemValidator, logger, env);
            return new Field.Value<>(overridable, opt);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            String reference = readReference(json, key, logger, env);
            Field<List<T>> result = referenceOrFallback(overridable, reference, fallback);
            if (result != null) {
                return result;
            } else {
                throw e;
            }
        }
    }

    @PublishedApi
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static <T> Field<T> referenceOrFallback(
            boolean overridable,
            @Nullable String reference,
            @Nullable Field<T> fallback) {
        if (reference != null) {
            return new Field.Reference<>(overridable, reference);
        } else if (fallback != null) {
            return FieldKt.clone(fallback, overridable);
        } else {
            return overridable ? Field.Companion.nullField(overridable) : null;
        }
    }

    @NonNull
    public static <R, T> Field<Expression<T>> readFieldWithExpression(@NonNull JSONObject jsonObject,
                                                                      @NonNull String key,
                                                                      boolean overridable,
                                                                      @Nullable Field<Expression<T>> fallback,
                                                                      @NonNull Function1<R, T> converter,
                                                                      @NonNull ParsingErrorLogger logger,
                                                                      @NonNull ParsingEnvironment env,
                                                                      @NonNull final TypeHelper<T> typeHelper) {
        return readFieldWithExpression(jsonObject, key, overridable, fallback, converter, alwaysValid(), logger, env, typeHelper);
    }

    @NonNull
    public static <T> Field<Expression<T>> readFieldWithExpression(@NonNull JSONObject jsonObject,
                                                                   @NonNull String key,
                                                                   boolean overridable,
                                                                   @Nullable Field<Expression<T>> fallback,
                                                                   @NonNull ParsingErrorLogger logger,
                                                                   @NonNull ParsingEnvironment env,
                                                                   @NonNull final TypeHelper<T> typeHelper) {
        return readFieldWithExpression(jsonObject, key, overridable, fallback, doNotConvert(), alwaysValid(), logger,
                                       env, typeHelper);
    }

    @NonNull
    public static <T> Field<Expression<T>> readFieldWithExpression(@NonNull JSONObject jsonObject,
                                                                   @NonNull String key,
                                                                   boolean overridable,
                                                                   @Nullable Field<Expression<T>> fallback,
                                                                   @NonNull ValueValidator<T> validator,
                                                                   @NonNull ParsingErrorLogger logger,
                                                                   @NonNull ParsingEnvironment env,
                                                                   @NonNull final TypeHelper<T> typeHelper) {
        return readFieldWithExpression(jsonObject, key, overridable, fallback, doNotConvert(), validator, logger, env, typeHelper);
    }

    @NonNull
    public static <R, T> Field<Expression<T>> readFieldWithExpression(@NonNull JSONObject jsonObject,
                                                                      @NonNull String key,
                                                                      boolean overridable,
                                                                      @Nullable Field<Expression<T>> fallback,
                                                                      @NonNull Function1<R, T> converter,
                                                                      @NonNull ValueValidator<T> validator,
                                                                      @NonNull ParsingErrorLogger logger,
                                                                      @NonNull ParsingEnvironment env,
                                                                      @NonNull final TypeHelper<T> typeHelper) {
        try {
            Expression<T> expression = JsonParser.readExpression(jsonObject, key, converter, validator, logger, env,
                                                                 typeHelper);
            return new Field.Value<>(overridable, expression);
        } catch (ParsingException e) {
            suppressMissingValueOrThrow(e);
            Field<Expression<T>> referenceOrFallback = referenceOrFallback(
                    overridable,
                    readReference(jsonObject, key, logger, env),
                    fallback);
            if (referenceOrFallback != null) {
                return referenceOrFallback;
            } else {
                throw e;
            }
        }
    }
}
