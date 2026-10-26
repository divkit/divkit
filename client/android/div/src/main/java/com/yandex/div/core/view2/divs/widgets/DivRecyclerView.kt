package com.yandex.div.core.view2.divs.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.yandex.div.R
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.view2.Releasable
import com.yandex.div.core.view2.backbutton.BackHandlingRecyclerView
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.core.view2.divs.gallery.DivGalleryAdapter
import com.yandex.div.core.view2.divs.gallery.DivGallerySnapHelper
import com.yandex.div.core.view2.divs.gallery.DivGridLayoutManager
import com.yandex.div.core.view2.items.DivScrollActionHolder
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.core.widget.isExact
import com.yandex.div.core.widget.isUnspecified
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost
import com.yandex.div2.Div
import com.yandex.div2.DivGallery.ScrollMode
import com.yandex.div2.DivSize
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.ceil

internal class DivRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BackHandlingRecyclerView(ContextThemeWrapper(context, R.style.Div_Gallery), attrs, defStyleAttr),
    DivHolderView<DivBlock.Gallery> by DivHolderViewMixin(),
    OnInterceptTouchEventListenerHost,
    DivScrollActionHolder {

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
    val snapHelper = DivGallerySnapHelper(this)
    private var needFling = false
    private var beforeScrollFocusPosition = NO_POSITION

    var widthMeasureSpec = 0
    var heightMeasureSpec = 0
    private var parentCrossAxisMeasureSpec = 0
    var considerMatchParent = false

    private var itemAnimationsEnabled = true
    private var suppressItemAnimations = false
    private var savedItemAnimator: ItemAnimator? = itemAnimator

    override fun setItemAnimator(animator: ItemAnimator?) {
        savedItemAnimator = animator
        if (itemAnimationsEnabled && !suppressItemAnimations) {
            super.setItemAnimator(animator)
        }
    }

    fun setItemAnimationsEnabled(enabled: Boolean) {
        itemAnimationsEnabled = enabled
        updateItemAnimator()
    }

    fun suppressItemAnimationsUntilLayout() {
        suppressItemAnimations = true
        updateItemAnimator()
        doOnActualLayout {
            suppressItemAnimations = false
            updateItemAnimator()
        }
    }

    private fun updateItemAnimator() {
        val animator = savedItemAnimator.takeIf { itemAnimationsEnabled && !suppressItemAnimations }
        if (itemAnimator !== animator) {
            super.setItemAnimator(animator)
        }
    }


    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        widthMeasureSpec = widthSpec
        heightMeasureSpec = heightSpec

        val orientation = when (val layoutManager = layoutManager) {
            is LinearLayoutManager -> layoutManager.orientation
            is DivGridLayoutManager -> layoutManager.orientation
            else -> HORIZONTAL
        }
        val isHorizontal = orientation == HORIZONTAL
        parentCrossAxisMeasureSpec = if (isHorizontal) heightSpec else widthSpec
        considerMatchParent = needConsiderMatchParent(parentCrossAxisMeasureSpec, isHorizontal)

        super.onMeasure(widthSpec, heightSpec)

        if (layoutManager == null) return
        if (!needRecalculateMatchParent(parentCrossAxisMeasureSpec, isHorizontal)) return

        considerMatchParent = false
        if (isHorizontal) {
            heightMeasureSpec = makeExactSpec(measuredHeight)
        } else {
            widthMeasureSpec = makeExactSpec(measuredWidth)
        }

        children.forEach {
            val divBlock = it.divBlock ?: return@forEach
            if (!divBlock.div.isCrossAxisMatchParent(isHorizontal)) return@forEach
            it.measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun needConsiderMatchParent(spec: Int, isHorizontal: Boolean): Boolean {
        val items = getItemsIfNeeded(spec) ?: return false
        return items.all { it.div.isCrossAxisMatchParent(isHorizontal) }
    }

    private fun needRecalculateMatchParent(spec: Int, isHorizontal: Boolean): Boolean {
        val items = getItemsIfNeeded(spec) ?: return false
        return items.any { it.div.isCrossAxisMatchParent(isHorizontal) }
    }

    private fun getItemsIfNeeded(spec: Int): List<DivBlock>? {
        if (!isUnspecified(spec)) return null
        val adapter = adapter as? DivGalleryAdapter ?: return null

        return adapter.visibleItems.takeIf { it.isNotEmpty() }
    }

    private fun Div.isCrossAxisMatchParent(isHorizontal: Boolean): Boolean {
        val div = value()
        val size = if (isHorizontal) div.height else div.width
        return size is DivSize.MatchParent
    }

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
        requestCrossAxisRemeasureIfNeeded()

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

    private fun requestCrossAxisRemeasureIfNeeded() {
        if (isExact(parentCrossAxisMeasureSpec)) return

        val layoutManager = layoutManager ?: return
        if (layoutManager.childCount == 0) return

        val isHorizontal = layoutManager.canScrollHorizontally()
        val helper = OrientationHelper.createOrientationHelper(
            layoutManager,
            if (isHorizontal) VERTICAL else HORIZONTAL,
        )

        var maxItemSize = 0
        for (index in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(index) ?: continue
            maxItemSize = maxOf(maxItemSize, helper.getDecoratedMeasurement(child))
        }

        val spanCount = (layoutManager as? DivGridLayoutManager)?.spanCount ?: 1
        val requiredContentSize = maxItemSize * spanCount
        val crossAxisPadding = helper.startAfterPadding + helper.endPadding
        val availableContentSize = if (isUnspecified(parentCrossAxisMeasureSpec)) {
            requiredContentSize
        } else {
            minOf(requiredContentSize, MeasureSpec.getSize(parentCrossAxisMeasureSpec) - crossAxisPadding)
        }
        val targetSize = LayoutManager.chooseSize(
            parentCrossAxisMeasureSpec,
            availableContentSize / spanCount * spanCount + crossAxisPadding,
            if (isHorizontal) minimumHeight else minimumWidth,
        )
        val currentSize = if (isHorizontal) measuredHeight else measuredWidth

        if (targetSize > currentSize) requestLayout()
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
                    val snapView = snapHelper.findSnapView(layoutManager) ?: return eventResult
                    val position = snapHelper.calculateDistanceToFinalSnap(layoutManager, snapView)

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

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child?.isVisible == true) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    override fun release() {
        super.release()
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
