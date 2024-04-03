package com.yandex.div.core.view2.divs.widgets

import android.view.MotionEvent
import android.view.ViewGroup
import com.yandex.div.internal.widget.OnInterceptTouchEventListener

internal object ParentScrollRestrictor: OnInterceptTouchEventListener {
    override fun onInterceptTouchEvent(target: ViewGroup, event: MotionEvent): Boolean {
        val parent = target.parent ?: return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }
}
