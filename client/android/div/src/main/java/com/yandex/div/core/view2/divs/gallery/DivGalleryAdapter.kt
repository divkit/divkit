package com.yandex.div.core.view2.divs.gallery

import android.view.View
import android.view.ViewGroup
import com.yandex.div.R
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.VisibilityAwareAdapter
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div2.Div
import java.util.WeakHashMap

internal abstract class DivGalleryAdapter<T: Any>(
    items: List<T>,
    private val bindingContext: BindingContext,
    private val divBinder: DivBinder,
    private val viewCreator: DivViewCreator,
    private val itemStateBinder: (itemView: View, div: Div) -> Unit,
) : VisibilityAwareAdapter<DivGalleryBinder.GalleryViewHolder, T>(items, bindingContext) {

    private val ids = WeakHashMap<T, Long>()
    private var lastItemId = 0L

    init {
        @Suppress("LeakingThis")
        setHasStableIds(true)
        subscribeOnElements()
    }

    override fun onViewAttachedToWindow(holder: DivGalleryBinder.GalleryViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.oldDiv?.let { div ->
            itemStateBinder.invoke(holder.rootView, div)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivGalleryBinder.GalleryViewHolder {
        val view = DivViewWrapper(bindingContext.divView.context)
        return DivGalleryBinder.GalleryViewHolder(view, divBinder, viewCreator)
    }

    override fun getItemId(position: Int): Long {
        val item = visibleItems[position]
        return ids[item] ?: (lastItemId++).also { ids[item] = it }
    }

    override fun getItemCount() = visibleItems.size

    override fun onBindViewHolder(holder: DivGalleryBinder.GalleryViewHolder, position: Int) {
        holder.bindItem(position)
        holder.rootView.setTag(R.id.div_gallery_item_index, position)
        divBinder.attachIndicators()
    }
}
