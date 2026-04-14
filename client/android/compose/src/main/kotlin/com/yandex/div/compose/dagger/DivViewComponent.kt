package com.yandex.div.compose.dagger

import com.yandex.div.compose.actions.VisibilityActionTracker
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.Component
import javax.inject.Named

@DivViewScope
@Component(isRoot = false)
internal interface DivViewComponent {

    val localComponentStorage: DivLocalComponentStorage
    val visibilityActionTracker: VisibilityActionTracker

    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController

    fun localComponent(): DivLocalComponent.Builder

    @Component.Builder
    interface Builder {
        fun build(): DivViewComponent
    }
}
