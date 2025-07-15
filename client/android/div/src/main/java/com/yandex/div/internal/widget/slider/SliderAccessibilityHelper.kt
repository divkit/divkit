package com.yandex.div.internal.widget.slider

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_INT
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import com.yandex.div.R
import com.yandex.div.internal.widget.slider.SliderView.Companion.boundsHeight
import com.yandex.div.internal.widget.slider.SliderView.Companion.boundsWidth
import com.yandex.div.internal.widget.slider.SliderView.Thumb
import kotlin.math.max
import kotlin.math.roundToInt

private const val THUMB_VIRTUAL_VIEW_ID = 0
private const val SECONDARY_THUMB_VIRTUAL_VIEW_ID = 1

/**
 * Provides info about virtual view hierarchy for accessibility services.
 */
internal class SliderAccessibilityHelper(private val slider: SliderView) : ExploreByTouchHelper(slider) {

    private val bounds = Rect()
    private val step get() = max(((slider.maxValue - slider.minValue) * 0.05).roundToInt(), 1)

    init {
        ViewCompat.setAccessibilityDelegate(slider, this)
        slider.accessibilityLiveRegion = ACCESSIBILITY_LIVE_REGION_POLITE
    }

    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) = Unit

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        if (x < slider.paddingLeft) return THUMB_VIRTUAL_VIEW_ID

        return when (slider.getClosestThumb(x.toInt())) {
            Thumb.THUMB -> THUMB_VIRTUAL_VIEW_ID
            Thumb.THUMB_SECONDARY -> SECONDARY_THUMB_VIRTUAL_VIEW_ID
        }
    }

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        virtualViewIds.add(THUMB_VIRTUAL_VIEW_ID)
        slider.thumbSecondaryValue?.let {
            virtualViewIds.add(SECONDARY_THUMB_VIRTUAL_VIEW_ID)
        }
    }

    override fun onPopulateNodeForVirtualView(
        virtualViewId: Int,
        node: AccessibilityNodeInfoCompat
    ) {
        node.apply {
            className = SeekBar::class.java.name
            rangeInfo = AccessibilityNodeInfoCompat.RangeInfoCompat.obtain(
                RANGE_TYPE_INT,
                slider.minValue,
                slider.maxValue,
                virtualViewId.toThumbValue()
            )

            val description = StringBuilder()
            slider.contentDescription?.let { description.append(it).append(",") }
            description.append(startOrEndDescription(virtualViewId))
            contentDescription = description.toString()

            addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD)
            addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD)

            updateBounds(virtualViewId)
            setBoundsInParent(bounds)
        }
    }

    override fun onPerformActionForVirtualView(virtualViewId: Int, action: Int, arguments: Bundle?): Boolean {
        val value = when (action) {
            android.R.id.accessibilityActionSetProgress -> {
                if (arguments?.containsKey(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_PROGRESS_VALUE) != true) {
                    return false
                }
                arguments.getFloat(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_PROGRESS_VALUE)
            }

            AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD -> virtualViewId.toThumbValue() + step
            AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD -> virtualViewId.toThumbValue() - step
            else -> return false
        }
        setThumbValue(virtualViewId, value)
        return true
    }

    private fun startOrEndDescription(virtualViewId: Int): String {
        return when {
            slider.thumbSecondaryValue == null -> ""
            virtualViewId == THUMB_VIRTUAL_VIEW_ID -> slider.context.getString(R.string.div_slider_range_start)
            virtualViewId == SECONDARY_THUMB_VIRTUAL_VIEW_ID -> slider.context.getString(R.string.div_slider_range_end)
            else -> ""
        }
    }

    private fun updateBounds(index: Int) {
        val width: Int
        val height: Int
        when (index) {
            SECONDARY_THUMB_VIRTUAL_VIEW_ID -> {
                width = slider.thumbSecondaryDrawable.boundsWidth
                height = slider.thumbSecondaryDrawable.boundsHeight
            }
            else -> {
                width = slider.thumbDrawable.boundsWidth
                height = slider.thumbDrawable.boundsHeight
            }
        }

        val position = slider.getPositionInView(index.toThumbValue())
        bounds.apply {
            left = position
            right = position + width
            top = (slider.height + slider.paddingTop - slider.paddingBottom - height) / 2
            bottom = (slider.height + slider.paddingTop - slider.paddingBottom + height) / 2
        }
    }

    private fun setThumbValue(virtualViewId: Int, value: Float) {
        slider.setValueToAccessibilityThumb(virtualViewId.toThumb(), value)
        sendEventForVirtualView(virtualViewId, AccessibilityEvent.TYPE_VIEW_SELECTED)
        invalidateVirtualView(virtualViewId)
    }

    private fun Int.toThumb(): Thumb {
        return when {
            this == THUMB_VIRTUAL_VIEW_ID -> Thumb.THUMB
            slider.thumbSecondaryValue != null -> Thumb.THUMB_SECONDARY
            else -> Thumb.THUMB
        }
    }

    private fun Int.toThumbValue(): Float {
        return if (this == THUMB_VIRTUAL_VIEW_ID) {
            slider.thumbValue
        } else {
            slider.thumbSecondaryValue ?: slider.thumbValue
        }
    }
}
