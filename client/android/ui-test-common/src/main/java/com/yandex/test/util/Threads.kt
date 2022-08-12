package com.yandex.test.util

import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.atomic.AtomicReference

fun <V> performOnMain(action: () -> V): V {
    val value = AtomicReference<V>()
    runOnMainSync { value.set(action()) }
    return value.get()
}

fun runOnMainSync(action: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        action.invoke()
    } else {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action)
    }
}
