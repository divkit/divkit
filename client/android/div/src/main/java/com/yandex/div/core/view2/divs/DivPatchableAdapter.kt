package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility
import java.util.WeakHashMap

internal abstract class DivPatchableAdapter<VH : RecyclerView.ViewHolder>(
    divs: List<Div>,
    private val div2View: Div2View,
) : RecyclerView.Adapter<VH>(), ExpressionSubscriber {
    private val _items = divs.toMutableList()
    val items: List<Div>
        get() = _items

    private val visibilityMap = WeakHashMap<Div, DivVisibility>()
    var activeItems = mutableListOf<Div>()

    fun updateActiveItems() {
        visibilityMap.clear()
        activeItems = items.filter { div ->
            div.notGoneDiv(div2View.expressionResolver)
        }.toMutableList()
        items.map { div ->
            div to div.value().visibility.evaluate(div2View.expressionResolver)
        }.forEach { pair ->
            visibilityMap[pair.first] = pair.second
        }
    }

    fun subscribeOnElements() {
        items.forEachIndexed { index, div ->
            addSubscription(
                div.value().visibility.observe(div2View.expressionResolver) { divVisibility ->
                    if (visibilityMap[div] == DivVisibility.GONE) {
                        var position = 0
                        for (i in 0 until index) {
                            if (items[i] != div && visibilityMap[items[i]] == DivVisibility.GONE) {
                                continue
                            }
                            position++
                        }
                        activeItems.add(position, div)
                        notifyItemInserted(position)
                    } else {
                        if (divVisibility == DivVisibility.GONE) {
                            val elementIndex = activeItems.indexOf(div)
                            activeItems.removeAt(elementIndex)
                            notifyItemRemoved(elementIndex)
                        }
                    }
                    visibilityMap[div] = divVisibility
                }
            )
        }
    }

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
        updateActiveItems()
        return isPatchApplied
    }

    private fun Div.notGoneDiv(expressionResolver: ExpressionResolver) =
        this.value().visibility.evaluate(expressionResolver) != DivVisibility.GONE
}
