package com.yandex.div.core.view2.divs.gallery

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivCollectionViewHolder
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.KLog
import com.yandex.div2.Div

internal class DivGalleryViewHolder(
    parentContext: BindingContext,
    private val rootView: DivViewWrapper,
    private val divBinder: DivBinder,
    viewCreator: DivViewCreator,
    private val itemStateBinder: (itemView: View, div: Div) -> Unit,
    path: DivStatePath,
) : DivCollectionViewHolder(rootView, parentContext, divBinder, viewCreator, path) {

    override fun bind(bindingContext: BindingContext, div: Div, position: Int) {
        super.bind(bindingContext, div, position)
        rootView.setTag(R.id.div_gallery_item_index, position)
        divBinder.attachIndicators()
    }

    override fun logReuseError() = KLog.d(TAG) { "Gallery holder reuse failed" }

    fun updateState() = oldDiv?.let { itemStateBinder.invoke(rootView, it) }

    companion object {
        const val TAG = "DivGalleryViewHolder"
    }
}
