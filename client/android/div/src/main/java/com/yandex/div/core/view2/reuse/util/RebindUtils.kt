package com.yandex.div.core.view2.reuse.util

import android.view.ViewGroup
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div2.Div
import javax.inject.Provider

internal fun ViewGroup.tryRebindRecycleContainerChildren(
    div2View: Div2View,
    div: Div
): Boolean {
    val reusableView = div2View.currentRebindReusableList?.getUniqueView(div) ?: return false

    addView(reusableView)
    return true
}

internal fun ViewGroup.tryRebindPlainContainerChildren(
    divView: Div2View,
    items: List<DivItemBuilderResult>,
    divViewCreator: Provider<DivViewCreator>
): Boolean {
    val reusableList = divView.currentRebindReusableList ?: return false
    removeAllViews()

    for(item in items) {
        val foundReusable = reusableList.getUniqueView(item.div)
        val viewToAdd = foundReusable ?: divViewCreator.get().create(item.div, item.expressionResolver)

        addView(viewToAdd)
    }

    return true
}
