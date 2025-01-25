package com.yandex.div.core.player

import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.DivVisibilityChangeListener
import com.yandex.div2.Div
import com.yandex.div2.DivVideo
import java.lang.ref.WeakReference
import java.util.WeakHashMap

class PlayerViewsVisibilityChangeListener : DivVisibilityChangeListener {
    private var lastVisibleVideoReferences = emptySet<WeakReference<DivPlayerView>>()
    private var lastVisibleViews = WeakHashMap<View, Div>()

    override fun onViewsVisibilityChanged(visibleViews: MutableMap<View, Div>?) {
        if (visibleViews == lastVisibleViews) return

        if (visibleViews.isNullOrEmpty()) {
            lastVisibleVideoReferences.forEach {
                it.get()?.setVisibleOnScreen(false)
            }
            lastVisibleVideoReferences = emptySet()
            lastVisibleViews = WeakHashMap()
        } else {
            val currentVisibleVideos =
                visibleViews.filterValues { it.value() is DivVideo }.keys.mapNotNullTo(HashSet()) { findPlayerView(it) }
            val lastVisibleVideos = lastVisibleVideoReferences.mapNotNullTo(HashSet()) { it.get() }

            if (currentVisibleVideos != lastVisibleVideos) {
                currentVisibleVideos.filter { it !in lastVisibleVideos }.forEach {
                    it.setVisibleOnScreen(true)
                }

                lastVisibleVideos.filter { it !in currentVisibleVideos }.forEach {
                    it.setVisibleOnScreen(false)
                }

                lastVisibleVideoReferences = currentVisibleVideos.mapTo(HashSet()) { WeakReference(it) }
            }
            lastVisibleViews = WeakHashMap(visibleViews)
        }
    }

    private fun findPlayerView(view: View): DivPlayerView? {
        if (view is DivPlayerView) return view
        if (view !is ViewGroup) return null

        for (i in 0 until view.childCount) {
            findPlayerView(view.getChildAt(i))?.let { return it }
        }
        return null
    }
}
