package com.yandex.div.internal.widget.tabs

import android.view.View
import android.view.ViewGroup

internal class TabPagesBinder<TabData : Any, TabView : Any>(
    private val pageFactory: PageFactory<TabData, TabView>,
    private val currentItem: () -> Int,
) {

    private val pagesByPlaceholder = mutableMapOf<View, Page<TabData, TabView>>()

    // Measurement and holders reuse the same page until recycling or a data change.
    private val pagesByPosition = mutableMapOf<Int, Page<TabData, TabView>>()

    /**
     * Called by the PageTransformer for pages entering the visible area during a swipe.
     * Binds their content before it is shown without eagerly binding every offscreen page.
     */
    fun bindBySwipeTransitionRequest(placeholder: View) {
        val page = pagesByPlaceholder[placeholder] ?: return
        page.bind()
    }

    /**
     * Called by the height calculator when a tab has no fixed height and its content must
     * be measured. The bound page is reused when the adapter attaches its placeholder.
     */
    fun bindByContainerHeightRequest(position: Int, data: TabData): Page<TabData, TabView> {
        val page = getOrCreatePage(position, data)
        page.bind()
        return page
    }

    fun clear() {
        pagesByPosition.values.forEach { it.unbind() }
        pagesByPlaceholder.keys.forEach { (it as ViewGroup).removeAllViews() }
        pagesByPlaceholder.clear()
        pagesByPosition.clear()
    }

    internal fun bindByAdapterRequest(placeholder: View, position: Int, data: TabData) {
        onPlaceholderRecycled(placeholder)
        val page = getOrCreatePage(position, data)
        (placeholder as ViewGroup).addView(page.container)
        pagesByPlaceholder[placeholder] = page
        // Rebinding at the same position does not guarantee a PageTransformer callback.
        if (position == currentItem()) {
            page.bind()
        }
    }

    internal fun onPlaceholderRecycled(placeholder: View) {
        val page = pagesByPlaceholder.remove(placeholder) ?: return
        page.unbind()
        pagesByPosition.entries.removeAll { it.value === page }
        (placeholder as ViewGroup).removeAllViews()
    }

    private fun getOrCreatePage(position: Int, data: TabData): Page<TabData, TabView> =
        pagesByPosition.getOrPut(position) { pageFactory.create(data) }
}
