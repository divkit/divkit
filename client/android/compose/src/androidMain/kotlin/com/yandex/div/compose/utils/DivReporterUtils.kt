@file:Suppress("ComposableNaming")

package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.yandex.div.compose.di.LocalComponent

@Composable
internal fun reportError(message: String) {
    val reporter = LocalComponent.current.reporter
    LaunchedEffect(message) {
        reporter.reportError(message)
    }
}

@Composable
internal fun reportError(error: Exception) {
    val reporter = LocalComponent.current.reporter
    LaunchedEffect(error) {
        reporter.reportError(error)
    }
}

@Composable
internal fun reportWarning(message: String) {
    val reporter = LocalComponent.current.reporter
    LaunchedEffect(message) {
        reporter.reportWarning(message)
    }
}
