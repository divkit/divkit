@file:SuppressLint("ComposableNaming")

package com.yandex.div.compose.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.yandex.div.compose.dagger.LocalComponent

@Composable
internal fun reportError(message: String) {
    val reporter = LocalComponent.current.reporter
    LaunchedEffect(message) {
        reporter.reportError(message)
    }
}

@Composable
internal fun reportWarning(message: String) {
    val reporter = LocalComponent.current.reporter
    LaunchedEffect(message) {
        reporter.reportWarning(message)
    }
}
