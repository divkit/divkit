package com.yandex.div.json

import com.yandex.div.internal.util.JsonArray
import com.yandex.div.internal.util.JsonNode
import com.yandex.div.internal.util.JsonObject
import com.yandex.div.internal.util.summary
import org.json.JSONArray
import org.json.JSONObject

private const val MAX_TO_STRING_LENGTH = 100

class ParsingException internal constructor(
    val reason: ParsingExceptionReason,
    message: String,
    cause: Throwable? = null,
    val source: JsonNode? = null,
    val jsonSummary: String? = null,
) : RuntimeException(message, cause)

fun missingValue(json: JSONObject, key: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VALUE,
        message = "Value for key '$key' is missing",
        source = JsonObject(json),
        jsonSummary = json.summary()
    )
}

fun missingValue(json: JSONArray, key: String, index: Int): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VALUE,
        message = "Value at $index position of '$key' is missing",
        source = JsonArray(json),
        jsonSummary = json.summary()
    )
}

fun missingValue(key: String, path: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_VALUE,
        message = "Value for key '$key' at path '$path' is missing"
    )
}

fun typeMismatch(json: JSONObject, key: String, value: Any): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Value for key '$key' has wrong type ${value.javaClass.name}",
        source = JsonObject(json),
        jsonSummary = json.summary()
    )
}

fun typeMismatch(json: JSONArray, key: String, index: Int, value: Any): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Value at $index position of '$key' has wrong type ${value.javaClass.name}",
        source = JsonArray(json),
        jsonSummary = json.summary()
    )
}

fun typeMismatch(path: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.TYPE_MISMATCH,
        message = "Value at path '$path' has wrong type",
    )
}

fun typeMismatch(
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

fun templateNotFound(json: JSONObject, templateId: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.MISSING_TEMPLATE,
        message = "Template '$templateId' is missing!",
        source = JsonObject(json),
        jsonSummary = json.summary()
    )

}

fun <T> invalidValue(json: JSONObject, key: String, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' is not valid",
        source = JsonObject(json),
        jsonSummary = json.summary()
    )
}

fun invalidValue(
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

private fun Any?.trimLength(): String {
    val fullMessage = this.toString()
    return if (fullMessage.length > MAX_TO_STRING_LENGTH) {
        fullMessage.take(MAX_TO_STRING_LENGTH - 3) + "..."
    } else {
        fullMessage
    }
}


fun <T> invalidValue(json: JSONObject, key: String, value: T, cause: Throwable): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' is not valid",
        cause = cause,
        source = JsonObject(json),
    )
}

fun <T> invalidValue(json: JSONArray, key: String, index: Int, value: T, cause: Throwable): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' at $index position of '$key' is not valid",
        cause = cause,
        source = JsonArray(json),
    )
}

fun <T> invalidValue(json: JSONArray, key: String, index: Int, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' at $index position of '$key' is not valid",
        source = JsonArray(json),
        jsonSummary = json.summary()
    )
}

fun <T> invalidValue(path: String, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' at path '$path' is not valid"
    )
}

fun <T> resolveFailed(key: String, value: T, cause: Throwable? = null): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' could not be resolved",
        cause = cause
    )
}

fun <T> invalidValue(key: String, path: String, value: T): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE,
        message = "Value '${value.trimLength()}' for key '$key' at path '$path' is not valid"
    )
}

fun missingVariable(
    key: String,
    expression: String,
    variableName: String,
    cause: Throwable? = null
) = ParsingException(
    reason = ParsingExceptionReason.MISSING_VARIABLE,
    message = "Undefined variable '$variableName' at \"$key\": \"$expression\"",
    cause = cause
)

fun missingVariable(variableName: String, cause: Throwable? = null) =
    ParsingException(
        reason = ParsingExceptionReason.MISSING_VARIABLE,
        message = "No variable could be resolved for '$variableName",
        cause = cause
    )

fun dependencyFailed(json: JSONObject, key: String, cause: ParsingException): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value for key '$key' is failed to create",
        cause = cause,
        source = JsonObject(json),
        jsonSummary = json.summary()
    )
}

fun dependencyFailed(json: JSONArray, key: String, index: Int, cause: ParsingException): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value at $index position of '$key' is failed to create",
        cause = cause,
        source = JsonArray(json),
        jsonSummary = json.summary()
    )
}

fun dependencyFailed(path: String, cause: ParsingException): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value at path '$path' is failed to create",
        cause = cause
    )
}

fun dependencyFailed(key: String, path: String, cause: ParsingException): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.DEPENDENCY_FAILED,
        message = "Value for key '$key' at path '$path' is failed to create",
        cause = cause
    )
}

fun invalidCondition(message: String, input: String): ParsingException {
    return ParsingException(
        reason = ParsingExceptionReason.INVALID_VALUE, message = message,
        jsonSummary = input
    )
}
