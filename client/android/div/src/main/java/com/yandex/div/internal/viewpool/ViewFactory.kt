package com.yandex.div.internal.viewpool

import android.view.View

fun interface ViewFactory<T : View> {
    fun createView(): T
}
