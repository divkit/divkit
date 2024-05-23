package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.downloader.DivPatchApply
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.VisibilityAwareAdapter.Companion.getVisibility
import com.yandex.div.core.view2.divs.VisibilityAwareAdapter.Companion.isVisible
import com.yandex.div2.Div

internal interface DivPatchableAdapterHelper<VH: RecyclerView.ViewHolder> : DivAdapter<VH, Div> {

    override val visibleDivs get() = visibleItems

    override fun getItemDiv(position: Int) = items[position]

    fun applyPatch(
        recyclerView: RecyclerView?,
        divPatchCache: DivPatchCache,
        bindingContext: BindingContext,
    ): Boolean {
        val patch = divPatchCache.getPatch(bindingContext.divView.dataTag) ?: return false
        val divPatchApply = DivPatchApply(patch)

        val appliedToListPatchIds = mutableSetOf<String>()
        var index = 0
        var activeIndex = 0

        while (index < items.size) {
            val childDiv = items[index]
            val patchId = childDiv.value().id
            val patchDivs = patchId?.let {
                divPatchCache.getPatchDivListById(bindingContext.divView.dataTag, it)
            }

            val isActive = visibilityMap[childDiv] == true

            if (patchDivs != null) {
                items.removeAt(index)
                if (isActive) {
                    notifyItemRemoved(activeIndex)
                }

                items.addAll(index, patchDivs)

                val activeItemsInserted = patchDivs.count { it.getVisibility(bindingContext).isVisible }
                notifyItemRangeInserted(activeIndex, activeItemsInserted)

                index += patchDivs.size - 1
                activeIndex += activeItemsInserted - 1
                appliedToListPatchIds.add(patchId)
            }

            if (isActive) {
                activeIndex++
            }

            index++
        }

        // Apply patch inside items if needed
        patch.patches.keys.filter { it !in appliedToListPatchIds }.forEach { idToFind ->
            for (i in 0 until items.size) {
                val childDiv = items[i]
                divPatchApply.patchDivChild(
                    parentView = recyclerView ?: bindingContext.divView,
                    childDiv,
                    idToFind,
                    bindingContext.expressionResolver
                )?.let { newDiv ->
                    items[i] = newDiv
                    return@forEach
                }
            }
        }

        updateVisibleItems()

        return appliedToListPatchIds.isNotEmpty()
    }
}
