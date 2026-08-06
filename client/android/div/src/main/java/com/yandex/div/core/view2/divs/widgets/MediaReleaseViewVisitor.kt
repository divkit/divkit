package com.yandex.div.core.view2.divs.widgets

import android.view.View
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.view.DivImageView
import javax.inject.Inject

@DivViewScope
internal class MediaReleaseViewVisitor @Inject constructor(
    private val bindingDispatcher: BindingDispatcher,
    private val extensionController: DivExtensionController,
    private val divView: Div2View,
) : DivViewVisitor() {

    override fun visit(view: DivVideoView) {
        bindingDispatcher.runMainThreadAction {
            view.releaseMedia()
            defaultVisit(view)
        }
    }

    override fun visit(view: DivImageView) {
        bindingDispatcher.runMainThreadAction {
            view.releaseMedia()
            defaultVisit(view)
        }
    }

    override fun visit(view: DivGifImageView) {
        bindingDispatcher.runMainThreadAction {
            view.releaseMedia()
            defaultVisit(view)
        }
    }

    override fun defaultVisit(view: DivHolderView<*>) {
        val divBlock = view.divBlock ?: return
        extensionController.releaseMedia(view as View, divBlock, divView)
    }
}
