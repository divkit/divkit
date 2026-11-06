package com.yandex.div.internal.widget.tabs

import android.view.ViewGroup

internal class Page<TabData : Any, TabView : Any>(
    val container: ViewGroup,
    private val data: TabData,
    private val bindTabData: (ViewGroup, TabData) -> TabView,
    private val unbindTabData: (TabView) -> Unit,
) {

    /**
     * Bound tab content. Null until the page is needed for display or measurement,
     * and after unbinding releases the content while the page container still exists.
     */
    private var view: TabView? = null

    fun bind() {
        if (view == null) {
            view = bindTabData(container, data)
        }
    }

    fun unbind() {
        val view = view ?: return
        unbindTabData(view)
        this.view = null
    }
}
