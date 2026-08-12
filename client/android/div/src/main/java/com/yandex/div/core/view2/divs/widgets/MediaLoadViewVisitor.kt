package com.yandex.div.core.view2.divs.widgets

import android.view.View
import androidx.core.view.children
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivGifImageBinder
import com.yandex.div.core.view2.divs.DivImageBinder
import com.yandex.div.core.view2.divs.DivVideoBinder
import com.yandex.div.internal.view.DivImageView
import javax.inject.Inject

@DivViewScope
internal class MediaLoadViewVisitor @Inject constructor(
    private val imageBinder: DivImageBinder,
    private val gifImageBinder: DivGifImageBinder,
    private val videoBinder: DivVideoBinder,
    private val extensionController: DivExtensionController,
    private val divView: Div2View,
) : DivViewVisitor() {

    fun loadMedia() {
        divView.children.forEach { visitViewTree(it) }
    }

    override fun visit(view: DivImageView) {
        val divBlock = view.divBlock ?: return
        imageBinder.loadImage(view, divBlock, divView)
        extensionController.loadMedia(view, divBlock, divView)
    }

    override fun visit(view: DivGifImageView) {
        val divBlock = view.divBlock ?: return
        gifImageBinder.loadGifImage(view, divBlock, divView)
        extensionController.loadMedia(view, divBlock, divView)
    }

    override fun visit(view: DivVideoView) {
        val divBlock = view.divBlock ?: return
        videoBinder.loadVideo(view, divBlock, divView)
        extensionController.loadMedia(view, divBlock, divView)
    }

    override fun defaultVisit(view: DivHolderView<*>) {
        val divBlock = view.divBlock ?: return
        extensionController.loadMedia(view as View, divBlock, divView)
    }
}
