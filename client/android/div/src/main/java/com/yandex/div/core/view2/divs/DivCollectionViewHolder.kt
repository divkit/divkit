package com.yandex.div.core.view2.divs

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.reuse.util.tryRebindRecycleContainerChildren
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div2.Div

internal abstract class DivCollectionViewHolder(
    private val viewWrapper: DivViewWrapper,
    private val parentContext: BindingContext,
    private val divBinder: DivBinder,
    private val viewCreator: DivViewCreator,
) : RecyclerView.ViewHolder(viewWrapper) {

    protected var oldDiv: Div? = null

    open fun bind(bindingContext: BindingContext, div: Div, position: Int, path: DivStatePath) {
        val divView = bindingContext.divView
        val resolver = bindingContext.expressionResolver

        if (viewWrapper.tryRebindRecycleContainerChildren(divView, div)) {
            oldDiv = div
            return
        }

        val childView = viewWrapper.child
            ?.takeIf { oldDiv != null }
            ?.takeIf { child ->
                (child as? DivHolderView<*>)?.bindingContext?.expressionResolver?.let {
                    DivComparator.areDivsReplaceable(oldDiv, div, it, resolver)
                } == true
            } ?: createChildView(bindingContext, div).also { viewWrapper.addView(it) }

        oldDiv = div

        divView.runtimeStore.resolveRuntimeWith(path, div, resolver, parentContext.expressionResolver)

        divBinder.bind(bindingContext, childView, div, path)
        divView.runtimeStore.showWarningIfNeeded(div.value())
    }

    private fun createChildView(bindingContext: BindingContext, div: Div): View {
        oldDiv?.let { logReuseError() }

        viewWrapper.releaseAndRemoveChildren(bindingContext.divView)
        return viewCreator.create(div, bindingContext.expressionResolver)
    }

    fun updateState() {
        val child = viewWrapper.child ?: return
        child.bindingContext?.let { child.bindStates(it, divBinder) }
    }

    protected abstract fun logReuseError()
}
