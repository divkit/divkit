package com.yandex.div.core.util

import android.view.View

internal interface ViewProperty<T> {
    fun get(): T
}

internal inline fun <T> View.property(crossinline block: View.() -> T): ViewProperty<T> {
    return object : ViewProperty<T> {
        override fun get(): T = block(this@property)
    }
}

internal fun View.widthProperty(): ViewProperty<Int> {
    return property { if (width > 0) width else measuredWidth }
}

internal fun View.heightProperty(): ViewProperty<Int> {
    return property { if (height > 0) height else measuredHeight }
}
