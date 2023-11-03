package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import com.yandex.div.R
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.DivCustomViewFactory
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivCustomWrapper
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div2.DivCustom
import javax.inject.Inject

internal class DivCustomBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divCustomViewFactory: DivCustomViewFactory,
    private val divCustomViewAdapter: DivCustomViewAdapter? = null,
    private val divCustomContainerViewAdapter: DivCustomContainerViewAdapter? = null,
    private val extensionController: DivExtensionController,
) : DivViewBinder<DivCustom, DivCustomWrapper> {

    override fun bindView(view: DivCustomWrapper, div: DivCustom, divView: Div2View, path: DivStatePath) {
        val customView = view.customView
        val oldDiv = view.div

        if (oldDiv == div) return

        if (customView != null && oldDiv != null) {
            extensionController.unbindView(divView, customView, oldDiv)
        }

        baseBinder.bindView(view, div, null, divView)
        baseBinder.bindId(view, divView, null)

        if (divCustomContainerViewAdapter?.isCustomTypeSupported(div.customType) == true) {
            bind(view, customView, div, divView,
                { divCustomContainerViewAdapter.createView(div, divView, path) },
                { divCustomContainerViewAdapter.bindView(it, div, divView, path) }
            )
        } else if (divCustomViewAdapter?.isCustomTypeSupported(div.customType) == true) {
            bind(view, customView, div, divView,
                { divCustomViewAdapter.createView(div, divView) },
                { divCustomViewAdapter.bindView(it, div, divView) }
            )
        } else {
            oldBind(div, divView, view, customView)
        }
    }

    private fun bind(
        previousWrapper: DivCustomWrapper,
        oldCustomView: View?,
        div: DivCustom,
        divView: Div2View,
        createView: () -> View,
        bindView: (View) -> Unit
    ) {
        val customView = if (oldCustomView != null && previousWrapper.div?.customType == div.customType) {
            oldCustomView
        } else {
            createView().apply {
                setTag(R.id.div_custom_tag, div)
            }
        }

        bindView(customView)
        baseBinder.bindId(customView, divView, div.id)

        if (oldCustomView != customView) {
            replaceInParent(previousWrapper, customView, divView)
        }
        extensionController.bindView(divView, customView, div)
    }

    @Deprecated(message = "for backward compat only", replaceWith = ReplaceWith("DivCustomViewAdapter.newBind"))
    private fun oldBind(
        div: DivCustom,
        divView: Div2View,
        previousViewGroup: ViewGroup,
        previousCustomView: View?
    ) {
        divCustomViewFactory.create(div, divView) { newCustomView ->
            baseBinder.bindId(newCustomView, divView, div.id)
            if (newCustomView != previousCustomView) {
                replaceInParent(previousViewGroup, newCustomView, divView)
                extensionController.bindView(divView, newCustomView, div)
            }
        }
    }

    private fun replaceInParent(
        parent: ViewGroup,
        newCustomView: View,
        divView: Div2View
    ) {
        if (parent.isNotEmpty()) {
            divView.releaseViewVisitor.visitViewTree(parent[0])
            parent.removeViewAt(0)
        }
        parent.addView(
            newCustomView,
            ViewGroup.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT
            )
        )
    }
}
