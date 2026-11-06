package com.yandex.div.internal.widget.tabs

internal interface PageFactory<TabData : Any, TabView : Any> {

    fun create(data: TabData): Page<TabData, TabView>
}
