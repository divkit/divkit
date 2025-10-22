package com.yandex.divkit.multiplatform.dependencies

interface ErrorReporter {
    fun report(cardId: String, message: String)
}
