package com.yandex.div.core.view2.debugview

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.TransactionTooLargeException
import android.widget.Toast
import androidx.core.net.toUri
import com.yandex.div.DivDataTag
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.Binding
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.util.hotreload.HotReloadController
import com.yandex.div.core.util.hotreload.HotReloadStatus
import com.yandex.div.core.view2.errors.LogcatErrorDumper
import com.yandex.div.internal.Assert
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.ParsingException
import org.json.JSONArray
import org.json.JSONObject

private const val SHOW_LIMIT = 25
private const val HOT_RELOAD_ANNOUNCE_DURATION = 5000L
private const val DOC_LINK = "https://github.com/divkit/divkit/tools/hot_reload/README.md"

/**
 * Provides [DebugViewModel] to view for rendering.
 */
internal class DebugViewModelProvider(
    private val errorCollectors: ErrorCollectors,
    private val div2View: Div2View,
    private val visualErrorsEnabled: Boolean,
    private val alwaysShowDebugView: Boolean,
    private val hotReloadController: HotReloadController,
) {
    private var hotReloadObserver: Disposable? = null
    private var dataTag: DivDataTag? = null

    fun bind(binding: Binding) {
        dataTag = binding.tag
        existingSubscription?.close()
        existingSubscription = errorCollectors
            .getOrCreate(binding.tag, binding.data)
            .observeAndGet(updateOnErrors)
    }

    private val observers = mutableSetOf<(DebugViewModel) -> Unit>()
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
            state = state.copy(errors = errors, warnings = warnings)
            logcatErrorDumper.logErrors(currentErrors, currentWarnings, dataTag)
        }
    }

    private fun toViewModel(state: State): DebugViewModel {
        if (state.showDetails) {
            return renderDetailsViewModel(state)
        }

        if (state.hotReloadStatus != null) {
            tryRenderHotReloadViewModel(state.hotReloadStatus)?.let {
                return it
            }
        }

        val alwaysShow = alwaysShowDebugView || state.hotReloadActive

        if (alwaysShow || visualErrorsEnabled) {
            tryRenderErrorsAndWarnings()?.let {
                return it
            }
        }

        return if (alwaysShow) {
            DebugViewModel.InfoButton(
                label = "",
                background = R.drawable.neutral_counter_background,
            )
        } else {
            DebugViewModel.Hidden
        }
    }

    private fun renderDetailsViewModel(state: State): DebugViewModel.Details =
        DebugViewModel.Details(
            errorsAndWarningsOverview = buildString {
                if (state.errors.isNotEmpty()) {
                    append(errorsToDetails(state.errors))
                }

                if (state.warnings.isNotEmpty()) {
                    append(warningsToDetails(state.warnings))
                }
            },
            hotReload = DebugViewModel.Details.HotReloadViewModel(
                title = "Hot Reload",
                description = when (val s = state.hotReloadStatus) {
                    HotReloadStatus.Applied -> "Applied!"
                    is HotReloadStatus.Failure -> "Failure: ${s.error.message}"
                    HotReloadStatus.Reloading -> "Reloading..."
                    HotReloadStatus.Skipped -> "Update Skipped."
                    null -> ""
                },
                switchChecked = state.hotReloadActive,
                onSwitchClick = {
                    val nowActive = it

                    if (state.hotReloadActive == nowActive) {
                        return@HotReloadViewModel
                    }

                    this.state = state.copy(hotReloadActive = nowActive)
                },

                docLink = if (state.hotReloadActive) {
                    "View Documentation"
                } else {
                    null
                },
                onDocLinkClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, DOC_LINK.toUri())
                        div2View.context.startActivity(intent)
                    } catch (e: Exception) {
                        div2View.logError(
                            Exception("Unable to open documentation link: ${DOC_LINK}!", e)
                        )
                    }
                },
                address = hotReloadController.host,
                onAddressChanged = {
                    hotReloadController.host = it
                }
            )
        )

    private fun tryRenderHotReloadViewModel(hotReloadStatus: HotReloadStatus): DebugViewModel? =
        when (hotReloadStatus) {
            HotReloadStatus.Applied -> DebugViewModel.InfoButton(
                label = "✓",
                notificationText = "Hot reload!",
                background = R.drawable.neutral_counter_background,
            )

            is HotReloadStatus.Failure -> DebugViewModel.InfoButton(
                label = "!",
                notificationText = "Hot reload failed!",
                background = R.drawable.error_counter_background,
            )

            HotReloadStatus.Reloading -> DebugViewModel.InfoButton(
                label = "•",
                notificationText = "",
                background = R.drawable.neutral_counter_background,
            )

            HotReloadStatus.Skipped -> null
        }

    fun tryRenderErrorsAndWarnings(): DebugViewModel? {
        if (state.errors.isNotEmpty() && state.warnings.isNotEmpty()) {
            return DebugViewModel.InfoButton(
                label = "${state.errors.size}/${state.warnings.size}",
                background = R.drawable.warning_error_counter_background,
            )
        }

        if (state.errors.isNotEmpty()) {
            return DebugViewModel.InfoButton(
                label = state.errors.size.toString(),
                background = R.drawable.error_counter_background,
            )
        }

        if (state.warnings.isNotEmpty()) {
            return DebugViewModel.InfoButton(
                label = state.warnings.size.toString(),
                background = R.drawable.warning_counter_background,
            )
        }

        return null
    }

    fun observeAndGet(observer: (DebugViewModel) -> Unit): Disposable {
        observers.add(observer)
        observer.invoke(toViewModel(state))
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

        if (currentErrors.isNotEmpty()) {
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

        if (currentWarnings.isNotEmpty()) {
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

    fun onCounterClick() {
        showDetails()
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

        Toast.makeText(
            context, "Errors, DivData and Variables are dumped to clipboard!",
            Toast.LENGTH_LONG
        ).show()

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

    private var state: State = State()
        set(value) {
            field = value
            notifyObservers()

            if (hotReloadController.active != value.hotReloadActive) {
                invalidateHotReloadObservation(nowActive = value.hotReloadActive)
            }
            hotReloadController.active = value.hotReloadActive
        }

    private fun invalidateHotReloadObservation(nowActive: Boolean) {
        if (nowActive) {
            hotReloadObserver = hotReloadController.observeStatusNotifications { status ->
                UiThreadHandler.executeOnMainThread {
                    if (state.hotReloadStatus == status) {
                        return@executeOnMainThread
                    }
                    state = state.copy(hotReloadStatus = status)
                }

                div2View.postDelayed({
                    if (state.hotReloadStatus == status) {
                        state = state.copy(hotReloadStatus = null)
                    }
                }, HOT_RELOAD_ANNOUNCE_DURATION)

            }
        } else {
            hotReloadObserver?.close()
            hotReloadObserver = null
        }
    }

    private fun notifyObservers() {
        val viewModel = toViewModel(state)
        observers.forEach { it.invoke(viewModel) }
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

    return "Last ${SHOW_LIMIT} errors:\n$errorsList"
}

private fun warningsToDetails(currentWarnings: List<Throwable>): String {
    val warningsList = currentWarnings.take(SHOW_LIMIT)
        .joinToString(separator = "\n") { " - ${it.fullStackMessage}" }
    return "Last ${SHOW_LIMIT} warnings:\n$warningsList"
}

private val Throwable.fullStackMessage: String
    get() {
        val result = StringBuilder()
        result.append(message)

        var nextCause = cause
        while (nextCause != null) {
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
