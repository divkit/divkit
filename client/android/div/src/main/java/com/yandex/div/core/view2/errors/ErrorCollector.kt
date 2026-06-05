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

    fun logError(e: Throwable) {
        val snapshot = takeSnapshot {
            runtimeErrors.add(e)
            divErrorsReporter.onRuntimeError(divData, divDataTag, e)
        }
        snapshot.notifyObservers()
    }

    override fun logError(e: Exception) = logError(e as Throwable)

    fun logWarning(warning: Throwable) {
        val snapshot = takeSnapshot {
            warnings.add(warning)
            divErrorsReporter.onWarning(divData, divDataTag, warning)
        }
        snapshot.notifyObservers()
    }

    fun getWarnings(): Iterable<Throwable> = synchronized(mutex) { warnings.toList() }

    fun cleanRuntimeWarningsAndErrors() {
        val snapshot = takeSnapshot {
            warnings.clear()
            runtimeErrors.clear()
        }
        snapshot.notifyObservers()
    }

    fun observeAndGet(observer: ErrorObserver): Disposable {
        synchronized(mutex)  {
            observers.add(observer)
            rebuildErrors()
        }

        observer.invoke(errors, warnings)
        return Disposable {
            synchronized(mutex) {
                observers.remove(observer)
            }
        }
    }

    fun attachParsingErrors() {
        val snapshot = takeSnapshot {
            parsingErrors = divData?.parsingErrors ?: emptyList()
        }
        snapshot.notifyObservers()
    }

    private inline fun takeSnapshot(block: () -> Unit): StateSnapshot = synchronized(mutex) {
        block()

        errorsAreValid = false

        if (observers.isEmpty()) {
            return StateSnapshot.EMPTY
        }
        rebuildErrors()

        return StateSnapshot(
            errors = errors.toList(),
            warnings = warnings.toList(),
            observers = observers.toList()
        )
    }

    private fun rebuildErrors() {
        if (errorsAreValid) return
        errors.clear()
        errors.addAll(parsingErrors)
        errors.addAll(runtimeErrors)
        errorsAreValid = true
    }

    private class StateSnapshot(
        private val errors: List<Throwable>,
        private val warnings: List<Throwable>,
        private val observers: List<ErrorObserver>,
    ) {

        fun notifyObservers() {
            observers.forEach { observer ->
                observer.invoke(errors, warnings)
            }
        }

        companion object {
            @JvmField
            val EMPTY = StateSnapshot(emptyList(), emptyList(), emptyList())
        }
    }
}
