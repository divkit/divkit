package com.yandex.div.core.view2.logging.bind

internal interface SimpleRebindReporter: ForceRebindReporter {
    fun onSimpleRebindSuccess() = Unit

    fun onSimpleRebindNoChild() = Unit

    fun onSimpleRebindException(e: Exception) = Unit

    fun onSimpleRebindFatalNoState() = Unit
}
