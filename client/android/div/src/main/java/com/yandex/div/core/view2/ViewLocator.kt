package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.actions.logError

internal object ViewLocator {

    @JvmStatic
    fun findSingleViewWithTag(divView: Div2View, tag: String): View? {
        val foundViews = findViewsWithTag(divView, tag)
        if (foundViews.isEmpty()) {
            return null
        }
        if (foundViews.size > 1) {
            divView.logError(RuntimeException("Ambiguous scope id. There are ${foundViews.size} divs with id '$tag'"))
            return null
        }
        return foundViews.first()
    }

    @JvmStatic
    fun findViewsWithTag(divView: Div2View, tag: String): List<View> {
        return divView.view.findViewsWithTag(tag)
    }

    private fun View.findViewsWithTag(tag: Any?): List<View> {
        if (tag == null) return emptyList()
        val result = mutableListOf<View>()
        findViewsWithTagTraversal(this, tag, result)
        return result
    }

    private fun findViewsWithTagTraversal(view: View, tag: Any, views: MutableList<View>): List<View> {
        if (tag == view.tag) {
            views += view
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findViewsWithTagTraversal(view.getChildAt(i), tag, views)
            }
        }
        return views
    }
}
