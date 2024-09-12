package com.yandex.div.core.view2.divs.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.Releasable
import com.yandex.div.core.view2.backbutton.BackHandlingRecyclerView
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.view2.divs.gallery.DivGridLayoutManager
import com.yandex.div.core.view2.divs.gallery.PagerSnapStartHelper
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGallery.ScrollMode
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.ceil

@Mockable
internal class DivRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BackHandlingRecyclerView(ContextThemeWrapper(context, R.style.Div_Gallery), attrs, defStyleAttr),
    DivHolderView<DivGallery> by DivHolderViewMixin(),
    OnInterceptTouchEventListenerHost {

    private var scrollPointerId = -1
    private var pointTouchX = 0
    private var pointTouchY = 0
    var scrollInterceptionAngle = NOT_INTERCEPT
        set(value) {
            field = if (value == NOT_INTERCEPT) {
                NOT_INTERCEPT
            } else {
                abs(value) % 90
            }
        }

    override var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null

    var scrollMode = ScrollMode.DEFAULT
    var pagerSnapStartHelper: PagerSnapStartHelper? = null
    private var needFling = false
    private var beforeScrollFocusPosition = NO_POSITION

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val flingPerformed = super.fling(velocityX, velocityY)

        if (scrollMode == ScrollMode.PAGING) needFling = !flingPerformed
        return flingPerformed
    }

    override fun onScrollStateChanged(state: Int) {
        when (state) {
            SCROLL_STATE_SETTLING -> {
                val focusedChild = focusedChild ?: run {
                    beforeScrollFocusPosition = NO_POSITION
                    return
                }

                beforeScrollFocusPosition = getChildAdapterPosition(focusedChild)
            }
        }
        super.onScrollStateChanged(state)
    }

    override fun onScrolled(dx: Int, dy: Int) {
        // if there was no focus before scroll we should not set extra focus
        if (beforeScrollFocusPosition == NO_POSITION) return

        val orientation = when (val layoutManager = layoutManager) {
            is LinearLayoutManager -> layoutManager.orientation
            is DivGridLayoutManager -> layoutManager.orientation
            else -> HORIZONTAL
        }

        val nextPosition = when {
            orientation == VERTICAL && dy > 0 -> beforeScrollFocusPosition + 1
            orientation == VERTICAL && dy <= 0 -> beforeScrollFocusPosition - 1
            dx > 0 -> beforeScrollFocusPosition + 1
            else -> beforeScrollFocusPosition - 1
        }

        (findViewHolderForAdapterPosition(nextPosition)?.itemView as? DivViewWrapper)
            ?.child?.requestFocus()

        super.onScrolled(dx, dy)
    }

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        if (scrollMode == ScrollMode.PAGING) needFling = true

        val eventResult = super.onTouchEvent(e) && canScroll()
        val action = e?.actionMasked ?: return eventResult

        when(action) {
            MotionEvent.ACTION_UP -> {
                if (scrollMode == ScrollMode.PAGING && needFling) {
                    val layoutManager = this.layoutManager ?: return eventResult
                    val pagerSnapStartHelper = this.pagerSnapStartHelper ?: return eventResult

                    val snapView =
                        pagerSnapStartHelper.findSnapView(layoutManager) ?: return eventResult
                    val position = pagerSnapStartHelper.calculateDistanceToFinalSnap(
                        layoutManager,
                        snapView
                    )

                    if (position.size < 2 || (position[0] == 0 && position[1] == 0)) return eventResult
                    smoothScrollBy(position[0], position[1])
                }
            }
        }

        return eventResult
    }

    private fun canScroll(): Boolean {
        return canScrollHorizontally(-1) || canScrollHorizontally(1) ||
            canScrollVertically(-1) || canScrollVertically(1)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
    }

    override fun release() {
        super.release()
        releaseBorderDrawer()
        val currentAdapter = adapter
        if (currentAdapter is Releasable) {
            currentAdapter.release()
        }
    }

    fun getItemView(index: Int): View? {
        val wrappedChild = this.getChildAt(index) as? ViewGroup ?: return null
        return wrappedChild.getChildAt(0)
    }

    private fun Float.toTouchPoint(): Int {
        return ceil(this).toInt()
    }

    companion object {
        private const val RIGHT_ANGLE = 90.0

        const val NOT_INTERCEPT = 0f
    }
}
