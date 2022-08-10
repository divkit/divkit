package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.Div

internal abstract class DivPatchableAdapter<VH : RecyclerView.ViewHolder>(
    divs: List<Div>,
    private val div2View: Div2View,
) : RecyclerView.Adapter<VH>() {
    private val _items = divs.toMutableList()
    val items: List<Div>
        get() = _items

    fun applyPatch(divPatchCache: DivPatchCache): Boolean {
        if (divPatchCache.getPatch(div2View.dataTag) == null) return false

        var isPatchApplied = false
        var index = 0
        while (index < _items.size) {
            val childDiv = _items[index]
            val childDivId = childDiv.value().id
            if (childDivId != null) {
                val patchDivs = divPatchCache.getPatchDivListById(div2View.dataTag, childDivId)
                if (patchDivs != null) {
                    _items.removeAt(index)
                    _items.addAll(index, patchDivs)
                    notifyItemRangeChanged(index, patchDivs.size + 1)
                    index += patchDivs.size - 1
                    isPatchApplied = true
                }
            }
            index += 1
        }
        return isPatchApplied
    }
}
