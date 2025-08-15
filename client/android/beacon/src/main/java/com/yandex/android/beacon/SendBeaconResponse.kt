package com.yandex.android.beacon

/**
 * Send beacon network response primitive.
 */
interface SendBeaconResponse {

    /**
     * HTTP response code.
     */
    val responseCode: Int

    /**
     * Whether response is valid.
     */
    fun isValid(): Boolean
}
