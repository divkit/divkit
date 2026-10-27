package com.yandex.div.compose.dagger

import androidx.compose.runtime.MutableState
import com.yandex.div.compose.actions.ActionMenuHolder
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.VisibilityActionTracker
import com.yandex.div.compose.animation.AnimatorStorage
import com.yandex.div.compose.context.CompositionKeyStorage
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.context.DivViewCoroutineScopeProvider
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div.compose.haptics.HapticFeedbackStorage
import com.yandex.div.compose.histogram.DivViewHistogramReporter
import com.yandex.div.compose.images.ImageStateStorage
import com.yandex.div.compose.layoutprovider.LayoutProviderStorage
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.patch.DivPatchCoordinator
import com.yandex.div.compose.scroll.ScrollTargetStorage
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.storedvalues.DivStoredValuesStorage
import com.yandex.div.compose.text.LabelTextStorage
import com.yandex.div.compose.timers.TimerStorage
import com.yandex.div.compose.tooltips.DivTooltipController
import com.yandex.div.compose.tooltips.TooltipStateStorage
import com.yandex.div.compose.video.VideoPlayerStorage
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div2.DivData
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import com.yandex.yatagan.Lazy
import javax.inject.Named

@DivViewScope
@Component(
    isRoot = false,
    modules = [DivViewModule::class]
)
internal interface DivViewComponent {

    val actionHandler: DivActionHandler
    val actionMenuHolder: ActionMenuHolder
    val animatorStorage: AnimatorStorage
    val compositionKeyStorage: CompositionKeyStorage
    val coroutineScopeProvider: DivViewCoroutineScopeProvider
    val data: DivData
    val focusBindingStorage: FocusBindingStorage
    val hapticFeedbackStorage: HapticFeedbackStorage
    val histogramReporter: DivViewHistogramReporter
    val imageStateStorage: ImageStateStorage
    val labelTextStorage: LabelTextStorage
    val layoutProviderStorage: LayoutProviderStorage
    val localComponentStorage: DivLocalComponentStorage
    val pagerStateStorage: DivPagerStateStorage
    val patchCoordinator: DivPatchCoordinator
    val scrollTargetStorage: ScrollTargetStorage
    val states: MutableState<List<DivData.State>>
    val stateStorage: DivStateStorage
    val storedValuesStorage: DivStoredValuesStorage
    val timerStorage: TimerStorage
    val tooltipController: DivTooltipController
    val tooltipStateStorage: TooltipStateStorage
    val videoPlayerStorage: VideoPlayerStorage
    val visibilityActionTracker: VisibilityActionTracker

    @get:Named(Names.ROOT_LOCAL_COMPONENT)
    val rootLocalComponent: Lazy<DivLocalComponent>

    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController

    fun localComponent(): DivLocalComponent.Builder

    @Component.Builder
    interface Builder {
        fun build(
            @BindsInstance data: DivData
        ): DivViewComponent
    }
}
