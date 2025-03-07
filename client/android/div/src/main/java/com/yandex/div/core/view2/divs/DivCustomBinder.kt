package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import com.yandex.div.R
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivCustomWrapper
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div2.Div
import com.yandex.div2.DivCustom
import javax.inject.Inject
import javax.inject.Provider

internal class DivCustomBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divCustomContainerViewAdapter: DivCustomContainerViewAdapter,
    private val extensionController: DivExtensionController,
    private val divBinder: Provider<DivBinder>,
) : DivViewBinder<Div.Custom, DivCustom, DivCustomWrapper>(baseBinder) {

    override fun bindView(context: BindingContext, view: DivCustomWrapper, div: Div.Custom, path: DivStatePath) {
        val customView = view.customView
        val oldDiv = view.div
        val divView = context.divView
        val resolver = context.expressionResolver

        if (oldDiv === div) {
            view.bindStates(divView.rootDiv(), context, resolver, divBinder.get())
            return
        }

        if (customView != null && oldDiv != null) {
            view.bindingContext?.expressionResolver?.let {
                extensionController.unbindView(divView, it, customView, oldDiv.value())
            }
        }

        baseBinder.bindView(context, view, div, oldDiv)
        baseBinder.bindId(divView, view, null)

        val divValue = div.value
        if (divCustomContainerViewAdapter.isCustomTypeSupported(divValue.customType)) {
            bind(view, customView, oldDiv?.value, divValue, context,
                { divCustomContainerViewAdapter.createView(divValue, divView, resolver, path) },
                { divCustomContainerViewAdapter.bindView(it, divValue, divView, resolver, path) }
            )
        }
    }

    private fun bind(
        previousWrapper: DivCustomWrapper,
        oldCustomView: View?,
        oldDiv: DivCustom?,
        div: DivCustom,
        context: BindingContext,
        createView: () -> View,
        bindView: (View) -> Unit
    ) {
        val customView = if (oldCustomView != null && previousWrapper.div?.value?.customType == div.customType
                && oldDiv?.nonNullItems?.size == div.nonNullItems.size
        ) {
            oldCustomView
        } else {
            createView().apply {
                setTag(R.id.div_custom_tag, div)
            }
        }

        val divView = context.divView

        if (oldCustomView != customView) {
            replaceInParent(previousWrapper, customView, divView)
        }

        bindView(customView)
        baseBinder.bindId(divView, customView, div.id)

        extensionController.bindView(divView, context.expressionResolver, customView, div)
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
        parent.addView(newCustomView)
    }
}
