package com.yandex.div.core.view2.errors

import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div2.DivData

typealias ErrorObserver = (errors: List<Throwable>, warnings: List<Throwable>) -> Unit

@Mockable
internal class ErrorCollector {
    private val observers = mutableSetOf<ErrorObserver>()
    private val runtimeErrors = mutableListOf<Throwable>()
    private var parsingErrors = emptyList<Throwable>()
    private var warnings = mutableListOf<Throwable>()

    private var errors = mutableListOf<Throwable>()
    private var errorsAreValid = true

    fun logError(e: Throwable) {
        runtimeErrors.add(e)
        notifyObservers()
    }

    fun logWarning(warning: Throwable) {
        warnings.add(warning)
        notifyObservers()
    }

    fun getWarnings(): Iterator<Throwable> = warnings.listIterator()

    fun cleanRuntimeWarningsAndErrors() {
        warnings.clear()
        runtimeErrors.clear()
        notifyObservers()
    }

    private fun notifyObservers() {
        errorsAreValid = false
        if (observers.isEmpty()) return
        rebuildErrors()
        observers.forEach { it(errors, warnings) }
    }

    private fun rebuildErrors() {
        if (errorsAreValid) return
        errors.clear()
        errors.addAll(parsingErrors)
        errors.addAll(runtimeErrors)
        errorsAreValid = true
    }

    fun observeAndGet(observer: ErrorObserver): Disposable {
        observers.add(observer)
        rebuildErrors()
        observer(errors, warnings)
        return Disposable { observers.remove(observer) }
    }

    fun attachParsingErrors(divData: DivData?) {
        parsingErrors = divData?.parsingErrors ?: emptyList()
        notifyObservers()
    }
}
