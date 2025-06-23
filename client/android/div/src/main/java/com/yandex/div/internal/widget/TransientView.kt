package com.yandex.div.internal.widget

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.view.allViews
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports

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

class TransientViewMixin: TransientView {

    private var transitionCount = 0

    override fun transitionStarted(view: View) {
        if (++transitionCount == 1) {
            invalidateView(view)
        }
    }

    override fun transitionFinished(view: View) {
        if (transitionCount > 0 && --transitionCount == 0) {
            invalidateView(view)
        }
    }

    private fun invalidateView(view: View) {
        view.invalidate()
        view.allViews
            .filterIsInstance<DivBorderSupports>()
            .forEach { it.invalidateBorder() }
    }

    override val isTransient: Boolean
        get() = transitionCount != 0
}

/**
 * Indicates whether this [View] is [TransientView] in a transient state.
 */
internal fun View.isTransient(): Boolean {
    return this is TransientView && isTransient
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

    return parent is ViewGroup && parent.isInTransientHierarchy()
}

internal fun ViewParent.isInTransientHierarchy(): Boolean {
    return this is TransientView && (this as View).isInTransientHierarchy()
}
