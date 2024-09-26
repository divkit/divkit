package com.yandex.div.core.dagger

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTransitionBuilder
import com.yandex.div.core.view2.DivViewIdProvider
import com.yandex.div.core.view2.ViewBindingProvider
import com.yandex.div.core.view2.animations.DivAnimatorController
import com.yandex.div.core.view2.divs.widgets.MediaReleaseViewVisitor
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.view2.errors.ErrorVisualMonitor
import com.yandex.div.core.view2.reuse.InputFocusTracker
import com.yandex.div.core.view2.state.DivStateSwitcher
import com.yandex.div.core.view2.state.DivStateTransitionHolder
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component

@DivViewScope
@Component(isRoot = false, modules = [Div2ViewModule::class])
internal interface Div2ViewComponent {

    val viewIdProvider: DivViewIdProvider

    val transitionBuilder: DivTransitionBuilder

    val releaseViewVisitor: ReleaseViewVisitor
    val mediaReleaseViewVisitor: MediaReleaseViewVisitor

    val stateSwitcher: DivStateSwitcher

    val stateTransitionHolder: DivStateTransitionHolder

    val errorMonitor: ErrorVisualMonitor
    val bindingProvider: ViewBindingProvider
    val errorCollectors: ErrorCollectors
    val inputFocusTracker: InputFocusTracker
    val animatorController: DivAnimatorController

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun divView(divView: Div2View): Builder

        fun build(): Div2ViewComponent
    }
}
