package com.yandex.divkit.demo.div.editor

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.ViewTreeObserver

private const val DEBOUNCE_TOKEN = "ya_token"
private const val DEFAULT_OFFSET = 250L

class DebounceOnViewDrawObserver(
    private val callback: Runnable
) : ViewTreeObserver.OnDrawListener {

    private val handler = Handler(Looper.getMainLooper())

    override fun onDraw() {
        handler.removeCallbacksAndMessages(DEBOUNCE_TOKEN)
        handler.postAtTime(callback, DEBOUNCE_TOKEN, SystemClock.uptimeMillis() + DEFAULT_OFFSET)
    }
}