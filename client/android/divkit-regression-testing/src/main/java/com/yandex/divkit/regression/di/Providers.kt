package com.yandex.divkit.regression.di

import android.content.Context

internal fun Context.provideHasRegressionTesting() =
    applicationContext as HasRegressionTesting

internal fun Context.provideRegressionComponent() = provideHasRegressionTesting().regressionComponent

internal fun Context.provideScenariosRepository() = provideRegressionComponent().scenariosRepository

internal fun Context.provideDiv2ViewCreator() = provideRegressionComponent().div2ViewCreator

internal fun Context.provideScenarioViewCreator() = provideRegressionComponent().scenarioViewCreator

internal fun Context.provideIsComposeRendererEnabled() = provideHasRegressionTesting().isComposeRendererEnabled
