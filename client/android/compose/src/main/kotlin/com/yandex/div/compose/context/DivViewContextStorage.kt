package com.yandex.div.compose.context

import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div2.DivData
import javax.inject.Inject

@DivContextScope
internal class DivViewContextStorage @Inject constructor() {
    private val items = mutableMapOf<DivData, DivViewContext>()
    private val itemsByCardId = mutableMapOf<String, DivViewContext>()

    fun get(data: DivData): DivViewContext? {
        return items[data]
    }

    fun get(cardId: String): DivViewContext? {
        return itemsByCardId[cardId]
    }

    fun put(data: DivData, context: DivViewContext) {
        items[data] = context
        itemsByCardId[data.logId] = context
    }

    fun remove(data: DivData) {
        val context = items.remove(data) ?: return
        itemsByCardId.remove(data.logId)
        context.component.tooltipController.dispose()
    }
}
