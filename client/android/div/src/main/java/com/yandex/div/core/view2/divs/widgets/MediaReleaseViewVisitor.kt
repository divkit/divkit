package com.yandex.div.core.view2.divs.widgets

import android.view.View
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.extension.DivExtensionController
import javax.inject.Inject

@DivViewScope
@Mockable
internal class MediaReleaseViewVisitor @Inject constructor(
    private val extensionController: DivExtensionController,
) : DivViewVisitor() {

    override fun visit(view: DivVideoView) {
        view.releaseMedia()
        defaultVisit(view)
    }

    override fun visit(view: DivImageView) {
        view.releaseMedia()
        defaultVisit(view)
    }

    override fun visit(view: DivGifImageView) {
        view.releaseMedia()
        defaultVisit(view)
    }

    override fun defaultVisit(view: DivHolderView<*>) {
        val bindingContext = view.bindingContext ?: return
        val div = view.div ?: return
        extensionController.releaseMedia(
            bindingContext.divView,
            bindingContext.expressionResolver,
            view as View,
            div.value()
        )
    }
}
