package com.yandex.divkit.regression.di

import android.content.Context
import androidx.activity.result.ActivityResultRegistry
import com.yandex.divkit.regression.screenrecord.ScreenRecord

internal fun Context.provideRegressionComponent() =
    (applicationContext as HasRegressionTesting).regressionComponent

internal fun Context.provideScenariosRepository() = provideRegressionComponent().scenariosRepository

internal fun Context.provideDiv2ViewCreator() = provideRegressionComponent().div2ViewCreator

fun Context.provideRegressionConfig() = provideRegressionComponent().regressionConfig

internal fun Context.provideScreenRecord(
    activityResultRegistry: ActivityResultRegistry
): ScreenRecord {
    return provideRegressionComponent().screenRecordComponent.create(activityResultRegistry)
        .screenRecord
}
