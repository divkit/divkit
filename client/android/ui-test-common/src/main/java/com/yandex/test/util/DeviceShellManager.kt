package com.yandex.test.util

import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation

object DeviceShellManager {

    private const val LOG_TAG = "DeviceShellManager"
    private const val SAFE_PUNCTUATION = "@%-_+:,./"

    fun executeDeviceShellCommand(cmd: String) {
        Log.i(LOG_TAG, "Executing command \"$cmd\"")
        try {
            val cmdDescriptor = getInstrumentation().uiAutomation.executeShellCommand(cmd)
            awaitTermination(cmdDescriptor)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error executing cmd: \"$cmd\"")
        }
    }

    fun shellEscape(word: String): String {
        val len = word.length
        if (len == 0) {
            return "''"
        }
        for (i in 0 until len) {
            val c = word[i]
            if (!Character.isLetterOrDigit(c) && SAFE_PUNCTUATION.indexOf(c) == -1) {
                return "'" + word.replace("'", "'\\''") + "'"
            }
        }
        return word
    }

    private fun awaitTermination(parcelFileDescriptor: ParcelFileDescriptor): String {
        val inputStream = ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor)
        inputStream.bufferedReader().use { reader ->
            return reader.readText()
        }
    }
}
