package com.yandex.div.core.view2.errors

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.TransactionTooLargeException
import android.view.ViewGroup
import android.widget.Toast
import com.yandex.div.core.Disposable
import com.yandex.div.core.actions.logError
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.PERMANENT_DEBUG_PANEL_ENABLED
import com.yandex.div.core.experiments.Experiment.VISUAL_ERRORS_ENABLED
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.Binding
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewBindingProvider
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.internal.Assert
import com.yandex.div.json.ParsingException
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import com.yandex.div.DivDataTag

private const val SHOW_LIMIT = 25
private const val MIN_SIZE_FOR_DETAILS_DP = 150

@DivViewScope
internal class ErrorVisualMonitor @Inject constructor(
    errorCollectors: ErrorCollectors,
    divView: Div2View,
    @ExperimentFlag(VISUAL_ERRORS_ENABLED) private val visualErrorsEnabled: Boolean,
    @ExperimentFlag(PERMANENT_DEBUG_PANEL_ENABLED) private val showPermanently: Boolean,
    private val bindingProvider: ViewBindingProvider,
) {
    internal var enabled = visualErrorsEnabled || showPermanently
        set(value) {
            field = value
            connectOrDisconnect()
        }

    private val errorModel = ErrorModel(errorCollectors, divView, visualErrorsEnabled)
    private var lastConnectionView: ViewGroup? = null
    private var errorView: ErrorView? = null

    init {
        connectOrDisconnect()
    }

    private fun connectOrDisconnect() {
        if (enabled) {
            bindingProvider.observeAndGet { errorModel.bind(it) }
            lastConnectionView?.let {
                connect(it)
            }
        } else {
            errorView?.close()
            errorView = null
        }
    }

    fun connect(root: ViewGroup) {
        lastConnectionView = root
        if (!enabled) {
            return
        }
        errorView?.close()
        errorView = ErrorView(root, errorModel, showPermanently)
    }
}

