package com.yandex.div.internal.widget.tabs

import android.view.ViewGroup
import com.yandex.div.internal.viewpool.ViewPool

internal class TabPageFactory<TabData : Any, TabView : Any>(
    private val viewPool: ViewPool,
    private val tabItemTag: String,
    private val bindTabData: (ViewGroup, TabData) -> TabView,
    private val unbindTabData: (TabView) -> Unit,
) : PageFactory<TabData, TabView> {

    override fun create(data: TabData) = Page<TabData, TabView>(
        container = viewPool.obtain(tabItemTag),
        data = data,
        bindTabData = bindTabData,
        unbindTabData = unbindTabData,
    )
}
