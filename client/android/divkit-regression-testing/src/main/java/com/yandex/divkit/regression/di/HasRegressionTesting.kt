package com.yandex.divkit.regression.di

interface HasRegressionTesting {
    val regressionComponent: RegressionComponent
    val isComposeRendererEnabled: Boolean get() = false
}
