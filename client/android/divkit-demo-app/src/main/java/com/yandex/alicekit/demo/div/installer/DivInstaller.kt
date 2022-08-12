package com.yandex.alicekit.demo.div.installer

import android.app.Activity
import android.content.IntentFilter

/**
 * Registers/unregisters [DivReceiver].
 * This class is used for debug purposes only.
 */
internal class DivInstaller {

    companion object {
        const val ACTION = "ru.yandex.searchplugin.intent.action.INSTALL_DIV"
    }

    private val divReceiver = DivReceiver()
    private val empty: (String) -> Unit = {}

    fun register(activity: Activity, callback: (String) -> Unit) {
        divReceiver.setCallback(callback)
        activity.registerReceiver(divReceiver, IntentFilter(ACTION))
    }

    fun unregister(activity: Activity) {
        divReceiver.setCallback(empty)
        activity.unregisterReceiver(divReceiver)
    }
}
