package com.yandex.div.render.common.tree

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public class NearestItemSearch<T : Any> {
    private var seekerRoute: SearchRoute<T>? = null
    private val searchRoutes = mutableListOf<SearchRoute<T>>()
    private val finishedRoutes = mutableMapOf<T, Int>()

    public fun onEnter() {
        searchRoutes.forEach { it.onEnter() }
        seekerRoute?.onEnter()
    }

    public fun onLeave() {
        searchRoutes.forEach { it.onLeave() }
        seekerRoute?.onLeave()
    }

    public fun onSeeker() {
        searchRoutes.forEach { route ->
            route.item?.let { finishedRoutes[it] = route.distance() }
        }
        searchRoutes.clear()
        seekerRoute = SearchRoute(null)
    }

    public fun onItem(item: T) {
        val route = seekerRoute
        if (route != null) {
            finishedRoutes[item] = route.distance()
        } else {
            searchRoutes.add(SearchRoute(item))
        }
    }

    public fun nearestItems(): List<T> {
        val minDistance = finishedRoutes.values.minOrNull() ?: return emptyList()
        return finishedRoutes.filterValues { it == minDistance }.keys.toList()
    }

    private class SearchRoute<T>(val item: T?) {
        private var enterLeaveBalance = 0
        private var movedDistance = 0

        fun onEnter() {
            enterLeaveBalance++
            movedDistance++
        }

        fun onLeave() {
            if (enterLeaveBalance > 0) {
                enterLeaveBalance--
                movedDistance--
            } else {
                movedDistance++
            }
        }

        fun distance() = movedDistance
    }
}
