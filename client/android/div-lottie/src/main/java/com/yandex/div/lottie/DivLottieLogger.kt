package com.yandex.div.lottie

interface DivLottieLogger {

    fun log(message: String)

    fun fail(message: String, throwable: Throwable? = null)

    companion object {
        val STUB = object : DivLottieLogger {
            override fun log(message: String) = Unit
            override fun fail(message: String, throwable: Throwable?) = Unit
        }
    }
}
