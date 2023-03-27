package com.yandex.div.core.view2.divs.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.math.MathUtils
import androidx.core.view.GestureDetectorCompat
import com.yandex.div.core.Disposable
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.view2.divs.updateBorderDrawer
import com.yandex.div.core.widget.invalidateAfter
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBorder
import com.yandex.div2.DivState
import kotlin.math.abs
import kotlin.math.sign

private const val ANIMATION_TIME = 300f

internal class DivStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), DivBorderSupports, ExpressionSubscriber, TransientView {

    var path: DivStatePath? = null
    val stateId: String?
        get() = path?.lastStateId
    private val swipeListener = SwipeListener()
    private val gestureDetector = GestureDetectorCompat(context, swipeListener, Handler(Looper.getMainLooper()))
    var swipeOutCallback: (() -> Unit)? = null
    internal var divState: DivState? = null
    internal var activeStateDiv: Div? = null

    override var isTransient = false
        set(value) = invalidateAfter {
            field = value
        }

    private var borderDrawer: DivBorderDrawer? = null
    override val border: DivBorder?
        get() = borderDrawer?.border

    override fun getDivBorderDrawer() = borderDrawer

    override val subscriptions = mutableListOf<Disposable>()

    private var isDrawing = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (swipeOutCallback == null) {
            requestDisallowInterceptTouchEvent(false)
            return false
        }
        gestureDetector.onTouchEvent(event)
        requestDisallowInterceptTouchEvent(swipeListener.inScroll)
        return if (swipeListener.inScroll) {
            true
        } else {
            super.onInterceptTouchEvent(event)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (swipeOutCallback == null) {
            requestDisallowInterceptTouchEvent(false)
            return super.onTouchEvent(event)
        }
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            swipeListener.finishSwipe()
        }
        val handled = gestureDetector.onTouchEvent(event)
        return if (handled) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        if (super.canScrollHorizontally(direction)) {
            return true
        }

        if (childCount < 1 || swipeOutCallback == null) {
            return super.canScrollHorizontally(direction)
        }

        val view = getChildAt(0)
        return if (direction < 0) {
            view.translationX <= view.width
        } else {
            -view.translationX <= view.width
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

    private inner class SwipeListener : GestureDetector.SimpleOnGestureListener() {

        private val view: View?
            get() = if (childCount > 0) getChildAt(0) else null
        val inScroll: Boolean
            get() = (view?.translationX ?: 0f) != 0f

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val view = this.view ?: return false
            val scrollDirection = sign(distanceX).toInt()
            if (view.translationX == 0f &&
                abs(distanceX) > 2 * abs(distanceY) &&
                canScroll(view, e1.x, e1.y, scrollDirection)
            ) {
                return false
            }
            view.translationX =
                MathUtils.clamp(view.translationX - distanceX, -view.width.toFloat(), view.width.toFloat())
            return view.translationX != 0f
        }

        fun finishSwipe() {
            val view = this.view ?: return

            val duration: Float
            val targetTranslation: Float
            val animatorListener: Animator.AnimatorListener?
            if (abs(view.translationX) > view.width / 2) {
                duration = ANIMATION_TIME * abs(view.width - view.translationX) / view.width
                targetTranslation = sign(view.translationX) * view.width.toFloat()
                animatorListener = object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        swipeOutCallback?.invoke()
                    }
                }
            } else {
                duration = ANIMATION_TIME * abs(view.translationX) / view.width
                targetTranslation = 0f
                animatorListener = null
            }
            view.animate().cancel()
            view.animate()
                .setDuration(MathUtils.clamp(duration, 0f, ANIMATION_TIME).toLong())
                .translationX(targetTranslation)
                .setListener(animatorListener)
                .start()
        }

        private fun canScroll(v: View, x: Float, y: Float, dir: Int): Boolean {
            if (v is ViewGroup) {
                for (i in v.childCount - 1 downTo 0) {
                    val child = v.getChildAt(i)
                    if (x >= child.left && x < child.right &&
                        y >= child.top && y < child.bottom &&
                        canScroll(child, x - child.left, y - child.top, dir)
                    ) {
                        return true
                    }
                }
            }
            return v.canScrollHorizontally(dir)
        }
    }

    override fun release() {
        super.release()
        borderDrawer?.release()
    }
}
