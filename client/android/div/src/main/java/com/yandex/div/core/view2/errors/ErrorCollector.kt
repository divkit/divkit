package com.yandex.div.core.view2.errors

import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div2.DivData

typealias ErrorObserver = (errors: List<Throwable>) -> Unit

@Mockable
internal class ErrorCollector {
    private val observers = mutableSetOf<ErrorObserver>()
    private val runtimeErrors = mutableListOf<Throwable>()
    private var parsingErrors = mutableListOf<Throwable>()
    private var errors = mutableListOf<Throwable>()

    fun logError(e: Throwable) {
        runtimeErrors.add(e)
        rebuildErrorsAndNotify()
    }

    private fun rebuildErrorsAndNotify() {
        errors.clear()
        errors.addAll(parsingErrors)
        errors.addAll(runtimeErrors)

        observers.forEach { it(errors) }
    }

    fun observeAndGet(observer: ErrorObserver): Disposable {
        observers.add(observer)
        observer(errors)
        return Disposable { observers.remove(observer) }
    }

    fun attachParsingErrors(divData: DivData?) {
        parsingErrors.clear()
        parsingErrors.addAll(divData?.parsingErrors ?: emptyList())
        rebuildErrorsAndNotify()
    }
}
