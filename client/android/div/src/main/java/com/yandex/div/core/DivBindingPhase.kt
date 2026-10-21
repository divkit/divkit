package com.yandex.div.core

import com.yandex.div.core.annotations.ExperimentalApi

/** Readiness phases and terminal outcomes of one asynchronous data binding request. */
@ExperimentalApi
enum class DivBindingPhase {
    /** Preparation has completed, but root binding still awaits attachment. */
    PREPARED,

    /** Root binding and its main-thread changes have completed. Layout and media loading may still be pending. */
    APPLIED,

    /** The request was cancelled before binding was fully applied. */
    CANCELLED,

    /**
     * The request could not be applied, including completion without a prepared root.
     * A failure does not necessarily have an exception. Runtime errors use [DivErrorsReporter].
     */
    FAILED,
}
