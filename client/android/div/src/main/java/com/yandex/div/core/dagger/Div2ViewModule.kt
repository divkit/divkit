package com.yandex.div.core.dagger

import com.yandex.div.core.experiments.Experiment.MULTIPLE_STATE_CHANGE_ENABLED
import com.yandex.div.core.util.ReportingSafeDrawingPassOverrideStrategy
import com.yandex.div.core.view.DrawingPassOverrideStrategy
import com.yandex.div.core.view2.state.DivJoinedStateSwitcher
import com.yandex.div.core.view2.state.DivMultipleStateSwitcher
import com.yandex.div.core.view2.state.DivStateSwitcher
import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import javax.inject.Provider

@Module
internal interface Div2ViewModule {

    @Binds
    fun bindsDrawingPassOverrideStrategy(i: ReportingSafeDrawingPassOverrideStrategy): DrawingPassOverrideStrategy

    companion object {

        @JvmStatic
        @Provides
        @DivViewScope
        fun provideStateSwitcher(
            @ExperimentFlag(MULTIPLE_STATE_CHANGE_ENABLED) multipleStateChangeEnabled: Boolean,
            joinedStateSwitcher: Provider<DivJoinedStateSwitcher>,
            multipleStateSwitcher: Provider<DivMultipleStateSwitcher>
        ): DivStateSwitcher {
            return if (multipleStateChangeEnabled) multipleStateSwitcher.get() else joinedStateSwitcher.get()
        }
    }
}
