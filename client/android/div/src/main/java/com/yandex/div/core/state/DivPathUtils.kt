package com.yandex.div.core.state

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div2.Div
import com.yandex.div2.DivState

internal object DivPathUtils {

    /**
     * Attempt to receive [DivStateLayout] within given hierarchy of views, searching by
     * [DivStatePath.pathToLastState], so [DivStatePath] equal to 0/first_state/second_div can return
     * [DivStateLayout] with path 0/first_state/first_div, as it very same layout but with different active states.
     */
    internal fun View.findStateLayout(path: DivStatePath): DivStateLayout? {
        if (this !is ViewGroup) {
            return null
        }
        if (this is DivStateLayout && this.path?.pathToLastState == path.pathToLastState) {
            return this
        }
        for (child in children) {
            val childView: DivStateLayout? = child.findStateLayout(path)
            if (childView != null) {
                return childView
            }
        }
        return null
    }

    /**
     * @return Finds corresponding to this path [Div] within given [Div] hierarchy, or null if failed. For example,
     * for [DivStatePath] equal to 0/first_state/first_div/second_state/second_div [Div] corresponding to
     * 0/first_state/first_div/second_state will be returned.
     */
    internal fun Div.findDivState(path: DivStatePath): Div? {
        val states = path.getStates()
        if (states.isEmpty()) {
            return null
        }
        var foundDiv: Div? = this
        states.forEach { (divId, _) ->
            foundDiv = foundDiv?.findByPath(divId) ?: return null
        }
        return foundDiv
    }

    /**
     * If one path is an ancestor of another, keeps only ancestor
     */
    internal fun compactPathList(paths: List<DivStatePath>): List<DivStatePath> {
        if (paths.isEmpty()) {
            return paths
        }

        val sortedPaths = paths.sortedWith(DivStatePath.alphabeticalComparator())
        return sortedPaths.scan(sortedPaths.first()) { acc, path ->
            if (acc.isAncestorOf(path)) acc else path
        }.distinct()
    }

    private fun Div.findByPath(divId: String): Div? {
        return when (this) {
            is Div.State -> {
                if (value.getId() == divId) {
                    this
                } else {
                    value.states.mapNotNull { it.div }.findRecursively(divId)
                }
            }
            is Div.Tabs -> value.items.map { it.div }.findRecursively(divId)
            is Div.Container -> value.items.findRecursively(divId)
            is Div.Grid -> value.items.findRecursively(divId)
            is Div.Gallery -> value.items.findRecursively(divId)
            is Div.Pager -> value.items.findRecursively(divId)
            is Div.Custom -> null
            is Div.Text -> null
            is Div.Image -> null
            is Div.Slider -> null
            is Div.Input -> null
            is Div.GifImage -> null
            is Div.Indicator -> null
            is Div.Separator -> null
            is Div.Select -> null
            is Div.Video -> null
        }
    }

    private fun Iterable<Div>.findRecursively(divId: String): Div? {
        forEach { div: Div ->
            val state = div.findByPath(divId)
            if (state != null) {
                return state
            }
        }
        return null
    }

    internal fun DivState.getId(errorCallback: (() -> Unit)? = null): String = divId ?: id ?: run {
        errorCallback?.invoke()
        ""
    }
}
