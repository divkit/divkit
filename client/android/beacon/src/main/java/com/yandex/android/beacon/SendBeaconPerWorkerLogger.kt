package com.yandex.android.beacon

import com.yandex.div.core.util.KLog

/**
 * Send beacon logging facility.
 */
interface SendBeaconPerWorkerLogger {

    /**
     * Called when beacon request was prepared and about to be executed.
     */
    fun onTrySendUrl(url: String)

    /**
     * Called when beacon request was successfully executed.
     */
    fun onSuccessSendUrl(url: String)

    /**
     * Called when beacon request was failed due to general error.
     */
    fun onFailedSendUrl(url: String, exceptionCaught: Boolean)

    /**
     * Called when beacon request was failed due to server error (usually this means that server
     * responded with 5XX code for this beacon request).
     */
    fun onFailedSendUrlDueServerError(url: String)

    /**
     * No-op implementation.
     */
    object NoOp : SendBeaconPerWorkerLogger {
        override fun onTrySendUrl(url: String) = Unit
        override fun onSuccessSendUrl(url: String) = Unit
        override fun onFailedSendUrl(url: String, exceptionCaught: Boolean) = Unit
        override fun onFailedSendUrlDueServerError(url: String) = Unit
    }

    /**
     * Logcat implementation.
     */
    object Logcat : SendBeaconPerWorkerLogger {
        override fun onTrySendUrl(url: String) = KLog.i(TAG) { "onTrySendUrl: $url" }
        override fun onSuccessSendUrl(url: String) = KLog.i(TAG) { "onSuccessSendUrl: $url" }
        override fun onFailedSendUrl(url: String, exceptionCaught: Boolean) = KLog.i(TAG) { "onFailedSendUrl: $url" }
        override fun onFailedSendUrlDueServerError(url: String) = KLog.i(TAG) { "onFailedSendUrlDueServerError: $url" }
    }

    private companion object {
        private const val TAG = "SendBeaconPerWorkerLogger"
    }
}
