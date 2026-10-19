package com.yandex.div.parser.json

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
public enum class ParsingExceptionReason {
    MISSING_TEMPLATE,
    MISSING_VALUE,
    MISSING_VARIABLE,
    TYPE_MISMATCH,
    INVALID_VALUE,
    DEPENDENCY_FAILED,
}
