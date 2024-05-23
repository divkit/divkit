package com.yandex.div.core.view2.divs.pager

import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.VisibilityAwareAdapter

internal abstract class DivPagerAdapter<T: Any>(
    items: List<T>,
    private val bindingContext: BindingContext,
    private val divBinder: DivBinder,
    private val translationBinder: (holder: DivPagerBinder.PagerViewHolder, position: Int) -> Unit,
    private val viewCreator: DivViewCreator,
    private val accessibilityEnabled: Boolean,
) : VisibilityAwareAdapter<DivPagerBinder.PagerViewHolder, T>(items, bindingContext) {

    var orientation = ViewPager2.ORIENTATION_HORIZONTAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivPagerBinder.PagerViewHolder {
        val view = DivPagerBinder.PageLayout(bindingContext.divView.context) { orientation }
        view.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        return DivPagerBinder.PagerViewHolder(bindingContext, view, divBinder, viewCreator, accessibilityEnabled)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DivPagerBinder.PagerViewHolder, position: Int) {
        holder.bindItem(position)
        translationBinder.invoke(holder, position)
    }
}
