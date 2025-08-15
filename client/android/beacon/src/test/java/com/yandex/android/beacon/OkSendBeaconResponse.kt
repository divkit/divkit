package com.yandex.android.beacon

internal class OkSendBeaconResponse : SendBeaconResponse {

    override val responseCode = 200

    override fun isValid(): Boolean {
        return true
    }
}
