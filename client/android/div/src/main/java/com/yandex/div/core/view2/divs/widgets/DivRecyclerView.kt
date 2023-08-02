package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.Releasable
import com.yandex.div.core.view2.backbutton.BackHandlingRecyclerView
import com.yandex.div.core.view2.divs.PagerSnapStartHelper
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.view2.divs.updateBorderDrawer
import com.yandex.div.core.widget.invalidateAfter
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder
import com.yandex.div2.DivGallery
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.ceil

@Mockable
internal class DivRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BackHandlingRecyclerView(context, attrs, defStyleAttr),
    DivBorderSupports,
    OnInterceptTouchEventListenerHost,
    TransientView,
    ExpressionSubscriber {

    private var scrollPointerId = -1
    private var pointTouchX = 0
    private var pointTouchY = 0
    var scrollInterceptionAngle = NOT_INTERCEPT
        set(value) {
            field = abs(value) % 90
        }

    private var borderDrawer: DivBorderDrawer? = null
    override val border: DivBorder?
        get() = borderDrawer?.border

    override fun getDivBorderDrawer() = borderDrawer

    override var isTransient = false
        set(value) = invalidateAfter {
            field = value
        }

    var div: DivGallery? = null
    override var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null

    var pagerSnapStartHelper: PagerSnapStartHelper? = null

    override val subscriptions = mutableListOf<Disposable>()

    private var isDrawing = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val intercepted = onInterceptTouchEventListener?.onInterceptTouchEvent(target = this, event = event) ?: false

        if (intercepted) return true
        if (scrollInterceptionAngle == NOT_INTERCEPT) {
            return super.onInterceptTouchEvent(event)
        }

        val action = event.actionMasked
        val actionIndex = event.actionIndex

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                scrollPointerId = event.getPointerId(0)
                pointTouchX = event.x.toTouchPoint()
                pointTouchY = event.y.toTouchPoint()
                return super.onInterceptTouchEvent(event)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                scrollPointerId = event.getPointerId(actionIndex)
                pointTouchX = event.getX(actionIndex).toTouchPoint()
                pointTouchY = event.getY(actionIndex).toTouchPoint()
                return super.onInterceptTouchEvent(event)
            }

            MotionEvent.ACTION_MOVE -> {
                val layoutManager = layoutManager ?: return false

                val index = event.findPointerIndex(scrollPointerId)
                if (index < 0) {
                    return false
                }

                val x = event.getX(index).toTouchPoint()
                val y = event.getY(index).toTouchPoint()

                if (scrollState == SCROLL_STATE_DRAGGING) {
                    return super.onInterceptTouchEvent(event)
                }

                val dx = abs(x - pointTouchX)
                val dy = abs(y - pointTouchY)

                if (dx == 0 && dy == 0) return false
                
                val angle = if (dx != 0) {
                    val radian = atan(dy.toDouble() / dx.toDouble())
                    radian * 180 / Math.PI
                } else {
                    RIGHT_ANGLE
                }

                return (layoutManager.canScrollHorizontally() && angle <= scrollInterceptionAngle) ||
                        (layoutManager.canScrollVertically() && angle > scrollInterceptionAngle)
            }
            else -> {
                return super.onInterceptTouchEvent(event)
            }
        }
    }

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        borderDrawer = updateBorderDrawer(border, resolver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderDrawer?.onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        isDrawing = true
        borderDrawer.drawClipped(canvas) { super.draw(canvas) }
        isDrawing = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)

        if (isDrawing) {
            super.dispatchDraw(canvas)
        } else {
            borderDrawer.drawClipped(canvas) { super.dispatchDraw(canvas) }
        }
    }

    override fun release() {
        super.release()
        borderDrawer?.release()
        val currentAdapter = adapter
        if (currentAdapter is Releasable) {
            currentAdapter.release()
        }
    }

    private fun Float.toTouchPoint(): Int {
        return ceil(this).toInt()
    }

    companion object {
        private const val RIGHT_ANGLE = 90.0

        const val NOT_INTERCEPT = -1f
    }
}
