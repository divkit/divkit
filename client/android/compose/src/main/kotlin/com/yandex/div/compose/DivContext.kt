package com.yandex.div.compose

import android.content.ContextWrapper
import androidx.annotation.MainThread
import com.yandex.div.compose.dagger.DivContextComponent
import com.yandex.div.compose.views.DivViewContext
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div2.DivData
import javax.inject.Inject

@PublicApi
class DivContext @Inject @MainThread internal constructor(
    internal val component: DivContextComponent
) : ContextWrapper(component.baseContext) {

    internal fun createDivViewContext(data: DivData): DivViewContext {
        val context = component.viewComponent().build().context

        data.variables.orEmpty().forEach { variableData ->
            context.variableAdapter.convert(variableData)?.let {
                context.variableController.declare(it)
            }
        }

        return context
    }
}
