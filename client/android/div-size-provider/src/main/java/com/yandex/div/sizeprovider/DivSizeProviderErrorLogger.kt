package com.yandex.div.sizeprovider

interface DivSizeProviderErrorLogger {

    fun logError(e: Throwable)

    companion object {
        val STUB = object : DivSizeProviderErrorLogger {
            override fun logError(e: Throwable) = Unit
        }
    }
}
