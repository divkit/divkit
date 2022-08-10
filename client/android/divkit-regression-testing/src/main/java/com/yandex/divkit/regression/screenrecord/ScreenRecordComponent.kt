package com.yandex.divkit.regression.screenrecord

import androidx.activity.result.ActivityResultRegistry
import com.yandex.divkit.regression.di.ActivityScope
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ScreenRecordModule::class])
interface ScreenRecordComponent {

    val screenRecord: ScreenRecord

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance activityResultRegistry: ActivityResultRegistry
        ): ScreenRecordComponent
    }
}
