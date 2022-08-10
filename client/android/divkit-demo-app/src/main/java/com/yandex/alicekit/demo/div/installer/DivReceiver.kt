package com.yandex.alicekit.demo.div.installer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File

/**
 * Broadcast receiver that reads div JSON from the file.
 * This class is used for debug purposes only.
 */
class DivReceiver : BroadcastReceiver() {

    companion object {
        const val FILE_NAME_EXTRA = "ru.yandex.searchplugin.intent.extra.INSTALL_DIV_CARD_FILE"
    }

    private lateinit var callback: (String) -> Unit

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val fileName = intent.getStringExtra(FILE_NAME_EXTRA)
            GlobalScope.async(Dispatchers.Main) {
                val json: Deferred<String> = async(Dispatchers.Default) {
                    File(fileName).inputStream().bufferedReader().use { it.readText() }
                }
                callback(json.await())
            }
        }
    }

    fun setCallback(callback: (String) -> Unit) {
        this.callback = callback
    }
}
