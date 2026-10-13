package com.yandex.div.core.util

import android.util.Log
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.util.NearestItemSearch
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase

private const val TAG = "SearchUtil"

internal inline fun <reified T: DivBase> findNearest(
    rootDiv: Div,
    expressionResolver: ExpressionResolver,
    path: DivStatePath,
    seeker: DivBase,
    matchCondition: (T) -> Boolean,
): T? {
    val search = NearestItemSearch<T>()
    val visitor = rootDiv.walk(expressionResolver, path)
        .onEnter {
            search.onEnter()
            true
        }.onLeave {
            search.onLeave()
        }

    for (block in visitor) {
        val currentDiv = block.div.value()
        if (currentDiv === seeker) search.onSeeker()
        if (currentDiv is T && matchCondition(currentDiv)) search.onItem(currentDiv)
    }

    val nearestItems = search.nearestItems()
    if (nearestItems.size > 1) {
        Log.w(TAG, "Distance clash when searching for the nearest ${T::class.simpleName}. First found is taken")
    }
    return nearestItems.firstOrNull()
}
