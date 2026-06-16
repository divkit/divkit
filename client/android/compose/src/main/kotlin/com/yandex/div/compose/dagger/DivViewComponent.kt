package com.yandex.div.compose.dagger

import com.yandex.div.compose.actions.ActionMenuHolder
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.VisibilityActionTracker
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.histogram.DivViewHistogramReporter
import com.yandex.div.compose.images.ImageStateStorage
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.timers.TimerStorage
import com.yandex.div.compose.tooltips.TooltipStateStorage
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import javax.inject.Named

@DivViewScope
@Component(
    isRoot = false,
    modules = [DivViewModule::class]
)
internal interface DivViewComponent {

    val actionHandler: DivActionHandler
    val actionMenuHolder: ActionMenuHolder
    val histogramReporter: DivViewHistogramReporter
    val imageStateStorage: ImageStateStorage
    val localComponentStorage: DivLocalComponentStorage
    val pagerStateStorage: DivPagerStateStorage
    val stateStorage: DivStateStorage
    val timerStorage: TimerStorage
    val tooltipStateStorage: TooltipStateStorage
    val visibilityActionTracker: VisibilityActionTracker

    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController

    fun localComponent(): DivLocalComponent.Builder

    @Component.Builder
    interface Builder {
        fun build(
            @BindsInstance @Named(Names.CARD_ID) cardId: String
        ): DivViewComponent
    }
}
