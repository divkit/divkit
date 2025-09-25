package com.yandex.div.core.view2.errors

import com.yandex.div.DivDataTag
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.annotations.Mockable
import com.yandex.div2.DivData

typealias ErrorObserver = (errors: List<Throwable>, warnings: List<Throwable>) -> Unit

@Mockable
internal class ErrorCollector(
    internal val divData: DivData?,
    private val divDataTag: DivDataTag,
    private val divErrorsReporter: DivErrorsReporter,
) {
    private val observers = mutableSetOf<ErrorObserver>()
    private val runtimeErrors = mutableListOf<Throwable>()
    private var parsingErrors = emptyList<Throwable>()
    private var warnings = mutableListOf<Throwable>()

    private var errors = mutableListOf<Throwable>()
    private var errorsAreValid = true

    fun logError(e: Throwable) {
        runtimeErrors.add(e)
        divErrorsReporter.onRuntimeError(divData, divDataTag, e)
        notifyObservers()
    }

    fun logWarning(warning: Throwable) {
        warnings.add(warning)
        divErrorsReporter.onWarning(divData, divDataTag, warning)
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

    fun attachParsingErrors() {
        parsingErrors = divData?.parsingErrors ?: emptyList()
        notifyObservers()
    }
}
