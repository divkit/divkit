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
) : DivCollectionViewHolder(rootView, parentContext, divBinder, viewCreator) {

    override fun bind(bindingContext: BindingContext, div: Div, position: Int, path: DivStatePath) {
        super.bind(bindingContext, div, position, path)
        rootView.setTag(R.id.div_gallery_item_index, position)
        divBinder.attachIndicators()
    }

    override fun updateWrapperLayoutParams(childView: View) {
        rootView.layoutParams = childView.layoutParams
    }

    override fun logReuseError() = KLog.d(TAG) { "Gallery holder reuse failed" }

    companion object {
        const val TAG = "DivGalleryViewHolder"
    }
}
