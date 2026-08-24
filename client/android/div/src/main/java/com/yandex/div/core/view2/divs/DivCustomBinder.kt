package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import com.yandex.div.R
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivCustomWrapper
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.util.UiThreadHandler.Companion.executeOnMainThreadBlocking
import javax.inject.Inject
import javax.inject.Provider

internal class DivCustomBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divCustomContainerViewAdapter: DivCustomContainerViewAdapter,
    private val extensionController: DivExtensionController,
    private val divBinder: Provider<DivBinder>,
) : DivViewBinder<DivBlock.Custom, DivCustomWrapper>(baseBinder) {

    override fun bindView(view: DivCustomWrapper, divBlock: DivBlock.Custom, divView: Div2View) {
        val customView = view.customView
        val oldDivBlock = view.divBlock

        if (oldDivBlock?.div === divBlock.div) {
            view.bindStates(divBinder.get(), divView)
            return
        }

        if (oldDivBlock != null && divView.complexRebindInProgress &&
            DivComparator.areValuesReplaceable(oldDivBlock, divBlock)) {
            view.bindStates(divBinder.get(), divView)
            return
        }

        if (customView != null && oldDivBlock != null) {
            extensionController.unbindView(customView, oldDivBlock, divView)
        }

        baseBinder.bindView(view, divBlock, oldDivBlock, divView)
        baseBinder.bindId(divView, view, null)

        val divValue = divBlock.divValue
        executeOnMainThreadBlocking {
            if (divCustomContainerViewAdapter.isCustomTypeSupported(divValue.customType)) {
                bind(
                    previousWrapper = view,
                    oldCustomView = customView,
                    oldDivBlock = oldDivBlock,
                    divBlock = divBlock,
                    divView = divView,
                    createView = {
                        divCustomContainerViewAdapter.createView(
                            divValue,
                            divView,
                            divBlock.expressionResolver,
                            divBlock.path,
                        )
                    },
                    bindView = {
                        divCustomContainerViewAdapter.bindView(
                            it,
                            divValue,
                            divView,
                            divBlock.expressionResolver,
                            divBlock.path,
                        )
                    },
                )
            }
        }
    }

    private fun bind(
        previousWrapper: DivCustomWrapper,
        oldCustomView: View?,
        oldDivBlock: DivBlock.Custom?,
        divBlock: DivBlock.Custom,
        divView: Div2View,
        createView: () -> View,
        bindView: (View) -> Unit
    ) {
        val div = divBlock.divValue
        val customView = if (oldCustomView != null && canReuseCustomView(oldDivBlock, divBlock)) {
            oldCustomView
        } else {
            executeOnMainThreadBlocking {
                createView().apply {
                    setTag(R.id.div_custom_tag, div)
                }
            }
        }

        if (oldCustomView != customView) {
            replaceInParent(previousWrapper, customView, divView)
        }

        executeOnMainThreadBlocking {
            bindView(customView)
        }
        baseBinder.bindId(divView, customView, div.id)

        extensionController.bindView(customView, divBlock, divView)
    }

    private fun canReuseCustomView(old: DivBlock.Custom?, new: DivBlock.Custom): Boolean {
        return old?.divValue?.customType == new.divValue.customType &&
            old.divValue.nonNullItems.size == new.divValue.nonNullItems.size
    }

    private fun replaceInParent(
        parent: ViewGroup,
        newCustomView: View,
        divView: Div2View
    ) {
        if (parent.isNotEmpty()) {
            divView.viewComponent.releaseViewVisitor.visitViewTree(parent[0])
            parent.removeViewAt(0)
        }
        parent.addView(newCustomView)
    }
}
