package com.yandex.div.compose.dagger

import com.yandex.div.compose.triggers.DivTriggerStorage
import com.yandex.div.compose.variables.DivVariableAdapter
import com.yandex.div.compose.views.DivLocalContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component

@DivLocalScope
@Component(
    isRoot = false,
    modules = [
        DivLocalModule::class
    ]
)
internal interface DivLocalComponent {

    val context: DivLocalContext
    val triggerStorage: DivTriggerStorage
    val variableAdapter: DivVariableAdapter

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun variableController(variableController: DivVariableController): Builder

        fun build(): DivLocalComponent
    }
}
