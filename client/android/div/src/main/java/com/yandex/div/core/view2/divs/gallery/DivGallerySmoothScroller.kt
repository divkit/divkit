package com.yandex.div.core.view2.divs.gallery

import android.util.DisplayMetrics
import android.os.SystemClock
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import kotlin.math.abs
import kotlin.math.roundToInt

private const val MILLISECONDS_PER_INCH = 50f // default is 25f, bigger - slower
private const val LINEAR_SCROLL_FRACTION = .3356 // AndroidX's linear-to-decelerating transition.

internal class DivGallerySmoothScroller(
    private val recyclerView: DivRecyclerView,
    private val duration: Int? = null,
): LinearSmoothScroller(recyclerView.context) {

    private var startedAt = 0L
    private val finishAtDeadline = Runnable {
        val position = targetPosition
        recyclerView.stopScroll()
        (recyclerView.layoutManager as? DivGalleryItemHelper)?.instantScroll(position)
    }

    override fun onStart() {
        super.onStart()
        duration?.let {
            startedAt = SystemClock.uptimeMillis()
            recyclerView.postDelayed(finishAtDeadline, it.toLong())
        }
    }

    override fun onStop() {
        recyclerView.removeCallbacks(finishAtDeadline)
        super.onStop()
    }

    override fun calculateTimeForDeceleration(dx: Int): Int {
        if (duration == null) {
            return super.calculateTimeForDeceleration(dx)
        }
        // RecyclerView rejects non-positive action durations, even after the deadline.
        return remainingDuration().coerceAtLeast(1)
    }

    override fun updateActionForInterimTarget(action: RecyclerView.SmoothScroller.Action) {
        if (duration == null) {
            super.updateActionForInterimTarget(action)
            return
        }
        val manager = layoutManager ?: return
        val firstChild = manager.getChildAt(0) ?: return
        val vector = computeScrollVectorForPosition(targetPosition) ?: return
        normalize(vector)
        mTargetVector = vector
        val size = if (manager.canScrollHorizontally()) {
            manager.getDecoratedMeasuredWidth(firstChild)
        } else {
            manager.getDecoratedMeasuredHeight(firstChild)
        }
        // Estimate only the seeking distance; final alignment uses the actual target view.
        val distance = abs(targetPosition - manager.getPosition(firstChild)).toFloat() * size
        mInterimTargetDx = (vector.x * distance).roundToInt()
        mInterimTargetDy = (vector.y * distance).roundToInt()
        // The fractional duration can round to zero; RecyclerView requires a positive value.
        val seekDuration = (remainingDuration() * LINEAR_SCROLL_FRACTION).toInt().coerceAtLeast(1)
        action.update(mInterimTargetDx, mInterimTargetDy, seekDuration, mLinearInterpolator)
    }

    private fun remainingDuration(): Int =
        ((duration ?: 0) - (SystemClock.uptimeMillis() - startedAt)).toInt()

    override fun calculateDxToMakeVisible(view: View, snapPreference: Int) =
        recyclerView.snapHelper.distanceToItem(view)

    override fun calculateDyToMakeVisible(view: View, snapPreference: Int) =
        recyclerView.snapHelper.distanceToItem(view)

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics) =
        MILLISECONDS_PER_INCH / displayMetrics.densityDpi
}
