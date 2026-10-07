package com.yandex.div.core.view2.divs.widgets

import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.util.UiThreadHandler.Companion.executeOnMainThreadBlocking

internal object ReleaseUtils {

    internal fun ViewGroup.releaseAndRemoveChildren(divView: Div2View) = executeOnMainThreadBlocking {
        this.releaseChildren(divView)
        removeAllViews()
    }

    internal fun ViewGroup.releaseAndRemovePreparedChildren(divView: Div2View): Unit = executeOnMainThreadBlocking {
        var releaseError: Throwable? = null
        val visitor = divView.viewComponent.releaseViewVisitor.withErrorHandler { error ->
            val previousError = releaseError
            if (previousError == null) {
                releaseError = error
            } else if (previousError !== error) {
                previousError.addSuppressed(error)
            }
        }
        try {
            children.forEach { visitor.visitViewTree(it) }
        } finally {
            removeAllViews()
        }
        releaseError?.let { throw it }
    }

    internal fun ViewGroup.releaseChildren(divView: Div2View) {
        children.forEach {
            divView.viewComponent.releaseViewVisitor.visitViewTree(it)
        }
    }

    internal fun ViewGroup.releaseMedia(divView: Div2View) {
        if (this === divView) {
            divView.viewComponent.bindingDispatcher.preparedViewHoldersPool.invalidate(divView::logError)
        }
        children.forEach {
            divView.viewComponent.mediaReleaseViewVisitor.visitViewTree(it)
        }
    }
}
