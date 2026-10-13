package com.yandex.div.internal.util

import com.yandex.div.core.annotations.InternalApi

@InternalApi
class NearestItemSearch<T : Any> {
    private var seekerRoute: SearchRoute<T>? = null
    private val searchRoutes = mutableListOf<SearchRoute<T>>()
    private val finishedRoutes = mutableMapOf<T, Int>()

    fun onEnter() {
        searchRoutes.forEach { it.onEnter() }
        seekerRoute?.onEnter()
    }

    fun onLeave() {
        searchRoutes.forEach { it.onLeave() }
        seekerRoute?.onLeave()
    }

    fun onSeeker() {
        searchRoutes.forEach { route ->
            route.item?.let { finishedRoutes[it] = route.distance() }
        }
        searchRoutes.clear()
        seekerRoute = SearchRoute(null)
    }

    fun onItem(item: T) {
        val route = seekerRoute
        if (route != null) {
            finishedRoutes[item] = route.distance()
        } else {
            searchRoutes.add(SearchRoute(item))
        }
    }

    fun nearestItems(): List<T> {
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
