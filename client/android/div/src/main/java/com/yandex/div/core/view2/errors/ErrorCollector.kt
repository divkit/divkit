package com.yandex.div.core.view2.errors

import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div2.DivData

typealias ErrorObserver = (errors: List<Throwable>, warnings: List<Throwable>) -> Unit

@Mockable
internal class ErrorCollector {
    private val observers = mutableSetOf<ErrorObserver>()
    private val runtimeErrors = mutableListOf<Throwable>()
    private var parsingErrors = mutableListOf<Throwable>()
    private var errors = mutableListOf<Throwable>()
    private var warnings = mutableListOf<Throwable>()

    fun logError(e: Throwable) {
        runtimeErrors.add(e)
        rebuildErrorsAndNotify()
    }

    fun logWarning(warning: Throwable) {
        warnings.add(warning)
        rebuildErrorsAndNotify()
    }

    fun getWarnings(): Iterator<Throwable> = warnings.listIterator()

    fun cleanRuntimeWarningsAndErrors() {
        warnings.clear()
        runtimeErrors.clear()
        rebuildErrorsAndNotify()
    }

    private fun rebuildErrorsAndNotify() {
        errors.clear()
        errors.addAll(parsingErrors)
        errors.addAll(runtimeErrors)

        observers.forEach { it(errors, warnings) }
    }

    fun observeAndGet(observer: ErrorObserver): Disposable {
        observers.add(observer)
        observer(errors, warnings)
        return Disposable { observers.remove(observer) }
    }

    fun attachParsingErrors(divData: DivData?) {
        parsingErrors.clear()
        parsingErrors.addAll(divData?.parsingErrors ?: emptyList())
        rebuildErrorsAndNotify()
    }
}
