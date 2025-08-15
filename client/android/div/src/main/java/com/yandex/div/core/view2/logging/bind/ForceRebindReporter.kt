package com.yandex.div.core.view2.logging.bind

internal interface ForceRebindReporter {
    fun onForceRebindSuccess() = Unit

    fun onFirstBindingCompleted() = Unit

    fun onForceRebindFatalNoState() = Unit
}
