package com.yandex.div.internal.widget.slider

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Region
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Px
import com.yandex.div.core.ObserverList
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.internal.widget.slider.shapes.TextDrawable
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val DEFAULT_MIN_VALUE = 0f
private const val DEFAULT_MAX_VALUE = 100f
private const val MIN_POSSIBLE_RANGE = 1f
private const val UNSET_VALUE = -1
private const val DEFAULT_ANIMATION_DURATION = 300L
private const val DEFAULT_ANIMATION_ENABLED = true
private const val DEFAULT_INTERCEPTION_ANGLE = 45f
private const val RIGHT_ANGLE = 90.0

open class SliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val sliderDrawDelegate = SliderDrawDelegate()

    interface ChangedListener {
        fun onThumbValueChanged(value: Float) = Unit
        fun onThumbSecondaryValueChanged(value: Float?) = Unit
    }

    private val listeners = ObserverList<ChangedListener>()

    private fun notifyThumbChangedListeners(prevValue: Float?, newValue: Float) {
        if (prevValue != newValue) {
            listeners.forEach { it.onThumbValueChanged(newValue) }
        }
    }

    private fun notifyThumbSecondaryChangedListeners(prevValue: Float?, newValue: Float?) {
        if (prevValue != newValue) {
            listeners.forEach { it.onThumbSecondaryValueChanged(newValue) }
        }
    }

    private var sliderAnimator: ValueAnimator? = null
    private var sliderSecondaryAnimator: ValueAnimator? = null

    private val animatorListener = object : Animator.AnimatorListener {

        var prevThumbValue: Float = DEFAULT_MIN_VALUE

        private var hasCanceled = false

        override fun onAnimationEnd(animation: Animator) {
            sliderAnimator = null
            if (!hasCanceled) {
                notifyThumbChangedListeners(prevThumbValue, thumbValue)
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            hasCanceled = true
        }

        override fun onAnimationStart(animation: Animator) {
            hasCanceled = false
        }

        override fun onAnimationRepeat(animation: Animator) = Unit
    }
    private val animatorSecondaryListener = object : Animator.AnimatorListener {

        var prevThumbSecondaryValue: Float? = null

        private var hasCanceled = false

        override fun onAnimationEnd(animation: Animator) {
            sliderSecondaryAnimator = null
            if (!hasCanceled) {
                notifyThumbSecondaryChangedListeners(
                    prevThumbSecondaryValue,
                    thumbSecondaryValue
                )
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            hasCanceled = true
        }

        override fun onAnimationStart(animation: Animator) {
            hasCanceled = false
        }

        override fun onAnimationRepeat(animation: Animator) = Unit
    }

    val ranges = mutableListOf<Range>()

    /**
     * The length of the animation. The default duration is 300 milliseconds.
     */
    var animationDuration: Long = DEFAULT_ANIMATION_DURATION
        set(value) {
            if (field == value || value < 0) return
            field = value
        }

    /**
     * An interpolator defines the rate of change of an animation.
     * This allows the basic animation effects (alpha, scale, translate, rotate) to be accelerated,
     * decelerated, repeated, etc.
     */
    var animationInterpolator = AccelerateDecelerateInterpolator()

    /**
     * Animation enabled flag.
     */
    var animationEnabled = DEFAULT_ANIMATION_ENABLED

    /**
     * Value of slider start point.
     */
    var minValue = DEFAULT_MIN_VALUE
        set(value) {
            if (field == value) return
            maxValue = max(maxValue, value + MIN_POSSIBLE_RANGE)
            field = value
            setThumbsInBoarders()
            invalidate()
        }

    /**
     * Value of slider end point.
     */
    var maxValue = DEFAULT_MAX_VALUE
        set(value) {
            if (field == value) return
            minValue = min(minValue, value - MIN_POSSIBLE_RANGE)
            field = value
            setThumbsInBoarders()
            invalidate()
        }

    /**
     * The appearance of reached tick marks.
     */
    var activeTickMarkDrawable: Drawable? = null
        set(value) {
            field = value
            maxTickmarkOrThumbWidth = UNSET_VALUE
            setThumbsOnTickMarks()
            invalidate()
        }

    /**
     * The appearance of not reached tick marks.
     */
    var inactiveTickMarkDrawable: Drawable? = null
        set(value) {
            field = value
            maxTickmarkOrThumbWidth = UNSET_VALUE
            setThumbsOnTickMarks()
            invalidate()
        }

    /**
     * The appearance of active track.
     */
    var activeTrackDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * The appearance of inactive track.
     */
    var inactiveTrackDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * The value of thumb. Should be in range from [minValue] to [maxValue].
     */
    var thumbValue: Float = minValue
        private set

    /**
     * Set the value of thumb.
     * @param value should be in range from [minValue] to [maxValue]
     * @param animated change value with animation
     */
    fun setThumbValue(value: Float, animated: Boolean = animationEnabled) {
        trySetThumbValue(value, animated, forced = true)
    }

    /**
     * Tries to set the value of thumb.
     * @param value should be in range from [minValue] to [maxValue]
     * @param animated change value with animation
     * @param forced if [animated] is false and [forced] is true,
     * then value will be set regardless of the running animation
     */
    private fun trySetThumbValue(
        value: Float,
        animated: Boolean = animationEnabled,
        forced: Boolean,
    ) {
        val newValue = value.inBoarders()
        if (thumbValue == newValue) return
        if (animated && animationEnabled) {
            if (sliderAnimator == null) {
                animatorListener.prevThumbValue = thumbValue
            }
            sliderAnimator?.cancel()
            sliderAnimator = ValueAnimator.ofFloat(thumbValue, newValue).apply {
                addUpdateListener {
                    thumbValue = it.animatedValue as Float
                    postInvalidateOnAnimation()
                }
                addListener(animatorListener)
                setBaseParams()
                start()
            }
        } else {
            if (forced) {
                sliderAnimator?.cancel()
            }
            if (forced || sliderAnimator == null) {
                animatorListener.prevThumbValue = thumbValue
                thumbValue = newValue
                notifyThumbChangedListeners(animatorListener.prevThumbValue, thumbValue)
            }
        }
        invalidate()
    }

    /**
     * The appearance of thumb.
     */
    var thumbDrawable: Drawable? = null
        set(drawable) {
            field = drawable
            maxTickmarkOrThumbWidth = UNSET_VALUE
            invalidate()
        }

    /**
     * The appearance of thumb text.
     * Returns [null] if doesn't exist.
     */
    var thumbTextDrawable: TextDrawable? = null
        set(drawable) {
            field = drawable
            invalidate()
        }

    /**
     * The value of thumb secondary. Should be in range from [minValue] to [maxValue].
     * Returns [null] if doesn't exist.
     */
    var thumbSecondaryValue: Float? = null
        private set

    /**
     * Set the value of thumb secondary.
     * @param value should be in range from [minValue] to [maxValue] or be [null]
     * @param animated change value with animation
     */
    fun setThumbSecondaryValue(value: Float?, animated: Boolean = animationEnabled) {
        trySetThumbSecondaryValue(value, animated, forced = true)
    }

    /**
     * Tries to set the value of thumb secondary.
     * @param value should be in range from [minValue] to [maxValue] or be [null]
     * @param animated change value with animation
     * @param forced if [animated] is false and [forced] is true,
     * then value will be set regardless of the running animation
     */
    private fun trySetThumbSecondaryValue(
        value: Float?,
        animated: Boolean,
        forced: Boolean,
    ) {
        val newValue = value?.inBoarders()
        if (thumbSecondaryValue == newValue) return
        if (animated && animationEnabled && thumbSecondaryValue != null && newValue != null) {
            if (sliderSecondaryAnimator == null) {
                animatorSecondaryListener.prevThumbSecondaryValue = thumbSecondaryValue
            }
            sliderSecondaryAnimator?.cancel()
            sliderSecondaryAnimator = ValueAnimator.ofFloat(thumbSecondaryValue!!, newValue).apply {
                addUpdateListener {
                    thumbSecondaryValue = it.animatedValue as Float
                    postInvalidateOnAnimation()
                }
                addListener(animatorSecondaryListener)
                setBaseParams()
                start()
            }
        } else {
            if (forced) {
                sliderSecondaryAnimator?.cancel()
            }
            if (forced || sliderSecondaryAnimator == null) {
                animatorSecondaryListener.prevThumbSecondaryValue = thumbSecondaryValue
                thumbSecondaryValue = newValue
                notifyThumbSecondaryChangedListeners(
                    animatorSecondaryListener.prevThumbSecondaryValue,
                    thumbSecondaryValue
                )
            }
        }
        invalidate()
    }

    /**
     * The appearance of thumb secondary.
     * Returns [null] if doesn't exist.
     */
    var thumbSecondaryDrawable: Drawable? = null
        set(value) {
            field = value
            maxTickmarkOrThumbWidth = UNSET_VALUE
            invalidate()
        }

    /**
     * The appearance of thumb secondary text.
     * Returns [null] if doesn't exist.
     */
    var thumbSecondTextDrawable: TextDrawable? = null
        set(drawable) {
            field = drawable
            invalidate()
        }

    /**
     * Cached maximum width of thumb or tickmark drawables, used for an additional
     * horizontal padding in if tickmark or thumb is knobbing out of track.
     */
    private var maxTickmarkOrThumbWidth: Int = UNSET_VALUE
        get() {
            if (field == UNSET_VALUE) {
                val tickmarkWidth = max(activeTickMarkDrawable.boundsWidth, inactiveTickMarkDrawable.boundsWidth)
                val thumbWidth = max(thumbDrawable.boundsWidth, thumbSecondaryDrawable.boundsWidth)
                field = max(tickmarkWidth, thumbWidth)
            }
            return field
        }

    private val activeRange = ActiveRange()

    fun addOnThumbChangedListener(listener: ChangedListener) {
        listeners.addObserver(listener)
    }

    fun removeOnChangedListener(listener: ChangedListener) {
        listeners.removeObserver(listener)
    }

    fun clearOnThumbChangedListener() = listeners.clear()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val measuredWidth = measureDimension(desiredWidth, widthMeasureSpec)
        val measuredHeight = measureDimension(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)

        sliderDrawDelegate.onMeasure(
            getTrackLength(measuredWidth),
            measuredHeight - paddingTop - paddingBottom
        )

        ranges.forEach {
            with(it) {
                startPosition = max(startValue, minValue).toPosition(measuredWidth) + marginStart
                endPosition = min(endValue, maxValue).toPosition(measuredWidth) - marginEnd
            }
        }
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        return when(mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> min(desiredSize, size)
            else -> desiredSize
        }
    }

    override fun getSuggestedMinimumHeight(): Int {
        val trackHeight = max(activeTrackDrawable.boundsHeight, inactiveTrackDrawable.boundsHeight)
        val rangesHeight = ranges.maxOfOrNull {
            max(it.activeTrackDrawable.boundsHeight, it.inactiveTrackDrawable.boundsHeight)
        } ?: 0
        val thumbHeight = max(thumbDrawable.boundsHeight, thumbSecondaryDrawable.boundsHeight)
        return maxOf(thumbHeight, trackHeight, rangesHeight)
    }

    private val Drawable?.boundsWidth get() = this?.bounds?.width() ?: 0

    private val Drawable?.boundsHeight get() = this?.bounds?.height() ?: 0

    override fun getSuggestedMinimumWidth(): Int {
        val tickCount = (maxValue - minValue + 1).toInt()

        val trackWidth = max(activeTrackDrawable.boundsWidth, inactiveTrackDrawable.boundsWidth) * tickCount
        val thumbWidth = max(thumbDrawable.boundsWidth, thumbSecondaryDrawable.boundsWidth)
        val maxThumbTrack = max(thumbWidth, trackWidth)

        val textWidth = thumbTextDrawable?.intrinsicWidth ?: 0
        val secondaryTextWidth = thumbSecondTextDrawable?.intrinsicWidth ?: 0
        val textMax = max(textWidth, secondaryTextWidth)

        return max(maxThumbTrack, textMax)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(paddingLeft.toFloat() + maxTickmarkOrThumbWidth / 2, paddingTop.toFloat())
        val count = canvas.save()
        ranges.forEach {
            @Suppress("DEPRECATION")
            canvas.clipRect(
                (it.startPosition - it.marginStart).toFloat(),
                0f,
                (it.endPosition + it.marginEnd).toFloat(),
                height.toFloat(),
                Region.Op.DIFFERENCE
            )
        }
        sliderDrawDelegate.drawInactiveTrack(canvas, inactiveTrackDrawable)
        val start = activeRange.start
        val end = activeRange.end
        val startPosition = start.toPosition()
        val endPosition = end.toPosition()
        sliderDrawDelegate.drawTrackPart(
            canvas,
            activeTrackDrawable,
            startPosition.coerceAtMost(endPosition),
            endPosition.coerceAtLeast(startPosition)
        )
        canvas.restoreToCount(count)
        ranges.forEach {
            fun drawTrackPart(drawable: Drawable?, start: Int = it.startPosition, end: Int = it.endPosition) {
                sliderDrawDelegate.drawTrackPart(canvas, drawable, start, end)
            }
            when {
                it.endPosition < startPosition || it.startPosition > endPosition -> drawTrackPart(it.inactiveTrackDrawable)
                it.startPosition >= startPosition && it.endPosition <= endPosition -> drawTrackPart(it.activeTrackDrawable)
                it.startPosition < startPosition && it.endPosition <= endPosition -> {
                    drawTrackPart(it.inactiveTrackDrawable, end = (startPosition - 1).coerceAtLeast(it.startPosition))
                    drawTrackPart(it.activeTrackDrawable, start = startPosition)
                }
                it.startPosition >= startPosition && it.endPosition > endPosition -> {
                    drawTrackPart(it.activeTrackDrawable, end = endPosition)
                    drawTrackPart(it.inactiveTrackDrawable, start = (endPosition + 1).coerceAtMost(it.endPosition))
                }
                else -> {
                    drawTrackPart(it.inactiveTrackDrawable)
                    drawTrackPart(it.activeTrackDrawable, startPosition, endPosition)
                }
            }
        }
        for (i in minValue.toInt()..maxValue.toInt()) {
            val tickmarkDrawable = if (i in start.toInt()..end.toInt()) {
                activeTickMarkDrawable
            } else {
                inactiveTickMarkDrawable
            }
            sliderDrawDelegate.drawOnPosition(canvas, tickmarkDrawable, i.toPosition())
        }

        sliderDrawDelegate.drawThumb(
            canvas,
            thumbValue.toPosition(),
            thumbDrawable,
            thumbValue.toInt(),
            thumbTextDrawable
        )
        if (isThumbSecondaryEnabled()) {
            sliderDrawDelegate.drawThumb(
                canvas,
                thumbSecondaryValue!!.toPosition(),
                thumbSecondaryDrawable,
                thumbSecondaryValue!!.toInt(),
                thumbSecondTextDrawable
            )
        }
        canvas.restore()
    }

    private var thumbOnTouch = Thumb.THUMB

    var interactive = true

    var interceptionAngle = DEFAULT_INTERCEPTION_ANGLE
        set(value) {
            val newValue = value.absoluteValue % 90
            field = maxOf(DEFAULT_INTERCEPTION_ANGLE, newValue)
        }

    private var prevX = 0f
    private var prevY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!interactive) {
            return false
        }

        val touchPosition = ev.x.toInt() - paddingLeft - (maxTickmarkOrThumbWidth / 2)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                thumbOnTouch = getClosestThumb(touchPosition)
                setValueToThumb(thumbOnTouch, getTouchValue(touchPosition), animationEnabled)
                prevX = ev.x
                prevY = ev.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                setValueToThumb(thumbOnTouch, getTouchValue(touchPosition), animated = false, forced = true)
                val dy = (ev.y - prevY).absoluteValue
                val dx = (ev.x - prevX).absoluteValue
                val angle = if (dx != 0f) {
                    val radian = atan(dy / dx)
                    radian * 180 / Math.PI
                } else {
                    RIGHT_ANGLE
                }
                prevX = ev.x
                prevY = ev.y
                this.parent.requestDisallowInterceptTouchEvent(
                    angle <= interceptionAngle || dy < touchSlop
                )
                return true
            }
            MotionEvent.ACTION_UP -> {
                setValueToThumb(thumbOnTouch, getTouchValue(touchPosition), animationEnabled)
                return true
            }
            else -> Unit
        }
        return false
    }

    private fun getClosestThumb(position: Int): Thumb {
        if (!isThumbSecondaryEnabled()) {
            return Thumb.THUMB
        }
        val distanceToThumb = abs(position - thumbValue.toPosition())
        val distanceToThumbSecondary = abs(position - thumbSecondaryValue!!.toPosition())
        return if (distanceToThumb < distanceToThumbSecondary) {
            Thumb.THUMB
        } else {
            Thumb.THUMB_SECONDARY
        }
    }

    private fun setValueToThumb(thumb: Thumb, value: Float, animated: Boolean, forced: Boolean = false) =
        when (thumb) {
            Thumb.THUMB -> trySetThumbValue(value, animated, forced)
            Thumb.THUMB_SECONDARY -> trySetThumbSecondaryValue(value, animated, forced)
        }

    private fun getTouchValue(position: Int): Float {
        return if (inactiveTickMarkDrawable != null || activeTickMarkDrawable != null) {
            position.toValue().roundToInt().toFloat()
        } else {
            position.toValue()
        }
    }

    private fun isThumbSecondaryEnabled() = thumbSecondaryValue != null

    private fun setThumbsInBoarders() {
        trySetThumbValue(thumbValue.inBoarders(), animated = false, forced = true)
        if (isThumbSecondaryEnabled()) trySetThumbSecondaryValue(
            thumbSecondaryValue?.inBoarders(),
            animated = false,
            forced = true
        )
    }

    private fun setThumbsOnTickMarks() {
        trySetThumbValue(thumbValue.roundToInt().toFloat(), animated = false, forced = true)
        thumbSecondaryValue?.let { trySetThumbSecondaryValue(
            it.roundToInt().toFloat(),
            animated = false,
            forced = true
        ) }
    }

    /**
     * Calculates horizontal position of the point related to the specified value.
     */
    @Px
    private fun Float.toPosition(viewWidth: Int = width): Int {
        return (getTrackLength(viewWidth) / (maxValue - minValue) *
            if (isLayoutRtl()) maxValue - this else this - minValue).roundToInt()
    }

    private fun getTrackLength(viewWidth: Int = width) =
        viewWidth - paddingLeft - paddingRight - maxTickmarkOrThumbWidth

    @Px
    private fun Int.toPosition(): Int = this.toFloat().toPosition()

    /**
     * Calculates slider value on given horizontal position of the point.
     */
    private fun Int.toValue(): Float {
        return minValue + (this * (maxValue - minValue) / getTrackLength()).let {
            if (isLayoutRtl()) maxValue - it - 1 else it
        }
    }

    private fun Float.inBoarders() = min(max(this, minValue), maxValue)

    private fun ValueAnimator.setBaseParams() {
        duration = animationDuration
        interpolator = animationInterpolator
    }

    /**
     * Calculates slider active range presented by two values, where start <= end.
     * If there is only one thumb then its value will be considered as the end of the range.
     */
    private inner class ActiveRange {
        val start: Float
            get() {
                if (!isThumbSecondaryEnabled()) {
                    return minValue
                }
                return min(thumbValue, thumbSecondaryValue)
            }
        val end: Float
            get() {
                if (!isThumbSecondaryEnabled()) {
                    return thumbValue
                }
                return max(thumbValue, thumbSecondaryValue)
            }

        private fun min(one: Float, another: Float?): Float {
            another ?: return one
            return kotlin.math.min(one, another)
        }

        private fun max(one: Float, another: Float?): Float {
            another ?: return one
            return kotlin.math.max(one, another)
        }
    }

    private enum class Thumb {
        THUMB, THUMB_SECONDARY
    }

    class Range {
        var startValue = 0f
        var endValue = 0f

        @Px var marginStart = 0
        @Px var marginEnd = 0

        var activeTrackDrawable: Drawable? = null
        var inactiveTrackDrawable: Drawable? = null

        @Px var startPosition = 0
        @Px var endPosition = 0
    }
}
