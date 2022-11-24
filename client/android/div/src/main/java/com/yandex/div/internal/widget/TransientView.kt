package com.yandex.div.internal.widget

import android.view.View
import android.view.ViewGroup

/**
 * An interface for [View] which may be in a transient state.
 */
interface TransientView {

    /**
     * @return whether this [View] is in a transient state.
     */
    var isTransient: Boolean
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
