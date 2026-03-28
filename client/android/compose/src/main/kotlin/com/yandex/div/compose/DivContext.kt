package com.yandex.div.compose

import android.content.ContextWrapper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import com.yandex.div.compose.dagger.DivContextComponent
import com.yandex.div.compose.internal.DivDebugFeatures
import com.yandex.div.compose.views.DivLocalContext
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import javax.inject.Inject

/**
 * An implementation of [android.content.Context] that must be used for composing [DivView]s.
 *
 * Example usage:
 *
 *    val configuration = DivComposeConfiguration()
 *    val divContext = configuration.createContext(baseContext = activity)
 */
@PublicApi
class DivContext @Inject @MainThread internal constructor(
    internal val component: DivContextComponent
) : ContextWrapper(component.baseContext) {

    @InternalApi
    @VisibleForTesting
    val debugFeatures: DivDebugFeatures
        get() = component.debugFeatures

    internal fun createLocalContext(
        variableController: DivVariableController,
        functionProvider: FunctionProviderDecorator,
        triggers: List<DivTrigger>,
        variables: List<DivVariable>,
    ): DivLocalContext {
        val localComponent = component.localComponent()
            .functionProvider(functionProvider)
            .variableController(variableController)
            .build()

        variables.forEach { variableData ->
            localComponent.variableAdapter.convert(variableData)?.let {
                variableController.declare(it)
            }
        }

        triggers.forEach {
            localComponent.triggerStorage.add(it)
        }

        return localComponent.context
    }
}
