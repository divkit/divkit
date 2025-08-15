package com.yandex.div.core.expression

import com.yandex.div.json.ParsingException
import com.yandex.div.json.ParsingExceptionReason

internal inline fun suppressExpressionErrors(block: () -> Unit) {
    try {
        block()
    } catch (e: ParsingException) {
        if (e.isExpressionResolveFail()) {
            return
        }

        throw e
    }
}

private fun ParsingException.isExpressionResolveFail(): Boolean {
    return this.reason == ParsingExceptionReason.MISSING_VARIABLE ||
            this.reason == ParsingExceptionReason.INVALID_VALUE ||
            this.reason == ParsingExceptionReason.TYPE_MISMATCH
}
