package com.yandex.div.core.view2.logging.patch

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.logging.EventMessageBuilder
import com.yandex.div2.DivPatch

internal class PatchEventReporterImpl(
    private val div2View: Div2View,
    private val patch: DivPatch,
) : EventMessageBuilder(), PatchEventReporter {

    override fun onSimpleRebindNoChild() {
        appendEventMessage(
            EVENT_SIMPLE_REBIND_FAILED,
            "Div2View has no child to rebind"
        )
    }

    override fun onSimpleRebindFatalNoState() {
        appendEventMessage(
            EVENT_SIMPLE_REBIND_FAILED,
            "Div has no state to bind"
        )
    }

    override fun onSimpleRebindException(e: Exception) {
        appendEventMessage(
            "$EVENT_SIMPLE_REBIND_FAILED with exception",
            "${e::class} (${e.message})"
        )
    }

    override fun onPatchSuccess() {
        sendLog("Div patched successfully")
    }

    override fun onPatchNoState() {
        sendLog("Patch not performed. Cannot find state to bind")
    }

    private fun sendLog(result: String) {
        div2View.div2Component.div2Logger.logPatchResult(
            div2View,
            patch,
            result,
            buildEventsLogMessage()
        )
    }

    companion object {
        private const val EVENT_SIMPLE_REBIND_FAILED = "Simple rebind failed"
    }
}