internal class ErrorModel(
    private val errorCollectors: ErrorCollectors,
    private val div2View: Div2View,
    private val visualErrorsEnabled: Boolean
    ) {

    private var dataTag: DivDataTag? = null

    fun bind(binding: Binding) {
            dataTag = binding.tag
            existingSubscription?.close()
            existingSubscription = errorCollectors
                .getOrCreate(binding.tag, binding.data)
                .observeAndGet(updateOnErrors)
        }

    private val observers = mutableSetOf<(ErrorViewModel) -> Unit>()
    private val currentErrors = mutableListOf<Throwable>()
    private val currentWarnings = mutableListOf<Throwable>()
    private val logcatErrorDumper = LogcatErrorDumper()
    private var existingSubscription: Disposable? = null

    private val updateOnErrors = { errors: List<Throwable>, warnings: List<Throwable> ->
        if (visualErrorsEnabled) {
            this.currentErrors.apply {
                clear()
                val reversedErrors = errors.toMutableList()
                reversedErrors.reverse()
                addAll(reversedErrors)
            }
            this.currentWarnings.apply {
                clear()
                val reversedWarnings = warnings.toMutableList()
                reversedWarnings.reverse()
                addAll(reversedWarnings)
            }
            state = state.copy(errorCount = currentErrors.size, errorDetails = errorsToDetails(currentErrors),
                warningCount = currentWarnings.size, warningDetails = warningsToDetails(currentWarnings))
            logcatErrorDumper.logErrors(currentErrors, currentWarnings, dataTag)
        }
    }

    private fun errorsToDetails(errors: List<Throwable>): String {
        val errorsList = errors.take(SHOW_LIMIT).joinToString(separator = "\n") {
            if (it is ParsingException) {
                " - ${it.reason}: ${it.fullStackMessage}"
            } else {
                " - ${it.fullStackMessage}"
            }
        }

        return "Last $SHOW_LIMIT errors:\n$errorsList"
    }

    private fun warningsToDetails(currentWarnings: List<Throwable>): String {
        val warningsList = currentWarnings.take(SHOW_LIMIT)
            .joinToString(separator = "\n") { " - ${it.fullStackMessage}" }
        return "Last $SHOW_LIMIT warnings:\n$warningsList"
    }

    fun observeAndGet(observer: (ErrorViewModel) -> Unit): Disposable {
        observers.add(observer)
        observer.invoke(state)
        return Disposable {
            observers.remove(observer)
        }
    }

    private fun showDetails() {
        state = state.copy(showDetails = true)
    }

    fun hideDetails() {
        state = state.copy(showDetails = false)
    }

    private fun generateReport(dumpCardContent: Boolean = true): String {
        val results = JSONObject()

        if (currentErrors.size > 0) {
            val errors = JSONArray()
            currentErrors.forEach {
                errors.put(JSONObject().apply {
                    put("message", it.fullStackMessage)
                    put("stacktrace", it.stackTraceToString())

                    if (it is ParsingException) {
                        put("reason", it.reason)
                        put("json_source", it.source?.dump())
                        put("json_summary", it.jsonSummary)
                    }
                })
            }
            results.put("errors", errors)
        }

        if (currentWarnings.size > 0) {
            val warnings = JSONArray()
            currentWarnings.forEach {
                warnings.put(JSONObject().apply {
                    put("warning_message", it.message)
                    put("stacktrace", it.stackTraceToString())
                })
            }
            results.put("warnings", warnings)
        }

        if (dumpCardContent) {
            results.put("card", dumpCardWithContextVariables())
        }
        return results.toString(/*indentSpaces*/ 4)
    }

    fun getAllControllers(): Map<String, VariableController> {
        val runtimeStore = div2View.runtimeStore
        val pathToRuntimes = runtimeStore.getUniquePathsAndRuntimes()

        val result = mutableMapOf<String, VariableController>()
        result[""] = runtimeStore.rootRuntime.variableController

        pathToRuntimes.forEach { (path, runtime) ->
            result[path] = runtime.variableController
        }
        return result
    }

    private val ExpressionsRuntime.variableController get() = expressionResolver.variableController

    fun getErrorHandler(): (Throwable) -> Unit {
        return div2View::logError
    }

    fun onCounterClick(rootWidth: Int, rootHeight: Int) {
        val minSizePx = MIN_SIZE_FOR_DETAILS_DP.dpToPx(div2View.context.resources.displayMetrics)
        if (rootWidth < minSizePx || rootHeight < minSizePx) {
            copyReportToClipboard()
        } else {
            showDetails()
        }
    }
    
    fun copyReportToClipboard() {
        val fullReport = generateReport()
        pasteToClipBoard(fullReport).onFailure {
            if (it.causedByTransactionTooLargeException()) {
                pasteToClipBoard(generateReport(dumpCardContent = false))
            }
        }
    }

    private fun pasteToClipBoard(s: String): Result<Unit> {
        val context = div2View.context
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                ?: run {
                    Assert.fail("Failed to access clipboard manager!")
                    return Result.success(Unit)
                }

        try {
            clipboardManager.setPrimaryClip(
                ClipData(
                    "Error report",
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    ClipData.Item(s)
                )
            )
        } catch (e: Exception) {
            return Result.failure(RuntimeException("Failed paste report to clipboard!", e))
        }

        Toast.makeText(context, "Errors, DivData and Variables are dumped to clipboard!",
            Toast.LENGTH_LONG).show()

        return Result.success(Unit)
    }

    private fun dumpCardWithContextVariables() = JSONObject().apply {
        // Let's dump in format that is suitable for playground apps.
        put("templates", JSONObject())
        put("card", div2View.divData?.writeToJSON())
        put("variables", dumpGlobalVariables())
    }

    private fun dumpGlobalVariables(): JSONArray {
        val result = JSONArray()
        div2View.div2Component.divVariableController.captureAllVariables().forEach {
            result.put(it.writeToJSON())
        }
        return result
    }

    private var state = ErrorViewModel()
        set(value) {
            field = value
            observers.forEach { it.invoke(value) }
        }
}

private val Throwable.fullStackMessage: String
    get() {
        val result = StringBuilder()
        result.append(message)

        var nextCause = cause
        while(nextCause != null) {
            result.append('\n')
            result.append(nextCause.message)
            nextCause = nextCause.cause
        }
        return result.toString()
    }

private fun Throwable.causedByTransactionTooLargeException(): Boolean {
    return this is TransactionTooLargeException ||
            this.cause?.causedByTransactionTooLargeException() == true
}
