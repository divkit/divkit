package com.yandex.div.compose.layoutprovider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.missingVariable
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

private const val MAX_LAYOUT_UPDATES = 3

/**
 * Publishes layout-provider measurements to existing integer variables within one DivView.
 *
 * Measurements are converted from pixels to rounded dp and queued, retaining the latest size
 * for each binding. Only the latest pending size for each variable is published.
 * Publication waits for the next frame to avoid changing variables during
 * measurement. If those writes cause another layout, its measurements are published on a later
 * frame; the loop ends when there are no pending sizes.
 *
 * To stop recursive size expressions, each target variable has a limit of [MAX_LAYOUT_UPDATES]
 * writes per publication run. The storage observes target variables while attached. Notifications
 * received outside publication reset only the changed variable's counter, while synchronous
 * notifications caused by our own writes do not. Disposing the last binding to a variable removes
 * its subscription. Handlers are registered only after the composition is applied.
 *
 * [observe] supplies the composition's coroutine scope and frame clock, and cancels pending work
 * when the view leaves the composition.
 */
@DivViewScope
internal class LayoutProviderStorage @Inject constructor(
    private val reporter: DivReporter,
) {
    private val variableHandlers = mutableSetOf<VariableHandler>()
    private val pendingUpdates = linkedMapOf<VariableHandler, Long>()
    private val updateCount = mutableMapOf<Variable, Int>()

    private var isUpdatingVariables = false
    private var coroutineScope: CoroutineScope? = null
    private var variableUpdateJob: Job? = null

    private val variableObserver: (Variable) -> Unit = { variable ->
        if (!isUpdatingVariables) {
            updateCount.remove(variable)
        }
    }

    fun getVariableHandler(
        variableName: String,
        expressionResolver: ExpressionResolver,
    ): VariableHandler? {
        val variable = expressionResolver.getVariable(variableName)
        if (variable == null) {
            reporter.reportError(missingVariable(variableName))
            return null
        }

        if (variable.getValue() !is Long) {
            reporter.reportError("Variable is not an integer variable: $variableName")
            return null
        }

        return VariableHandler(variable)
    }

    fun attach(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
        variableHandlers.forEach { it.variable.addObserver(variableObserver) }
    }

    fun detach() {
        variableHandlers.forEach { it.variable.removeObserver(variableObserver) }

        variableUpdateJob?.cancel()
        variableUpdateJob = null

        pendingUpdates.clear()
        updateCount.clear()

        isUpdatingVariables = false
        coroutineScope = null
    }

    private suspend fun updateVariables() {
        updateCount.clear()
        try {
            while (true) {
                withFrameNanos { }
                if (pendingUpdates.isEmpty()) {
                    return
                }

                val sizes = pendingUpdates.entries.associate { (handler, size) -> handler.variable to size }
                pendingUpdates.clear()
                isUpdatingVariables = true
                try {
                    sizes.forEach { (variable, size) -> updateVariable(variable, size) }
                } finally {
                    isUpdatingVariables = false
                }
            }
        } finally {
            if (variableUpdateJob === currentCoroutineContext()[Job]) {
                variableUpdateJob = null
            }
        }
    }

    private fun updateVariable(
        variable: Variable,
        size: Long,
    ) {
        if (variable.getValue() == size) {
            return
        }

        val count = updateCount[variable] ?: 0
        if (count >= MAX_LAYOUT_UPDATES) {
            reporter.reportWarning("Recursive layout for variable: ${variable.name}")
            return
        }

        updateCount[variable] = count + 1
        try {
            variable.setValueDirectly(size)
        } catch (e: VariableMutationException) {
            reporter.reportError(e)
        }
    }

    inner class VariableHandler internal constructor(
        internal val variable: Variable,
    ) {
        private var previousSize = 0

        fun register() {
            variableHandlers.add(this)
            if (coroutineScope != null) {
                variable.addObserver(variableObserver)
            }
        }

        fun unregister() {
            pendingUpdates.remove(this)
            variableHandlers.remove(this)
            if (variableHandlers.none { it.variable === variable }) {
                variable.removeObserver(variableObserver)
            }
        }

        fun update(
            sizeInPixels: Int,
            density: Float,
        ) {
            if (this !in variableHandlers || previousSize == sizeInPixels) {
                return
            }

            previousSize = sizeInPixels
            pendingUpdates.remove(this)
            pendingUpdates[this] = (sizeInPixels / density).roundToInt().toLong()
            if (variableUpdateJob == null) {
                variableUpdateJob = coroutineScope?.launch { updateVariables() }
            }
        }
    }
}

@Composable
internal fun LayoutProviderStorage.observe() {
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(this) {
        attach(coroutineScope)
        onDispose {
            detach()
        }
    }
}
