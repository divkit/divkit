package com.yandex.div.core.view2.reuse.util

import android.view.ViewGroup
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div2.Div
import javax.inject.Provider

internal fun ViewGroup.tryRebindRecycleContainerChildren(
    div2View: Div2View,
    div: Div
): Boolean {
    val reusableView = div2View.currentRebindReusableList?.getUniqueViewForDiv(div) ?: return false

    addView(reusableView)
    return true
}

internal fun ViewGroup.tryRebindPlainContainerChildren(
    bindingContext: BindingContext,
    items: List<Div>,
    divViewCreator: Provider<DivViewCreator>
): Boolean {
    val reusableList = bindingContext.divView.currentRebindReusableList ?: return false
    removeAllViews()

    for(item in items) {
        val foundReusable = reusableList.getUniqueViewForDiv(item)
        val viewToAdd = foundReusable ?: divViewCreator.get().create(item, bindingContext.expressionResolver)

        addView(viewToAdd)
    }

    return true
}


