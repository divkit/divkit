package com.yandex.div.compose

import android.content.ContextWrapper
import androidx.annotation.MainThread
import com.yandex.div.compose.dagger.DivContextComponent
import com.yandex.div.compose.views.DivLocalContext
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div2.DivVariable
import javax.inject.Inject

@PublicApi
class DivContext @Inject @MainThread internal constructor(
    internal val component: DivContextComponent
) : ContextWrapper(component.baseContext) {

    internal fun createLocalContext(
        baseVariableController: DivVariableController,
        variables: List<DivVariable>
    ): DivLocalContext {
        val variableController = DivVariableController(baseVariableController)
        val localComponent = component.localComponent()
            .variableController(variableController)
            .build()

        variables.forEach { variableData ->
            localComponent.variableAdapter.convert(variableData)?.let {
                variableController.declare(it)
            }
        }

        return localComponent.context
    }
}
