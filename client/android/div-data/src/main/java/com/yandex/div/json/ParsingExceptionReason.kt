package com.yandex.div.json

enum class ParsingExceptionReason {
    MISSING_TEMPLATE,
    MISSING_VALUE,
    MISSING_VARIABLE,
    TYPE_MISMATCH,
    INVALID_VALUE,
    DEPENDENCY_FAILED,
}
