package com.yandex.divkit.regression.di

import android.content.Context

internal fun Context.provideRegressionComponent() =
    (applicationContext as HasRegressionTesting).regressionComponent

internal fun Context.provideScenariosRepository() = provideRegressionComponent().scenariosRepository

internal fun Context.provideDiv2ViewCreator() = provideRegressionComponent().div2ViewCreator
