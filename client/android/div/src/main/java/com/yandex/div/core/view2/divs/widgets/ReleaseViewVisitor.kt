package com.yandex.div.core.view2.divs.widgets

import android.view.View
import androidx.annotation.VisibleForTesting
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.util.releasableList
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.Releasable
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivViewScope
@Mockable
internal class ReleaseViewVisitor @Inject constructor(
    private val divView: Div2View,
    private val divCustomViewAdapter: DivCustomViewAdapter,
    private val divCustomContainerViewAdapter: DivCustomContainerViewAdapter,
    private val divExtensionController: DivExtensionController,
) : DivViewVisitor() {

    override fun defaultVisit(view: DivHolderView<*>) = releaseInternal(view as View, view.div)

    override fun visit(view: DivCustomWrapper) {
        val divCustom = view.div ?: return
        release(view)
        view.customView?.let {
            divExtensionController.unbindView(divView, it, divCustom)
            divCustomViewAdapter.release(it, divCustom)
            divCustomContainerViewAdapter?.release(it, divCustom)
        }
    }

    override fun visit(view: View) = release(view)

    private fun releaseInternal(view: View, div: DivBase?) {
        if (div != null) {
            divExtensionController.unbindView(divView, view, div)
        }
        release(view)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun release(view: View) {
        if (view is Releasable) {
            view.release()
        }

        view.releasableList?.forEach { releasable ->
            releasable.release()
        }
    }
}
