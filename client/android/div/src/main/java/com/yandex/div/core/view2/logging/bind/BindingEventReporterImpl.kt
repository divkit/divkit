package com.yandex.div.core.view2.logging.bind

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.logging.EventMessageBuilder
import com.yandex.div.core.view2.reuse.RebindTask
import com.yandex.div2.DivData

internal class BindingEventReporterImpl(
    private val div2View: Div2View,
    private val oldData: DivData?,
    private val newData: DivData?,
) : EventMessageBuilder(), BindingEventReporter {

    override fun onComparisonNoOldData() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "No old `DivData` to compare with"
        )
    }

    override fun onComparisonNoState() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "Cannot find required state of `DivData`"
        )
    }

    override fun onComparisonDifferentClasses() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "For some element its old and new java classes are not equal"
        )
    }

    override fun onComparisonDifferentIdsWithTransition() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "Some element has changed its `id` while has transitions"
        )
    }

    override fun onComparisonDifferentCustomTypes() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "Some `DivCustom` element has different `customType`"
        )
    }

    override fun onComparisonDifferentOverlap() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "Some element has changed its `orientation` from/to 'overlap'"
        )
    }

    override fun onComparisonDifferentWrap() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "Some element has changed its `layoutMode` from/to 'wrap'"
        )
    }

    override fun onComparisonDifferentChildCount() {
        appendEventMessage(
            EVENT_COMPARISON_FAILED,
            "Some element changed its child count"
        )
    }

    override fun onComplexRebindSuccess() {
        sendLog("Performed complex rebind")
    }

    override fun onComplexRebindNothingToBind() {
        appendEventMessage(
            EVENT_COMPLEX_REBIND_FAILED,
            "Cannot find any difference to bind"
        )
    }

    override fun onComplexRebindUnsupportedElementException(e: RebindTask.UnsupportedElementException) {
        appendEventMessage(
            "$EVENT_COMPLEX_REBIND_FAILED with exception",
            "${e::class} (${e.message})"
        )
    }

    override fun onComplexRebindFatalNoState() {
        sendLog(MESSAGE_FATAL_NO_STATE)
    }

    override fun onComplexRebindNoDivInState() {
        appendEventMessage(
            EVENT_COMPLEX_REBIND_FAILED,
            "Cannot find div inside state to bind"
        )
    }

    override fun onComplexRebindNoExistingParent() {
        appendEventMessage(
            EVENT_COMPLEX_REBIND_FAILED,
            "Cannot find any existing view to start binding"
        )
    }

    override fun onSimpleRebindSuccess() {
        sendLog("Performed simple rebind")
    }

    override fun onSimpleRebindNoChild() {
        appendEventMessage(
            EVENT_SIMPLE_REBIND_FAILED,
            "Div2View has no child to rebind"
        )
    }

    override fun onSimpleRebindFatalNoState() {
        sendLog(MESSAGE_FATAL_NO_STATE)
    }

    override fun onSimpleRebindException(e: Exception) {
        appendEventMessage(
            "$EVENT_SIMPLE_REBIND_FAILED with exception",
            "${e::class} (${e.message})"
        )
    }

    override fun onForceRebindSuccess() {
        sendLog("Performed unoptimized rebind. Old data was cleaned up")
    }

    override fun onForceRebindFatalNoState() {
        sendLog(MESSAGE_FATAL_NO_STATE)
    }

    override fun onFirstBindingCompleted() {
        sendLog("DivData bound for the first time")
    }

    override fun onStateUpdateCompleted() {
        sendLog("Performed state update")
    }

    override fun onBindingFatalNoData() {
        sendLog("Binding failed. New DivData not provided")
    }

    override fun onBindingFatalNoState() {
        sendLog("Binding failed. Cannot find state to bind")
    }

    override fun onBindingFatalSameData() {
        sendLog("No actions performed. Old and new DivData are the same")
    }

    private fun sendLog(result: String) {
        div2View.div2Component.div2Logger.logBindingResult(
            div2View,
            oldData,
            newData,
            result,
            buildEventsLogMessage()
        )
    }

    companion object {
        private const val EVENT_COMPARISON_FAILED = "Div comparison failed"
        private const val EVENT_COMPLEX_REBIND_FAILED = "Complex rebind failed"
        private const val EVENT_SIMPLE_REBIND_FAILED = "Simple rebind failed"
        private const val MESSAGE_FATAL_NO_STATE = "Div has no state to bind"
    }
}
