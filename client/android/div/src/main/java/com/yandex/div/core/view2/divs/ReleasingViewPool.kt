package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.core.view2.divs.widgets.visitViewTree

internal class ReleasingViewPool(
    private val releaseViewVisitor: ReleaseViewVisitor
) : RecyclerView.RecycledViewPool() {

    private val viewsSet = mutableSetOf<RecyclerView.ViewHolder>()

    override fun putRecycledView(scrap: RecyclerView.ViewHolder?) {
        super.putRecycledView(scrap)
        if (scrap != null) {
            viewsSet.add(scrap)
        }
    }

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        val view: RecyclerView.ViewHolder = super.getRecycledView(viewType) ?: return null
        viewsSet.remove(view)
        return view
    }

    override fun clear() {
        super.clear()
        viewsSet.forEach { viewHolder: RecyclerView.ViewHolder ->
            releaseViewVisitor.visitViewTree(viewHolder.itemView)
        }
        viewsSet.clear()
    }
}
