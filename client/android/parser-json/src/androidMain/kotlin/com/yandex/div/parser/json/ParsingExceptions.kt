@file:Suppress("unused")

package com.yandex.div.parser.json

import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject

private const val MAX_LENGTH = 100

internal fun missingValue(json: JsonObject, key: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VALUE,
        message = "Value for key '$key' is missing",
        source = json,
    )
}

internal fun missingValue(json: JsonArray, key: String, index: Int): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VALUE,
        message = "Value at $index position of '$key' is missing",
        source = json,
    )
}

internal fun missingValue(key: String, path: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VALUE,
        message = "Value for key '$key' at path '$path' is missing"
    )
}

internal fun typeMismatch(json: JsonObject, key: String, value: Any): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Value for key '$key' has wrong type ${value.javaClass.name}",
        source = json,
    )
}

internal fun typeMismatch(json: JsonArray, key: String, index: Int, value: Any): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Value at $index position of '$key' has wrong type ${value.javaClass.name}",
        source = json,
    )
}

internal fun typeMismatch(path: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Value at path '$path' has wrong type",
    )
}

internal fun typeMismatch(
    expressionKey: String,
    rawExpression: String,
    wrongTypeValue: Any?,
    cause: Throwable? = null
): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Expression '$expressionKey': '$rawExpression' received value of wrong type: '$wrongTypeValue'",
        cause = cause,
    )
}

internal fun typeMismatch(
    index: Int,
    value: Any
): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Item builder data at $index position has wrong type: ${value.javaClass.name}"
    )
}

internal fun templateNotFound(json: JsonObject, templateId: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_TEMPLATE,
        message = "Template '$templateId' is missing!",
        source = json,
    )

}

internal fun <T> invalidValue(json: JsonObject, key: String, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' is not valid",
        source = json,
    )
}

internal fun invalidValue(
    expressionKey: String,
    rawExpression: String,
    wrongValue: Any?,
    cause: Throwable? = null
): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Field '$expressionKey' with expression '$rawExpression' received wrong value: '$wrongValue'",
        cause = cause,
    )
}

internal fun <T> invalidValue(json: JsonObject, key: String, value: T, cause: Throwable): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' is not valid",
        cause = cause,
        source = json,
    )
}

internal fun <T> invalidValue(json: JsonArray, key: String, index: Int, value: T, cause: Throwable): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' at $index position of '$key' is not valid",
        cause = cause,
        source = json,
    )
}

internal fun <T> invalidValue(json: JsonArray, key: String, index: Int, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' at $index position of '$key' is not valid",
        source = json,
    )
}

internal fun <T> invalidValue(path: String, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' at path '$path' is not valid"
    )
}

internal fun <T> resolveFailed(key: String, value: T, cause: Throwable? = null): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' could not be resolved",
        cause = cause
    )
}

internal fun <T> invalidValue(key: String, path: String, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' at path '$path' is not valid"
    )
}

internal fun missingVariable(
    expression: String,
    variableName: String,
    cause: Throwable? = null
): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VARIABLE,
        message = "Variable '$variableName' is missing. Expression: $expression",
        cause = cause
    )
}

internal fun missingVariable(variableName: String, cause: Throwable? = null): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VARIABLE,
        message = "Variable '$variableName' is missing.",
        cause = cause
    )
}

internal fun dependencyFailed(json: JsonObject, key: String, cause: ParsingException): ParsingException {
    return dependencyFailed(json, key, cause as Exception)
}

internal fun dependencyFailed(json: JsonObject, key: String, cause: Exception): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value for key '$key' is failed to create",
        cause = cause,
        source = json,
    )
}

internal fun dependencyFailed(json: JsonArray, key: String, index: Int, cause: ParsingException): ParsingException {
    return dependencyFailed(json, key, index, cause as Exception)
}

internal fun dependencyFailed(json: JsonArray, key: String, index: Int, cause: Exception): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value at $index position of '$key' is failed to create",
        cause = cause,
        source = json,
    )
}

internal fun dependencyFailed(path: String, cause: ParsingException): ParsingException {
    return dependencyFailed(path, cause as Exception)
}

internal fun dependencyFailed(path: String, cause: Exception): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value at path '$path' is failed to create",
        cause = cause
    )
}

internal fun dependencyFailed(key: String, path: String, cause: ParsingException): ParsingException {
    return dependencyFailed(key, path, cause as Exception)
}

internal fun dependencyFailed(key: String, path: String, cause: Exception): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value for key '$key' at path '$path' is failed to create",
        cause = cause
    )
}

internal fun suppressMissingValueOrThrow(exception: ParsingException) {
    if (exception.reason == ParsingExceptionReason.MISSING_VALUE) {
        return
    }
    throw exception
}

private fun Any?.trimLength(): String {
    val fullMessage = this.toString()
    return if (fullMessage.length > MAX_LENGTH) {
        fullMessage.take(MAX_LENGTH - 3) + "..."
    } else {
        fullMessage
    }
}
