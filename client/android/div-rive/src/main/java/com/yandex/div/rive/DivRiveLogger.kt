package com.yandex.div.rive

interface DivRiveLogger {

    fun log(message: String)

    fun error(message: String, throwable: Throwable?)

    companion object {
        @JvmStatic
        val STUB = object : DivRiveLogger {
            override fun log(message: String) = Unit
            override fun error(message: String, throwable: Throwable?) = Unit
        }
    }
}
