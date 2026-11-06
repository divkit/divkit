package com.yandex.div.internal.widget.tabs

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

internal class TabsPageChangeCallback(
    private val onSelectionChanged: Runnable,
) : ViewPager2.OnPageChangeCallback() {

    private var selectedPosition = 0
    private val listeners = mutableListOf<ViewPager.OnPageChangeListener>()

    var isDispatchingSelection = false
        private set

    fun resetSelection(position: Int) {
        selectedPosition = position
    }

    fun add(listener: ViewPager.OnPageChangeListener) {
        listeners.add(listener)
    }

    fun clear() {
        listeners.clear()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        listeners.toList().forEach { listener ->
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        if (selectedPosition == position) {
            return
        }
        selectedPosition = position
        isDispatchingSelection = true
        listeners.toList().forEach { listener ->
            listener.onPageSelected(position)
        }
        isDispatchingSelection = false
        onSelectionChanged.run()
    }

    override fun onPageScrollStateChanged(state: Int) {
        listeners.toList().forEach { listener ->
            listener.onPageScrollStateChanged(state)
        }
    }
}
