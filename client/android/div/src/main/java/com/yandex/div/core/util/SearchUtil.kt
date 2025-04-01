package com.yandex.div.core.util

import android.util.Log
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase

private const val TAG = "SearchUtil"

internal class SearchRoute<T>(
    val item: T?,
) {
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

internal inline fun <reified T: DivBase> findNearest(
    rootDiv: Div,
    expressionResolver: ExpressionResolver,
    seeker: DivBase,
    matchCondition: (T) -> Boolean,
): T? {
    var seekerRoute: SearchRoute<T>? = null
    val searchRoutes = mutableListOf<SearchRoute<T>>()
    val finishedRoutes = mutableMapOf<T, Int>()

    val visitor = rootDiv.walk(expressionResolver)
        .onEnter {
            searchRoutes.forEach {
                it.onEnter()
            }
            seekerRoute?.onEnter()
            true
        }.onLeave {
            searchRoutes.forEach {
                it.onLeave()
            }
            seekerRoute?.onLeave()
        }
    val iterator = visitor.asSequence().iterator().withIndex()

    while (iterator.hasNext()) {
        val currentDiv = iterator.next().value.div.value()

        if (currentDiv === seeker) {
            searchRoutes.forEach {
                it.item ?: return@forEach
                finishedRoutes[it.item] = it.distance()
            }
            searchRoutes.clear()
            seekerRoute = SearchRoute(null)
        }
        if (currentDiv is T && matchCondition(currentDiv)) {
            if (seekerRoute != null) {
                finishedRoutes[currentDiv] = seekerRoute.distance()
            } else {
                searchRoutes.add(SearchRoute(currentDiv))
            }
        }
    }

    val minDistance = finishedRoutes.values.minOrNull() ?: return null
    val minDistanceItems = finishedRoutes.filter { it.value == minDistance }.keys

    if (minDistanceItems.isEmpty()) {
        return null
    }

    if (minDistanceItems.size > 1) {
        Log.w(TAG, "Distance clash when searching for the nearest ${T::class.simpleName}. First found is taken")
    }

    return minDistanceItems.first()
}
