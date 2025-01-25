package com.yandex.div.core.view2.divs.gallery

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.getChildPathUnit
import com.yandex.div.core.view2.divs.resolvePath
import com.yandex.div.core.view2.divs.resolveRuntime
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.reuse.util.tryRebindRecycleContainerChildren
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.KLog
import com.yandex.div2.Div

internal class DivGalleryViewHolder(
    private val parentContext: BindingContext,
    private val rootView: DivViewWrapper,
    private val divBinder: DivBinder,
    private val viewCreator: DivViewCreator,
    private val itemStateBinder: (itemView: View, div: Div) -> Unit,
    private val path: DivStatePath,
) : RecyclerView.ViewHolder(rootView) {

    private var oldDiv: Div? = null

    private val childrenPaths = mutableMapOf<String, DivStatePath>()

    fun bind(context: BindingContext, div: Div, position: Int) {
        if (rootView.tryRebindRecycleContainerChildren(context.divView, div)) {
            oldDiv = div
            return
        }

        val resolver = context.expressionResolver
        val divView = rootView.child
            ?.takeIf { oldDiv != null }
            ?.takeIf { child ->
                (child as? DivHolderView<*>)?.bindingContext?.expressionResolver?.let {
                    DivComparator.areDivsReplaceable(oldDiv, div, it, resolver)
                } == true
            } ?: createChildView(context, div)

        oldDiv = div
        rootView.setTag(R.id.div_gallery_item_index, position)
        val id = div.value().getChildPathUnit(position)
        val childPath = childrenPaths.getOrPut(id) { div.value().resolvePath(id, path) }

        if (parentContext.expressionResolver != context.expressionResolver) {
            resolveRuntime(
                runtimeStore = context.runtimeStore,
                div = div.value(),
                path = childPath.fullPath,
                resolver = resolver,
                parentResolver = parentContext.expressionResolver,
            )
        }

        context.runtimeStore?.showWarningIfNeeded(div.value())
        divBinder.bind(context, divView, div, childPath)
        divBinder.attachIndicators()
    }

    private fun createChildView(bindingContext: BindingContext, div: Div): View {
        oldDiv?.let {
            KLog.d(TAG) { "Gallery holder reuse failed" }
        }

        rootView.releaseAndRemoveChildren(bindingContext.divView)
        return viewCreator.create(div, bindingContext.expressionResolver).also {
            rootView.addView(it)
        }
    }

    fun updateState() = oldDiv?.let { itemStateBinder.invoke(rootView, it) }

    companion object {
        const val TAG = "DivGalleryViewHolder"
    }
}
