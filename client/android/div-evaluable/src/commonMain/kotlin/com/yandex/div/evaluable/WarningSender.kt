package com.yandex.div.evaluable

public fun interface WarningSender {
    public fun send(expressionContext: ExpressionContext, message: String)
}
