package com.yandex.div.compose.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.animation.onComposition
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.DivViewComponent
import com.yandex.div.compose.di.declaresLocalScope
import com.yandex.div.compose.layoutprovider.onComposition
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.patch.onComposition
import com.yandex.div.compose.system.onComposition
import com.yandex.div.compose.timers.onComposition
import com.yandex.div.compose.triggers.onComposition
import com.yandex.div.compose.variables.ScopedVariableProvider
import com.yandex.div.compose.variables.VariableAndConstantProvider
import com.yandex.div.compose.visibility.onComposition
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.expressions.toLocalFunctions
import com.yandex.div2.DivBase
import com.yandex.div2.DivData

internal class DivViewContext(
    internal val component: DivViewComponent
) {
    private var compositionCount = 0

    val activeRootState: DivData.State?
        get() = component.stateStorage.getActiveRootState(component.states.value)

    val pagerStateStorage: DivPagerStateStorage
        get() = component.pagerStateStorage

    val rootLocalComponent: DivLocalComponent
        get() = component.rootLocalComponent.value

    init {
        component.data.variableTriggers.orEmpty().forEach(rootLocalComponent.triggerStorage::add)
    }

    @Suppress("ComposableNaming")
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

        component.apply {
            // must be attached before other components since other components could rely on
            // the coroutine scope
            coroutineScopeProvider.onComposition()
            animatorStorage.onComposition()
            hapticFeedbackStorage.onComposition()
            layoutProviderStorage.onComposition()
            patchCoordinator.onComposition()
            timerStorage.onComposition()
            visibilityActionTracker.onComposition()
        }
        rootLocalComponent.triggerStorage.onComposition()
    }

    /**
     * The scope the expressions of [data] itself are resolved in: [parentComponent] extended with
     * the variables, functions and triggers [data] declares. Elements declaring none keep
     * [parentComponent], so the scope of an element can be resolved outside its own composition —
     * a container reads the visibility and the margins of a child on the child's behalf and has to
     * use the same scope the child is rendered in.
     */
    fun getOwnComponent(
        data: DivBase,
        parentComponent: DivLocalComponent
    ): DivLocalComponent =
        if (data.declaresLocalScope) getLocalComponent(data, parentComponent) else parentComponent

    fun getLocalComponent(
        data: DivBase,
        parentComponent: DivLocalComponent,
    ): DivLocalComponent {
        component.localComponentStorage.get(data)?.let {
            return it
        }

        val functions = data.functions.orEmpty().toLocalFunctions()
        val functionProvider = parentComponent.functionProvider + functions
        val variables = data.variables.orEmpty()
        val parentProvider = parentComponent.variableProvider
        val localComponent = if (variables.isEmpty()) {
            buildLocalComponent(parentProvider, functionProvider)
        } else {
            val variableProvider = ScopedVariableProvider(
                controller = DivVariableController(parentProvider.controller),
                parent = parentProvider,
            )
            buildLocalComponent(variableProvider, functionProvider).also { localComponent ->
                variables.forEach { variableData ->
                    localComponent.variableAdapter.convert(variableData)
                        ?.let(variableProvider::declare)
                }
            }
        }
        component.localComponentStorage.put(data, localComponent)
        data.variableTriggers.orEmpty().forEach(localComponent.triggerStorage::add)
        return localComponent
    }

    fun createLocalComponent(
        parentComponent: DivLocalComponent,
        variableProvider: VariableAndConstantProvider,
    ): DivLocalComponent = buildLocalComponent(variableProvider, parentComponent.functionProvider)

    fun releaseComponents(divs: Set<DivBase>) {
        component.localComponentStorage.remove(divs)
        component.compositionKeyStorage.remove(divs)
        component.pagerStateStorage.remove(divs)
        component.visibilityTransitionStorage.remove(divs)
    }

    private fun buildLocalComponent(
        variableProvider: VariableAndConstantProvider,
        functionProvider: FunctionProviderDecorator,
    ): DivLocalComponent = component.localComponent().build(
        functionProvider = functionProvider,
        variableProvider = variableProvider,
    )
}

internal val LocalDivViewContext = staticCompositionLocalOf<DivViewContext> {
    error("DivViewContext not provided")
}
