package com.yandex.div.core.view2.divs.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivCollectionAdapter
import com.yandex.div.internal.core.DivItemBuilderResult
import java.util.WeakHashMap

internal class DivGalleryAdapter(
    items: List<DivItemBuilderResult>,
    private val bindingContext: BindingContext,
    private val divBinder: DivBinder,
    private val viewCreator: DivViewCreator,
    path: DivStatePath,
) : DivCollectionAdapter<DivGalleryViewHolder>(bindingContext, path, items) {

    var orientation = RecyclerView.HORIZONTAL
    var columnCount = 1
    var crossSpacing = 0f

    private val internalIds = WeakHashMap<DivItemBuilderResult, Long>()
    private var lastItemId = 0L

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivGalleryViewHolder {
        val view = DivGalleryItemLayout(bindingContext.divView.context)
        view.orientation = { orientation }
        view.columnCount = { columnCount }
        view.crossSpacing = { crossSpacing }
        return DivGalleryViewHolder(bindingContext, view, divBinder, viewCreator)
    }

    override fun getItemId(position: Int): Long {
        val item = visibleItems[position]
        return internalIds[item] ?: (lastItemId++).also { internalIds[item] = it }
    }

    override fun notifyRawItemRemoved(position: Int) {
        notifyItemRemoved(position)
        if (columnCount == 1) {
            notifyEdgeDecorationUpdateOnRemove(position)
        }
    }

    override fun notifyRawItemInserted(position: Int) {
        notifyItemInserted(position)
        if (columnCount == 1) {
            notifyEdgeDecorationUpdateOnInsert(position)
        }
    }

    /**
     * [PaddingItemDecoration] applies different offsets to the first and last items in a
     * single-column gallery. When an edge item is inserted or removed, the adjacent item must
     * be rebound so its decoration offsets are recalculated.
     */
    private fun notifyEdgeDecorationUpdateOnRemove(removedPosition: Int) {
        when {
            removedPosition == 0 && itemCount > 0 -> notifyRawItemChanged(0)
            removedPosition == itemCount -> notifyRawItemChanged(removedPosition - 1)
        }
    }

    private fun notifyEdgeDecorationUpdateOnInsert(insertedPosition: Int) {
        when {
            insertedPosition == 0 && itemCount > 1 -> notifyRawItemChanged(1)
            insertedPosition == itemCount - 1 && insertedPosition > 0 -> notifyRawItemChanged(insertedPosition - 1)
        }
    }
}
