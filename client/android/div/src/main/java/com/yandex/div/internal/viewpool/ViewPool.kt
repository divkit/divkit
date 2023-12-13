package com.yandex.div.internal.viewpool

import android.view.View

interface ViewPool {

    fun <T : View> register(tag: String, factory: ViewFactory<T>, capacity: Int)

    fun unregister(tag: String)

    fun <T : View> obtain(tag: String): T

    fun changeCapacity(tag: String, newCapacity: Int)
}
