package com.yandex.div.core.view2.logging

internal open class EventMessageBuilder {
    private val consolidatedEvents = StringBuilder()

    protected fun appendEventMessage(event: String, message: String) {
        if (consolidatedEvents.isNotEmpty()) {
            consolidatedEvents.append(", ")
        }
        consolidatedEvents.append("$event ($message)")
    }

    protected fun buildEventsLogMessage(): String? = with(consolidatedEvents) {
        if (isNotEmpty()) {
            toString().also { clear() }
        } else {
            null
        }
    }
}
