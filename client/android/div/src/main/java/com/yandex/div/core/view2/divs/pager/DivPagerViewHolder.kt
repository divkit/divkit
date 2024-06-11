package com.yandex.div.core.view2.divs.pager

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.doOnEveryDetach
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.reuse.util.tryRebindRecycleContainerChildren
import com.yandex.div2.Div

internal class DivPagerViewHolder(
    bindingContext: BindingContext,
    private val frameLayout: ViewGroup,
    private val divBinder: DivBinder,
    private val viewCreator: DivViewCreator,
    private val path: DivStatePath,
    private val accessibilityEnabled: Boolean,
) : RecyclerView.ViewHolder(frameLayout) {

    init {
        itemView.doOnEveryDetach { view ->
            val div = oldDiv ?: return@doOnEveryDetach
            bindingContext.divView.div2Component.visibilityActionTracker
                .startTrackingViewsHierarchy(bindingContext, view, div)
        }
    }

    private var oldDiv: Div? = null

    fun bind(bindingContext: BindingContext, div: Div, position: Int) {
        val resolver = bindingContext.expressionResolver

        if (frameLayout.tryRebindRecycleContainerChildren(bindingContext.divView, div)) {
            oldDiv = div
            return
        }

        val divView = frameLayout.getChildAt(0)
            ?.takeIf { oldDiv != null }
            ?.takeIf { child ->
                (child as? DivHolderView<*>)?.bindingContext?.expressionResolver?.let {
                    DivComparator.areDivsReplaceable(oldDiv, div, it, resolver)
                } == true
            } ?: createChildView(bindingContext, div)

        if (accessibilityEnabled) {
            frameLayout.setTag(R.id.div_pager_item_clip_id, position)
        }
        oldDiv = div
        divBinder.bind(bindingContext, divView, div, path)
    }

    private fun createChildView(bindingContext: BindingContext, div: Div): View {
        frameLayout.releaseAndRemoveChildren(bindingContext.divView)
        return viewCreator.create(div, bindingContext.expressionResolver).also {
            frameLayout.addView(it)
        }
    }
}
