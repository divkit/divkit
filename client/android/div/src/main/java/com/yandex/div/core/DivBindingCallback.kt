package com.yandex.div.core

import androidx.annotation.MainThread
import com.yandex.div.core.annotations.ExperimentalApi

/** Receives optional preparation and one terminal phase of a binding request on the main thread. */
@ExperimentalApi
fun interface DivBindingCallback {
    @MainThread
    fun onBindingPhase(phase: DivBindingPhase)
}
