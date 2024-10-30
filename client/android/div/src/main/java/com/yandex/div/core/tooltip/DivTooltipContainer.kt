package com.yandex.div.core.tooltip

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.internal.Assert

@Mockable
internal class DivTooltipContainer(
    context: Context,
    val tooltipView: View,
): RelativeLayout(context) {
    private var onWrapperTouchListener: () -> Unit = { }
    private var touchDownOnBackground = false
    private var isInitialized = false

    init {
        addView(tooltipView)
        isInitialized = true
    }

    fun updateLocation(x: Int, y: Int, width: Int, height: Int) {
        val layoutParams = tooltipView.layoutParams as? LayoutParams ?: return

        layoutParams.leftMargin = x
        layoutParams.topMargin = y
        layoutParams.height = height
        layoutParams.width = width

        tooltipView.requestLayout()
    }

    fun setPopupDismissCallback(callback: () -> Unit) {
        onWrapperTouchListener = callback
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)
        super.dispatchDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownOnBackground = isTouchOutsideTooltipView(event)
                return super.onTouchEvent(event)
            }
            MotionEvent.ACTION_UP -> {
                if (touchDownOnBackground && isTouchOutsideTooltipView(event)) {
                    touchDownOnBackground = false
                    onWrapperTouchListener.invoke()
                    super.onTouchEvent(event)
                    return true
                } else {
                    touchDownOnBackground = false
                    return super.onTouchEvent(event)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isTouchOutsideTooltipView(event: MotionEvent?): Boolean {
        event ?: return false
        val child = tooltipView
        val rect = Rect()
        child.getHitRect(rect)

        return !rect.contains(event.x.toInt(), event.y.toInt())
    }


    private fun assertAddingView() =
        Assert.fail("Adding children to DivTooltipContainer is not allowed.")

    private fun assertRemovingView() =
        Assert.fail("Removing children of DivTooltipContainer is not allowed.")

    final override fun addView(child: View?) =
        if (isInitialized) assertAddingView() else super.addView(child)

    final override fun addView(child: View?, index: Int) =
        if (isInitialized) assertAddingView() else super.addView(child, index)

    final override fun addView(child: View?, index: Int, height: Int) =
        if (isInitialized) assertAddingView() else super.addView(child, index, height)

    override fun removeView(view: View?) =
        if (isInitialized) assertRemovingView() else super.removeView(view)

    override fun removeViewAt(index: Int) =
        if (isInitialized) assertRemovingView() else super.removeViewAt(index)
}
