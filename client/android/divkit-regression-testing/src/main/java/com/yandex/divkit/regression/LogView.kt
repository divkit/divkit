package com.yandex.divkit.regression

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

internal class LogView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->
                parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.onTouchEvent(event)
    }
}
