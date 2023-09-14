package com.yandex.div.internal.widget

import android.view.View
import android.view.ViewGroup

/**
 * An interface for [View] which may be in a transient state.
 */
interface TransientView {

    /**
     *  Tells view that one transition started
     */
    fun transitionStarted(view: View)

    /**
     * Tells view that one transition finished
     */
    fun transitionFinished(view: View)

    /**
     * @return whether this [View] is in a transient state.
     */
    val isTransient: Boolean
}

class TransientViewMixin(): TransientView {
    private var transitionCount = 0

    override fun transitionStarted(view: View) {
        if (++transitionCount == 1) {
            view.invalidate()
        }
    }

    override fun transitionFinished(view: View) {
        if (transitionCount > 0 && --transitionCount == 0) {
            view.invalidate()
        }
    }

    override val isTransient: Boolean
        get() = transitionCount != 0
}

/**
 * Indicates whether this [View] is [TransientView] in a transient state or a descendant of such kind of view.
 */
internal fun View.isInTransientHierarchy(): Boolean {
    if (this !is TransientView) {
        return false
    }

    if (isTransient) {
        return true
    }

    val parent = parent as? ViewGroup
    return parent?.isInTransientHierarchy() ?: false
}
