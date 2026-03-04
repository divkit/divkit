package com.yandex.div.internal

fun interface AssertionErrorHandler {

    fun handleError(assertionError: AssertionError)
}
