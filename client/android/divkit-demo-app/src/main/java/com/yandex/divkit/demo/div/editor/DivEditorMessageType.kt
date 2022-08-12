package com.yandex.divkit.demo.div.editor

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    MessageType.LISTEN_MESSAGE,
    MessageType.JSON_MESSAGE,
    MessageType.UI_STATE,
    MessageType.ERROR,
)
annotation class MessageType {

    companion object {
        // Send types
        const val LISTEN_MESSAGE = "listen"
        const val UI_STATE = "ui_state"

        // Receive types
        const val JSON_MESSAGE = "json"
        const val ERROR = "error"
    }
}
