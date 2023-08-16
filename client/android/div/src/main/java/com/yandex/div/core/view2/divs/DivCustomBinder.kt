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
import com.yandex.div.core.view2.divs.widgets.DivFrameLayout
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.internal.KAssert
import com.yandex.div2.DivCustom
import javax.inject.Inject

internal class DivCustomBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divCustomViewFactory: DivCustomViewFactory,
    private val divCustomViewAdapter: DivCustomViewAdapter? = null,
    private val divCustomContainerViewAdapter: DivCustomContainerViewAdapter? = null,
    private val extensionController: DivExtensionController,
) : DivViewBinder<DivCustom, View> {

    override fun bindView(view: View, div: DivCustom, divView: Div2View, path: DivStatePath) {
        if (view !is DivFrameLayout) {
            KAssert.fail { "Wrong view type: custom view should be wrapped inside of DivFrameLayout" }
            return
        }
        val customView = if (view.isNotEmpty()) view[0] else null
        val oldDiv = customView?.getTag(R.id.div_custom_tag) as? DivCustom

        if (oldDiv == div) return

        if (oldDiv != null) baseBinder.unbindExtensions(customView, oldDiv, divView)

        baseBinder.bindView(view, div, null, divView)
        baseBinder.bindId(view, divView, null)

        if (divCustomContainerViewAdapter?.isCustomTypeSupported(div.customType) == true) {
            divCustomContainerViewAdapter.bind(view, customView, div, divView, path)
        } else if (divCustomViewAdapter?.isCustomTypeSupported(div.customType) == true) {
            divCustomViewAdapter.bind(view, customView, div, divView)
        } else {
            oldBind(div, divView, view, customView)
        }
    }

    private fun DivCustomContainerViewAdapter.bind(
            previousViewGroup: ViewGroup,
            oldCustomView: View?,
            div: DivCustom,
            divView: Div2View,
            path: DivStatePath
    ) {
        val customView = if (oldCustomView?.isSameType(div) == true)
            oldCustomView
        else
            createView(div, divView, path).apply {
                setTag(R.id.div_custom_tag, div)
            }
        bindView(customView, div, divView, path)
        if (oldCustomView != customView) {
            replaceInParent(previousViewGroup, customView, div, divView)
        }
        extensionController.bindView(divView, customView, div)
    }

    private fun DivCustomViewAdapter.bind(
        previousViewGroup: ViewGroup,
        oldCustomView: View?,
        div: DivCustom,
        divView: Div2View
    ) {
        val customView = if (oldCustomView?.isSameType(div) == true)
            oldCustomView
        else
            createView(div, divView).apply {
                setTag(R.id.div_custom_tag, div)
            }
        bindView(customView, div, divView)
        if (oldCustomView != customView) {
            replaceInParent(previousViewGroup, customView, div, divView)
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
            if (newCustomView != previousCustomView) {
                replaceInParent(previousViewGroup, newCustomView, div, divView)
                extensionController.bindView(divView, newCustomView, div)
            }
        }
    }

    private fun replaceInParent(
        parent: ViewGroup,
        newCustomView: View,
        div: DivCustom,
        divView: Div2View
    ) {
        baseBinder.bindId(newCustomView, divView, div.id)
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

    private fun View?.isSameType(div: DivCustom): Boolean {
        val prevDiv: DivCustom = this?.getTag(R.id.div_custom_tag) as? DivCustom ?: return false
        return prevDiv.customType == div.customType
    }

    fun setDataWithoutBinding(view: View, div: DivCustom) {
        val container = view as? ViewGroup ?: return
        val customView = if (container.isNotEmpty()) container[0] else return
        customView.setTag(R.id.div_custom_tag, div)
    }
}
