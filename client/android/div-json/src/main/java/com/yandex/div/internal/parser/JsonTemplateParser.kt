package com.yandex.div.internal.parser

import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.internal.parser.JsonParser.doNotConvert
import com.yandex.div.internal.template.Field
import com.yandex.div.internal.template.clone
import com.yandex.div.json.JSONSerializable
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.ParsingExceptionReason
import org.json.JSONObject

fun <T : Any> JSONObject.readField(
    key: String,
    overridable: Boolean,
    fallback: Field<T>?,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<T> {
    try {
        read(key = key, validator = validator, logger = logger, env = env).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

inline fun <reified R, reified T : Any> JSONObject.readField(
    key: String,
    overridable: Boolean,
    fallback: Field<T>?,
    converter: Converter<R, T?>,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<T> {
    try {
        read(key = key, converter = converter, validator = validator, logger = logger, env = env).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}


@JvmName("readSerializableField")
fun <T : JSONSerializable> JSONObject.readField(
    key: String,
    overridable: Boolean,
    fallback: Field<T>?,
    creator: Creator<JSONObject, T>,
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<T> {
    try {
        read(key = key, creator = creator, logger = logger, env = env).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

fun <T : Any> JSONObject.readOptionalField(
    key: String,
    overridable: Boolean,
    fallback: Field<T>?,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<T> {
    readOptional(key = key, validator = validator, logger = logger, env = env)?.let {
        return Field.Value(overridable, it)
    }

    readReference(key = key, logger = logger, env = env)?.let {
        return Field.Reference(overridable, it)
    }

    fallback?.let {
        return it.clone(overridable)
    }

    return Field.nullField(overridable)
}

fun <R, T : Any> JSONObject.readOptionalField(
    key: String,
    overridable: Boolean,
    fallback: Field<T>?,
    converter: Converter<R, T?>,
    validator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<T> {
    readOptional(key = key, converter = converter, validator = validator, logger = logger, env = env)?.let {
        return Field.Value(overridable, it)
    }

    readReference(key = key, logger = logger, env = env)?.let {
        return Field.Reference(overridable, it)
    }

    fallback?.let {
        return it.clone(overridable)
    }

    return Field.nullField(overridable)
}

@JvmName("readSerializableOptionalField")
fun <T : JSONSerializable> JSONObject.readOptionalField(
    key: String,
    overridable: Boolean,
    fallback: Field<T>?,
    creator: Creator<JSONObject, T>,
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<T> {
    readOptional(key = key, creator = creator, logger = logger, env = env)?.let {
        return Field.Value(overridable, it)
    }

    readReference(key = key, logger = logger, env = env)?.let {
        return Field.Reference(overridable, it)
    }

    fallback?.let {
        return it.clone(overridable)
    }

    return Field.nullField(overridable)
}

fun <T : Any> JSONObject.readListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    try {
        readList(
            key = key,
            validator = validator,
            itemValidator = itemValidator,
            logger = logger,
            env = env,
        ).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

fun <R, T : Any> JSONObject.readListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    converter: Converter<R, T?>,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    try {
        readList(
            key = key,
            converter = converter,
            validator = validator,
            itemValidator = itemValidator,
            logger = logger,
            env = env,
        ).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

@JvmName("readSerializableListField")
fun <T : JSONSerializable> JSONObject.readListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    creator: Creator<JSONObject, T>,
    validator: ListValidator<T> = ListValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    try {
        readList(
            key = key,
            creator = creator,
            validator = validator,
            logger = logger,
            env = env,
        ).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

fun <T : Any> JSONObject.readOptionalListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    readOptionalList(
        key = key,
        validator = validator,
        itemValidator = itemValidator,
        logger = logger
    )?.let {
        return Field.Value(overridable, it)
    }

    readReference(key = key, logger = logger, env = env)?.let {
        return Field.Reference(overridable, it)
    }

    fallback?.let {
        return it.clone(overridable)
    }

    return Field.nullField(overridable)
}

fun <R, T : Any> JSONObject.readOptionalListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    converter: Converter<R, T?>,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    readOptionalList(
        key = key,
        converter = converter,
        validator = validator,
        itemValidator = itemValidator,
        logger = logger
    )?.let {
        return Field.Value(overridable, it)
    }

    readReference(key = key, logger = logger, env = env)?.let {
        return Field.Reference(overridable, it)
    }

    fallback?.let {
        return it.clone(overridable)
    }

    return Field.nullField(overridable)
}

@JvmName("readSerializableOptionalListField")
fun <T : JSONSerializable> JSONObject.readOptionalListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    creator: Creator<JSONObject, T>,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    readOptionalList(
        key = key,
        creator = creator,
        validator = validator,
        itemValidator = itemValidator,
        logger = logger,
        env = env,
    )?.let {
        return Field.Value(overridable, it)
    }

    readReference(key = key, logger = logger, env)?.let {
        return Field.Reference(overridable, it)
    }

    fallback?.let {
        return it.clone(overridable)
    }

    return Field.nullField(overridable)
}

fun <T : Any> JSONObject.readStrictListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    try {
        readStrictList(
            key = key,
            validator = validator,
            itemValidator = itemValidator,
            logger = logger
        ).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

fun <R, T : Any> JSONObject.readStrictListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    converter: Converter<R, T?>,
    validator: ListValidator<T> = ListValidator { true },
    itemValidator: ValueValidator<T> = ValueValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    try {
        readStrictList(
            key = key,
            converter = converter,
            validator = validator,
            itemValidator = itemValidator,
            logger = logger
        ).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

@JvmName("readStrictSerializableListField")
fun <T : JSONSerializable> JSONObject.readStrictListField(
    key: String,
    overridable: Boolean,
    fallback: Field<List<T>>?,
    creator: Creator<JSONObject, T>,
    validator: ListValidator<T> = ListValidator { true },
    logger: ParsingErrorLogger,
    env: ParsingEnvironment,
): Field<List<T>> {
    try {
        readStrictList(
            key = key,
            creator = creator,
            validator = validator,
            logger = logger,
            env = env,
        ).let {
            return Field.Value(overridable, it)
        }
    } catch (e: ParsingException) {
        suppressMissingValueOrThrow(e)
        val reference = readReference(key = key, logger = logger, env = env)
        return referenceOrFallback(overridable, reference, fallback) ?: throw e
    }
}

fun suppressMissingValueOrThrow(e: ParsingException) {
    if (e.reason == ParsingExceptionReason.MISSING_VALUE) {
        return
    }

    throw e
}

@PublishedApi
internal fun JSONObject.readReference(key: String, logger: ParsingErrorLogger, env: ParsingEnvironment): String? {
    return readOptional(key = "$$key", validator = { it.isNotEmpty() }, logger = logger, env = env )
}

@PublishedApi
internal fun <T> referenceOrFallback(
    overridable: Boolean,
    reference: String?,
    fallback: Field<T>?,
): Field<T>? {
    if (reference != null) return Field.Reference(overridable, reference)
    if (fallback != null) return fallback.clone(overridable)
    if (overridable) return Field.nullField(overridable)
    return null
}

fun <T : Any> JSONObject.writeField(
    key: String,
    field: Field<T>?,
    converter: Converter<T, Any> = { it }
) {
    when (field) {
        is Field.Value -> write(key, converter(field.value))
        is Field.Reference -> write("$$key", field.reference)
        else -> Unit
    }
}

fun <T: Any, R> JSONObject.writeFieldWithExpression(
    key: String,
    field: Field<Expression<T>>?,
    converter: Converter<T, R>,
) {
    when (field) {
        is Field.Value -> writeExpression(key, field.value, converter)
        is Field.Reference -> write("$$key", field.reference)
        else -> Unit
    }
}

@JvmName("writeSerializableField")
fun <T : JSONSerializable> JSONObject.writeField(
    key: String,
    field: Field<T>?
) {
    when (field) {
        is Field.Value -> write(key, field.value.writeToJSON())
        is Field.Reference -> write("$$key", field.reference)
        else -> Unit
    }
}

fun <T: Any> JSONObject.writeFieldWithExpression(
    key: String,
    field: Field<Expression<T>>?
) {
    when (field) {
        is Field.Value -> writeExpression(key, field.value)
        is Field.Reference -> write("$$key", field.reference)
        else -> Unit
    }
}

fun <T: Any> JSONObject.writeExpressionListField(
    key: String,
    field: Field<ExpressionList<T>>?,
    converter: Converter<T, Any>,
) {
    when (field) {
        is Field.Value -> writeExpressionList(key, field.value, converter)
        is Field.Reference -> write("$$key", field.reference)
        else -> Unit
    }
}

@Suppress("unused")
fun <T: Any> JSONObject.writeExpressionListField(
    key: String,
    field: Field<ExpressionList<T>>?,
) {
    writeExpressionListField(key, field, doNotConvert())
}

@JvmName("writeListField")
fun <T : Any> JSONObject.writeField(
    key: String,
    field: Field<List<T>>?
) {
    when (field) {
        is Field.Value -> write(key, field.value)
        is Field.Reference -> write("$$key", field.reference)
        else -> Unit
    }
}

@JvmName("writeListField")
fun <T : Any> JSONObject.writeField(
    key: String,
    field: Field<List<T>>?,
    converter: Converter<T, Any>
) {
    when (field) {
        is Field.Value -> write(key, field.value, converter)
        is Field.Reference -> write("$$key", field.reference)
        else -> Unit
    }
}
