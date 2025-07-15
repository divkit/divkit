package com.yandex.div.core.view2.divs.gallery

import android.view.ViewGroup
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

    private val internalIds = WeakHashMap<DivItemBuilderResult, Long>()
    private var lastItemId = 0L

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivGalleryViewHolder {
        val view = DivGalleryItemLayout(bindingContext.divView.context)
        return DivGalleryViewHolder(bindingContext, view, divBinder, viewCreator)
    }

    override fun getItemId(position: Int): Long {
        val item = visibleItems[position]
        return internalIds[item] ?: (lastItemId++).also { internalIds[item] = it }
    }
}
