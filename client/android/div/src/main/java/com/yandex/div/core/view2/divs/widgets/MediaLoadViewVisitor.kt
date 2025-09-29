package com.yandex.div.core.view2.divs.widgets

import androidx.core.view.children
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivGifImageBinder
import com.yandex.div.core.view2.divs.DivImageBinder
import com.yandex.div.core.view2.divs.DivVideoBinder
import com.yandex.div.core.view2.errors.ErrorCollectors
import javax.inject.Inject

@DivViewScope
@Mockable
internal class MediaLoadViewVisitor @Inject constructor(
    private val imageBinder: DivImageBinder,
    private val gifImageBinder: DivGifImageBinder,
    private val videoBinder: DivVideoBinder,
    private val errorCollectors: ErrorCollectors,
) : DivViewVisitor() {

    fun loadMedia(divView: Div2View) {
        divView.children.forEach { visit(it) }
    }

    override fun visit(view: DivImageView) {
        val bindingContext = view.bindingContext ?: return
        val div = view.div ?: return
        val errorCollector = errorCollectors.getOrCreate(bindingContext.divView.dataTag, bindingContext.divView.divData)
        imageBinder.loadImage(view, bindingContext, div.value, errorCollector)
    }

    override fun visit(view: DivGifImageView) {
        val bindingContext = view.bindingContext ?: return
        val div = view.div ?: return
        val errorCollector = errorCollectors.getOrCreate(bindingContext.divView.dataTag, bindingContext.divView.divData)
        gifImageBinder.loadGifImage(view, bindingContext, div.value, errorCollector)
    }

    override fun visit(view: DivVideoView) {
        val bindingContext = view.bindingContext ?: return
        val div = view.div ?: return
        val path = view.path ?: return
        videoBinder.loadVideo(view, bindingContext, div.value, path)
    }
}
