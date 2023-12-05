package com.yandex.div.evaluable

fun interface WarningSender {
    fun send(expressionContext: ExpressionContext, message: String)
}
