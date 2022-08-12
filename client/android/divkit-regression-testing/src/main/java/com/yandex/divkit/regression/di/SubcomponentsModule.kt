package com.yandex.divkit.regression.di

import com.yandex.divkit.regression.screenrecord.ScreenRecordComponent
import dagger.Module

@Module(subcomponents = [ScreenRecordComponent::class])
internal interface SubcomponentsModule
