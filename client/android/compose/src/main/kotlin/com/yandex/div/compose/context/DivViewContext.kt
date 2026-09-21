package com.yandex.div.compose.context

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.compose.actions.VisibilityActionTracker
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewComponent
import com.yandex.div.compose.haptics.bind
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.timers.observe
import com.yandex.div.compose.triggers.observe
import com.yandex.div.compose.video.VideoPlayerStorage
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.expressions.toLocalFunctions
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable

internal class DivViewContext(
    data: DivData,
    internal val component: DivViewComponent
) {
    val rootLocalComponent: DivLocalComponent

    val pagerStateStorage: DivPagerStateStorage
        get() = component.pagerStateStorage

    val stateStorage: DivStateStorage
        get() = component.stateStorage

    val videoPlayerStorage: VideoPlayerStorage
        get() = component.videoPlayerStorage

    val visibilityActionTracker: VisibilityActionTracker
        get() = component.visibilityActionTracker

    init {
        val baseFunctionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider)
        val functions = data.functions.orEmpty().toLocalFunctions()
        rootLocalComponent = createLocalComponent(
            variableController = DivVariableController(component.variableController),
            functionProvider = baseFunctionProvider + functions,
            triggers = data.variableTriggers.orEmpty(),
            variables = data.variables.orEmpty()
        )

        component.timerStorage.init(
            timers = data.timers.orEmpty(),
            localComponent = rootLocalComponent
        )
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun onComposition() {
        component.hapticFeedbackStorage.bind()
        component.timerStorage.observe()
        rootLocalComponent.triggerStorage.observe()
    }

    fun getLocalComponent(
        data: DivBase,
        parentComponent: DivLocalComponent
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
            triggers = data.variableTriggers.orEmpty(),
            variables = variables
        ).also {
            component.localComponentStorage.put(data, it)
        }
    }

    private fun createLocalComponent(
        variableController: DivVariableController,
        functionProvider: FunctionProviderDecorator,
        triggers: List<DivTrigger>,
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

        triggers.forEach {
            localComponent.triggerStorage.add(it)
        }

        return localComponent
    }
}

internal val LocalDivViewContext = staticCompositionLocalOf<DivViewContext> {
    throw DivException("DivViewContext not provided")
}
