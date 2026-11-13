package com.yandex.div.compose.di

import androidx.compose.runtime.MutableState
import com.yandex.div.compose.actions.ActionMenuHolder
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.histogram.DivViewHistogramReporter
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.patch.DivPatchCoordinator
import com.yandex.div.compose.system.HapticFeedbackStorage
import com.yandex.div.compose.text.LabelTextStorage
import com.yandex.div.compose.timers.TimerStorage
import com.yandex.div.compose.tooltips.DivTooltipController
import com.yandex.div.compose.tooltips.TooltipStateStorage
import com.yandex.div2.DivData
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@DivViewScope
@GraphExtension(
    bindingContainers = [DivViewModule::class]
)
internal interface DivViewComponent : DivViewDependencies {

    val actionMenuHolder: ActionMenuHolder
    val data: DivData
    val hapticFeedbackStorage: HapticFeedbackStorage
    val histogramReporter: DivViewHistogramReporter
    val labelTextStorage: LabelTextStorage
    val localComponentStorage: DivLocalComponentStorage
    val pagerStateStorage: DivPagerStateStorage
    val patchCoordinator: DivPatchCoordinator
    val states: MutableState<List<DivData.State>>
    val timerStorage: TimerStorage
    val tooltipController: DivTooltipController
    val tooltipStateStorage: TooltipStateStorage

    @get:Named(Names.ROOT_LOCAL_COMPONENT)
    val rootLocalComponent: Lazy<DivLocalComponent>

    fun localComponent(): DivLocalComponent.Builder

    @GraphExtension.Factory
    interface Builder {
        fun build(
            @Provides data: DivData
        ): DivViewComponent
    }
}
