package com.yandex.div.core.dagger

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTransitionBuilder
import com.yandex.div.core.view2.DivViewIdProvider
import com.yandex.div.core.view2.ViewBindingProvider
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.core.view2.errors.ErrorVisualMonitor
import com.yandex.div.core.view2.state.DivStateSwitcher
import com.yandex.div.core.view2.state.DivStateTransitionHolder
import dagger.BindsInstance
import dagger.Subcomponent

@DivViewScope
@Subcomponent(modules = [Div2ViewModule::class])
internal interface Div2ViewComponent {

    val viewIdProvider: DivViewIdProvider

    val transitionBuilder: DivTransitionBuilder

    val releaseViewVisitor: ReleaseViewVisitor

    val stateSwitcher: DivStateSwitcher

    val stateTransitionHolder: DivStateTransitionHolder

    val errorMonitor: ErrorVisualMonitor
    val bindingProvider: ViewBindingProvider

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun divView(divView: Div2View): Builder

        fun build(): Div2ViewComponent
    }
}
