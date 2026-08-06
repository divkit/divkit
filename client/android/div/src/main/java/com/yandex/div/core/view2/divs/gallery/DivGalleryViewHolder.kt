package com.yandex.div.core.view2.divs.gallery

import com.yandex.div.R
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivCollectionViewHolder
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.KLog
import com.yandex.div.internal.core.DivBlock

internal class DivGalleryViewHolder(
    private val rootView: DivViewWrapper,
    private val divBinder: DivBinder,
    viewCreator: DivViewCreator,
    private val divView: Div2View,
) : DivCollectionViewHolder(rootView, divBinder, viewCreator, divView) {

    override fun bind(divBlock: DivBlock, position: Int) {
        super.bind(divBlock, position)
        rootView.setTag(R.id.div_gallery_item_index, position)
        divBinder.attachIndicators(divView)
    }

    override fun logReuseError() = KLog.d(TAG) { "Gallery holder reuse failed" }

    companion object {
        const val TAG = "DivGalleryViewHolder"
    }
}
