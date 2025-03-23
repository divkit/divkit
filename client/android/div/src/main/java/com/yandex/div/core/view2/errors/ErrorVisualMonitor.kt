package com.yandex.div.core.view2.errors

import android.net.Uri
import android.view.ViewGroup
import com.yandex.div.core.Disposable
import com.yandex.div.core.actions.logError
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.PERMANENT_DEBUG_PANEL_ENABLED
import com.yandex.div.core.experiments.Experiment.VISUAL_ERRORS_ENABLED
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.Binding
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewBindingProvider
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.KLog
import com.yandex.div.internal.util.map
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div2.DivAction
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject

private const val SHOW_LIMIT = 25

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
    fun bind(binding: Binding) {
            existingSubscription?.close()
            existingSubscription = errorCollectors
                .getOrCreate(binding.tag, binding.data)
                .observeAndGet(updateOnErrors)
        }

    private val observers = mutableSetOf<(ErrorViewModel) -> Unit>()
    private val currentErrors = mutableListOf<Throwable>()
    private val currentWarnings = mutableListOf<Throwable>()
    private var existingSubscription: Disposable? = null

    private val updateOnErrors = { errors: List<Throwable>, warnings: List<Throwable> ->
        if (visualErrorsEnabled) {
            this.currentErrors.apply {
                clear()
                addAll(errors.reversed())
            }
            this.currentWarnings.apply {
                clear()
                addAll(warnings.reversed())
            }
            state = state.copy(errorCount = currentErrors.size, errorDetails = errorsToDetails(currentErrors),
                warningCount = currentWarnings.size, warningDetails = warningsToDetails(currentWarnings))
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

    fun showDetails() {
        state = state.copy(showDetails = true)
    }

    fun hideDetails() {
        state = state.copy(showDetails = false)
    }

    fun generateReport(dumpCardContent: Boolean = true): String {
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
        val runtimeStore = div2View.runtimeStore ?: return emptyMap()
        val pathToRuntimes = runtimeStore.getUniquePathsAndRuntimes()

        val result = mutableMapOf<String, VariableController>()
        runtimeStore.rootRuntime?.let {
            result[""] = it.variableController
        }

        pathToRuntimes.forEach { (path, runtime) ->
            result[path] = runtime.variableController
        }
        return result
    }

    fun executeAction(action: String) {
        val parsingEnvironment = DivParsingEnvironment(ParsingErrorLogger.LOG)
        val actions = runCatching { JSONArray(action) }
            .mapCatching { jsonArray ->
                jsonArray.map { actionJson ->
                    DivAction(parsingEnvironment, actionJson as JSONObject)
                }
            }
            .recoverCatching {
                val jsonObject = JSONObject(action)
                listOf(DivAction(parsingEnvironment, jsonObject))
            }
            .getOrElse {
                val uri = Uri.parse(action)
                div2View.handleUri(uri)
                return
            }

        actions.forEach(div2View::handleAction)
    }

    fun getErrorHandler(): (Throwable) -> Unit {
        return div2View::logError
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
