package com.yandex.div.core.view2.divs.widgets

import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.core.view2.Div2View

internal object ReleaseUtils {

    internal fun ViewGroup.releaseAndRemoveChildren(divView: Div2View) {
        this.releaseChildren(divView)
        removeAllViews()
    }

    internal fun ViewGroup.releaseChildren(divView: Div2View) {
        children.forEach {
            divView.releaseViewVisitor.visitViewTree(it)
        }
    }

    internal fun ViewGroup.releaseMedia(divView: Div2View) {
        children.forEach {
            divView.mediaReleaseViewVisitor.visitViewTree(it)
        }
    }
}
