package com.yandex.div.core.view2.divs.gallery

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView

private const val MILLISECONDS_PER_INCH = 50f // default is 25f, bigger - slower

internal class DivGallerySmoothScroller(
    private val recyclerView: DivRecyclerView
): LinearSmoothScroller(recyclerView.context) {

    override fun calculateDxToMakeVisible(view: View, snapPreference: Int) =
        recyclerView.snapHelper.distanceToItem(view)

    override fun calculateDyToMakeVisible(view: View, snapPreference: Int) =
        recyclerView.snapHelper.distanceToItem(view)

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics) =
        MILLISECONDS_PER_INCH / displayMetrics.densityDpi
}
