package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.downloader.DivPatchApply
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.toDivItemBuilderResult

internal abstract class DivCollectionAdapter<VH: RecyclerView.ViewHolder>(
    items: List<DivItemBuilderResult>,
) : VisibilityAwareAdapter<VH>(items) {

    fun applyPatch(
        recyclerView: RecyclerView?,
        divPatchCache: DivPatchCache,
        bindingContext: BindingContext,
    ): Boolean {
        val patch = divPatchCache.getPatch(bindingContext.divView.dataTag) ?: return false
        val divPatchApply = DivPatchApply(patch)

        val appliedToListPatchIds = mutableSetOf<String>()
        var index = 0
        var visibleIndex = 0

        while (index < items.size) {
            val childItem = items[index]
            val patchId = childItem.div.value().id
            val patchDivs = patchId?.let {
                divPatchCache.getPatchDivListById(bindingContext.divView.dataTag, it)
            }

            if (patchDivs != null) {
                items.removeAt(index)
                if (childItem.isVisible) {
                    notifyRawItemRemoved(visibleIndex)
                }

                items.addAll(index, patchDivs.toDivItemBuilderResult(bindingContext.expressionResolver))

                val activeItemsInserted = patchDivs.count {
                    it.value().visibility.evaluate(bindingContext.expressionResolver).isVisible
                }
                notifyRawItemRangeInserted(visibleIndex, activeItemsInserted)

                index += patchDivs.size - 1
                visibleIndex += activeItemsInserted - 1
                appliedToListPatchIds.add(patchId)
            }

            if (childItem.isVisible) {
                visibleIndex++
            }

            index++
        }

        // Apply patch inside items if needed
        patch.patches.keys.filter { it !in appliedToListPatchIds }.forEach { idToFind ->
            for (i in 0 until items.size) {
                val childDiv = items[i].div
                divPatchApply.patchDivChild(
                    parentView = recyclerView ?: bindingContext.divView,
                    childDiv,
                    idToFind,
                    bindingContext.expressionResolver
                )?.let { newDiv ->
                    items[i] = DivItemBuilderResult(newDiv, bindingContext.expressionResolver)
                    return@forEach
                }
            }
        }

        updateVisibleItems()

        if (appliedToListPatchIds.isEmpty()) return false

        closeAllSubscription()
        subscribeOnElements()
        return true
    }
}
