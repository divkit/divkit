package com.yandex.div.core.view2.divs

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.reuse.util.tryRebindRecycleContainerChildren
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.core.DivBlock

internal abstract class DivCollectionViewHolder(
    private val viewWrapper: DivViewWrapper,
    private val divBinder: DivBinder,
    private val viewCreator: DivViewCreator,
    private val divView: Div2View,
) : RecyclerView.ViewHolder(viewWrapper) {

    protected var oldDivBlock: DivBlock? = null

    open fun bind(divBlock: DivBlock, position: Int) {
        if (viewWrapper.tryRebindRecycleContainerChildren(divView, divBlock.div)) {
            oldDivBlock = divBlock
            return
        }

        val childView = viewWrapper.child
            ?.takeIf { oldDivBlock != null }
            ?.takeIf { child ->
                child.divBlock?.let { DivComparator.areDivsReplaceable(it, divBlock) } == true
            } ?: createChildView(divBlock, divView).also { viewWrapper.addView(it) }

        oldDivBlock = divBlock

        divBinder.bind(childView, divBlock, divView)
        divView.runtimeStore.showWarningIfNeeded(divBlock.div.value())
    }

    private fun createChildView(divBlock: DivBlock, divView: Div2View): View {
        oldDivBlock?.let { logReuseError() }

        viewWrapper.releaseAndRemoveChildren(divView)
        return viewCreator.create(divBlock.div, divBlock.expressionResolver)
    }

    fun updateState() {
        val child = viewWrapper.child ?: return
        child.bindStates(divBinder, divView)
    }

    protected abstract fun logReuseError()
}
