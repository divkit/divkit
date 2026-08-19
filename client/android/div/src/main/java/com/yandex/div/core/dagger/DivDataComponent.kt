package com.yandex.div.core.dagger

import com.yandex.div.core.expression.RuntimeStoreProvider
import com.yandex.div.core.expression.local.DivRuntimeVisitor
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.TabsStateCache
import com.yandex.div.core.timer.DivTimerEventDispatcherProvider
import com.yandex.div.core.view2.DivVisibilityActionDispatcher
import com.yandex.div.core.view2.DivViewStateStoreImpl
import com.yandex.div.core.view2.divs.DivLayoutProviderBinder
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import javax.inject.Named

@DivDataScope
@Component(
    isRoot = false,
    multiThreadAccess = true,
)
internal interface DivDataComponent {

    val errorCollectors: ErrorCollectors
    val layoutProviderBinder: DivLayoutProviderBinder
    val runtimeStoreProvider: RuntimeStoreProvider
    val runtimeVisitor: DivRuntimeVisitor
    val stateManager: DivStateManager
    val tabsStateCache: TabsStateCache
    val timerEventDispatcherProvider: DivTimerEventDispatcherProvider
    val visibilityActionDispatcher: DivVisibilityActionDispatcher
    val viewStateStore: DivViewStateStoreImpl

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun dataTag(@Named(Names.DATA_TAG) tag: String): Builder

        fun build(): DivDataComponent
    }
}
