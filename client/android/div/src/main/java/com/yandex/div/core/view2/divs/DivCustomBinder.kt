package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import com.yandex.div.R
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.DivCustomViewFactory
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.view2.CustomViewStub
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div2.DivCustom
import javax.inject.Inject

internal class DivCustomBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divCustomViewFactory: DivCustomViewFactory,
    private val divCustomViewAdapter: DivCustomViewAdapter? = null,
    private val extensionController: DivExtensionController,
) : DivViewBinder<DivCustom, View> {

    override fun bindView(view: View, div: DivCustom, divView: Div2View) {
        val oldDiv = view.getTag(R.id.div_custom_tag) as? DivCustom

        if (oldDiv == div) return

        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, null, divView)

        if (divCustomViewAdapter?.isCustomTypeSupported(div.customType) == true) {
            divCustomViewAdapter.newBind(view, div, divView)
        } else {
            oldBind(div, divView, view)
        }
    }

    private fun DivCustomViewAdapter.newBind(previousView: View, div: DivCustom, divView: Div2View) {
        val customView = if (previousView !is CustomViewStub && previousView.isSameType(div)) {
            previousView
        } else {
            createView(div, divView).apply {
                setTag(R.id.div_custom_tag, div)
            }
        }
        bindView(customView, div, divView)
        if (previousView != customView) {
            replaceInParent(previousView, customView, div, divView)
        }
        extensionController.bindView(divView, customView, div)
    }

    @Deprecated(message = "for backward compat only", replaceWith = ReplaceWith("DivCustomViewAdapter.newBind"))
    private fun oldBind(
        div: DivCustom,
        divView: Div2View,
        previousView: View
    ) {
        divCustomViewFactory.create(div, divView) { newCustomView ->
            if (newCustomView == previousView) {
                return@create
            }
            replaceInParent(previousView, newCustomView, div, divView)
            extensionController.bindView(divView, newCustomView, div)
        }
    }

    private fun replaceInParent(
        previousView: View,
        newCustomView: View,
        div: DivCustom,
        divView: Div2View
    ) {
        val parent = previousView.parent
        if (parent != null && parent is ViewGroup) {
            val index = parent.indexOfChild(previousView)
            parent.removeView(previousView)
            divView.releaseViewVisitor.visitViewTree(previousView)

            val layoutParams = previousView.layoutParams
            if (layoutParams != null) {
                parent.addView(newCustomView, index, layoutParams)
            } else {
                parent.addView(newCustomView, index)
            }
            baseBinder.bindView(newCustomView, div, null, divView)
        }
    }

    private fun View.isSameType(div: DivCustom): Boolean {
        val prevDiv: DivCustom = getTag(R.id.div_custom_tag) as? DivCustom ?: return false
        return prevDiv.customType == div.customType
    }
}
