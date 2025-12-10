package com.yandex.div.core.view2.divs.widgets

import android.view.View
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.runBindingAction
import javax.inject.Inject

@DivViewScope
@Mockable
internal class MediaReleaseViewVisitor @Inject constructor(
    private val criticalSection: BindingCriticalSection,
    private val extensionController: DivExtensionController,
) : DivViewVisitor() {

    override fun visit(view: DivVideoView) {
        criticalSection.runBindingAction {
            view.releaseMedia()
            defaultVisit(view)
        }
    }

    override fun visit(view: DivImageView) {
        criticalSection.runBindingAction {
            view.releaseMedia()
            defaultVisit(view)
        }
    }

    override fun visit(view: DivGifImageView) {
        criticalSection.runBindingAction {
            view.releaseMedia()
            defaultVisit(view)
        }
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
