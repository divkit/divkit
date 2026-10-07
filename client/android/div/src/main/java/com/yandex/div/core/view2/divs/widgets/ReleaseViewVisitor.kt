package com.yandex.div.core.view2.divs.widgets

import android.view.View
import androidx.annotation.VisibleForTesting
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.util.releasableList
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.Releasable
import javax.inject.Inject

@DivViewScope
internal class ReleaseViewVisitor private constructor(
    private val divView: Div2View,
    private val divCustomContainerViewAdapter: DivCustomContainerViewAdapter,
    private val divExtensionController: DivExtensionController,
    private val onError: ((Throwable) -> Unit)?,
) : DivViewVisitor() {

    @Inject
    constructor(
        divView: Div2View,
        divCustomContainerViewAdapter: DivCustomContainerViewAdapter,
        divExtensionController: DivExtensionController,
    ) : this(divView, divCustomContainerViewAdapter, divExtensionController, onError = null)

    fun withErrorHandler(onError: (Throwable) -> Unit) =
        ReleaseViewVisitor(divView, divCustomContainerViewAdapter, divExtensionController, onError)

    override fun defaultVisit(view: DivHolderView<*>) = releaseInternal(view as View)

    override fun visit(view: DivPagerView) {
        super.visit(view)
        performRelease { view.viewPager.adapter = null }
    }

    override fun visit(view: DivRecyclerView) {
        super.visit(view)
        performRelease { view.adapter = null }
    }

    override fun visit(view: DivCustomWrapper) {
        performRelease { divExtensionController.unbindView(view, divView, onError) }
        val divBlock = view.divBlock ?: return
        release(view)
        view.customView?.let {
            performRelease { divExtensionController.unbindView(it, divView, onError) }
            performRelease { divCustomContainerViewAdapter.release(it, divBlock.divValue) }
        }
    }

    override fun visit(view: View) = release(view)

    private fun releaseInternal(view: View) {
        performRelease { divExtensionController.unbindView(view, divView, onError) }
        release(view)
    }

    private inline fun performRelease(action: () -> Unit) {
        if (onError == null) {
            action()
        } else {
            runCatching(action).onFailure(onError)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun release(view: View) {
        if (view is Releasable) {
            performRelease { view.release() }
        }

        view.releasableList?.forEach { releasable ->
            performRelease { releasable.release() }
        }
    }
}
