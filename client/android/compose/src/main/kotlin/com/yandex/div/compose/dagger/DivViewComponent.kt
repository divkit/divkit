package com.yandex.div.compose.dagger

import com.yandex.div.compose.views.DivViewContext
import com.yandex.yatagan.Component

@DivViewScope
@Component(
    isRoot = false,
    modules = [
        DivViewModule::class
    ]
)
internal interface DivViewComponent {

    val context: DivViewContext

    @Component.Builder
    interface Builder {
        fun build(): DivViewComponent
    }
}
