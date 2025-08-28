package com.yandex.div.multiplatform.dependencies

interface ErrorReporter {
    fun report(cardId: String, message: String)
}
