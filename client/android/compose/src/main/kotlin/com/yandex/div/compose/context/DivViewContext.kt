package com.yandex.div.compose.context

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.actions.observe
import com.yandex.div.compose.actions.VisibilityActionTracker
import com.yandex.div.compose.actions.bind
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewComponent
import com.yandex.div.compose.haptics.bind
import com.yandex.div.compose.layoutprovider.observe
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.patch.observe
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.timers.observe
import com.yandex.div.compose.triggers.observe
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.expressions.toLocalFunctions
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivVariable

internal class DivViewContext(
    internal val component: DivViewComponent
) {
    private var compositionCount = 0

    val states: List<DivData.State>
        get() = component.states.value

    val rootDiv: Div?
        get() = states.firstOrNull()?.div

    val compositionKeyStorage: CompositionKeyStorage
        get() = component.compositionKeyStorage

    val pagerStateStorage: DivPagerStateStorage
        get() = component.pagerStateStorage

    val rootLocalComponent: DivLocalComponent
        get() = component.rootLocalComponent.get()

    val stateStorage: DivStateStorage
        get() = component.stateStorage

    val visibilityActionTracker: VisibilityActionTracker
        get() = component.visibilityActionTracker

    init {
        component.data.variableTriggers.orEmpty().forEach(rootLocalComponent.triggerStorage::add)
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun onComposition() {
        DisposableEffect(this) {
            if (compositionCount > 0) {
                rootLocalComponent.reporter.reportError(
                    "DivViewContext is already used in another composition. This may lead to undefined behavior."
                )
            }
            compositionCount++
            onDispose { compositionCount-- }
        }

        visibilityActionTracker.bind()
        component.hapticFeedbackStorage.bind()
        component.layoutProviderStorage.observe()
        component.patchCoordinator.observe()
        component.patchDownloadManager.observe()
        component.submitActionHandler.observe()
        component.timerStorage.observe()
        rootLocalComponent.triggerStorage.observe()
    }

    fun getLocalComponent(
        data: DivBase,
        parentComponent: DivLocalComponent,
    ): DivLocalComponent {
        component.localComponentStorage.get(data)?.let {
            return it
        }

        val functions = data.functions.orEmpty().toLocalFunctions()
        val variables = data.variables.orEmpty()
        return createLocalComponent(
            variableController = if (variables.isEmpty()) {
                parentComponent.variableController
            } else {
                DivVariableController(parentComponent.variableController)
            },
            functionProvider = parentComponent.functionProvider + functions,
            variables = variables
        ).also { localComponent ->
            component.localComponentStorage.put(data, localComponent)
            data.variableTriggers.orEmpty().forEach(localComponent.triggerStorage::add)
        }
    }

    private fun createLocalComponent(
        variableController: DivVariableController,
        functionProvider: FunctionProviderDecorator,
        variables: List<DivVariable>,
    ): DivLocalComponent {
        val localComponent = component.localComponent().build(
            functionProvider = functionProvider,
            variableController = variableController
        )

        variables.forEach { variableData ->
            localComponent.variableAdapter.convert(variableData)?.let {
                variableController.declare(it)
            }
        }

        return localComponent
    }
}

internal val LocalDivViewContext = staticCompositionLocalOf<DivViewContext> {
    throw DivException("DivViewContext not provided")
}
