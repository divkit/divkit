package com.yandex.div.core.timer

import android.os.Handler
import android.os.Looper

internal class FixedRateScheduler {
    private val handler = Handler(Looper.getMainLooper())

    fun scheduleAtFixedRate(initialDelay: Long, period: Long, onTick: () -> Unit) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, period)
                onTick()
            }
        }, initialDelay)
    }

    fun cancel() {
        handler.removeCallbacksAndMessages(null)
    }
}
