package com.yandex.div.core.view2.errors

import androidx.annotation.AnyThread
import com.yandex.div.DivDataTag
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData

typealias ErrorObserver = (errors: List<Throwable>, warnings: List<Throwable>) -> Unit

@Mockable
@AnyThread
internal class ErrorCollector(
    internal val divData: DivData?,
    private val divDataTag: DivDataTag,
    private val divErrorsReporter: DivErrorsReporter,
) : ParsingErrorLogger {

    private val mutex = Any()

    private val observers = mutableSetOf<ErrorObserver>()
    private val runtimeErrors = mutableListOf<Throwable>()
    private var parsingErrors = emptyList<Throwable>()
    private var warnings = mutableListOf<Throwable>()

    private var errors = mutableListOf<Throwable>()
    private var errorsAreValid = true

    fun logError(e: Throwable): Unit = synchronized(mutex) {
        runtimeErrors.add(e)
        divErrorsReporter.onRuntimeError(divData, divDataTag, e)
        notifyObservers()
    }

    override fun logError(e: Exception) = logError(e as Throwable)

    fun logWarning(warning: Throwable): Unit = synchronized(mutex) {
        warnings.add(warning)
        divErrorsReporter.onWarning(divData, divDataTag, warning)
        notifyObservers()
    }

    fun getWarnings(): Iterable<Throwable> = synchronized(mutex) { warnings.toList() }

    fun cleanRuntimeWarningsAndErrors(): Unit = synchronized(mutex) {
        warnings.clear()
        runtimeErrors.clear()
        notifyObservers()
    }

    private fun notifyObservers(): Unit = synchronized(mutex) {
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

    fun observeAndGet(observer: ErrorObserver): Disposable = synchronized(mutex)  {
        observers.add(observer)
        rebuildErrors()
        observer(errors, warnings)
        return Disposable { observers.remove(observer) }
    }

    fun attachParsingErrors(): Unit = synchronized(mutex)  {
        parsingErrors = divData?.parsingErrors ?: emptyList()
        notifyObservers()
    }
}
