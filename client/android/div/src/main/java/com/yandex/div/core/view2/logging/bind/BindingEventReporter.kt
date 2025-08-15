package com.yandex.div.core.view2.logging.bind

import com.yandex.div.core.view2.animations.DivComparatorReporter
import com.yandex.div.core.view2.reuse.ComplexRebindReporter

internal interface BindingEventReporter : DivComparatorReporter, ComplexRebindReporter,
    SimpleRebindReporter, ForceRebindReporter {

    fun onStateUpdateCompleted()

    fun onBindingFatalNoData()

    fun onBindingFatalNoState()

    fun onBindingFatalSameData()

    companion object {
        val STUB = object : BindingEventReporter {

            override fun onStateUpdateCompleted() = Unit

            override fun onBindingFatalNoData() = Unit

            override fun onBindingFatalNoState() = Unit

            override fun onBindingFatalSameData() = Unit
        }
    }
}
