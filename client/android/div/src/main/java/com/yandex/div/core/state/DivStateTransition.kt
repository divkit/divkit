package com.yandex.div.core.state

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.view2.Div2View

/**
 * Transition that excludes child RecyclerViews and doesn't break recycler's scrolls.
 */
@PublicApi
class DivStateTransition @JvmOverloads constructor(
    view: View,
    excludeDivView: Boolean = true
) : ChangeBounds() {

    init {
        view.visit {
            if (it is RecyclerView) {
                excludeChildren(it, true)
            } else if (excludeDivView && it is Div2View) {
                excludeTarget(it, true)
            }
        }
    }
}

private fun View.visit(callback: (View) -> Unit) {
    if (this is ViewGroup) {
        this.children.forEach { it.visit(callback) }
    }
    callback(this)
}
