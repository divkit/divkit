package com.yandex.div.core.view2.animations

import android.view.MotionEvent
import android.view.View

internal fun interface TouchAnimationListener {
    fun onTouch(target: View, event: MotionEvent)
}
