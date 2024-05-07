package com.yandex.div.core.view2.reuse

import com.yandex.div.core.view2.logging.bind.ForceRebindReporter

internal interface ComplexRebindReporter: ForceRebindReporter {
    fun onComplexRebindSuccess() = Unit

    fun onComplexRebindNothingToBind() = Unit

    fun onComplexRebindNoDivInState() = Unit

    fun onComplexRebindNoExistingParent() = Unit

    fun onComplexRebindUnsupportedElementException(e: RebindTask.UnsupportedElementException) = Unit

    fun onComplexRebindFatalNoState() = Unit
}
