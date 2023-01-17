package com.yandex.div.rive

interface DivRiveNetworkDelegate {
    fun load(url: String): ByteArray
}
