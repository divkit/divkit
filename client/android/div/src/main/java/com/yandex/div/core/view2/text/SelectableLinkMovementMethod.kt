package com.yandex.div.core.view2.text

import android.text.Spannable
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView

internal object SelectableLinkMovementMethod : MovementMethod, ArrowKeyMovementMethod() {

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
            val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val offset = layout.getOffsetForHorizontal(line, x.toFloat())
            val links = buffer.getSpans(offset, offset, ClickableSpan::class.java)

            if (links.isNotEmpty()) {
                super.onTouchEvent(widget, buffer, event)
                if (action == MotionEvent.ACTION_UP) {
                    links.forEach { link ->
                        link.onClick(widget)
                    }
                }
                return true
            }
        }

        return super.onTouchEvent(widget, buffer, event)
    }
}
