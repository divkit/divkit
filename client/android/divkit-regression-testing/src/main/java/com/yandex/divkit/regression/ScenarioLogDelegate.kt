package com.yandex.divkit.regression

fun interface ScenarioLogDelegate {

    fun println(message: String)

    object Stub : ScenarioLogDelegate {
        override fun println(message: String) = Unit
    }
}
