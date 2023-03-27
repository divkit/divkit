package com.yandex.div.core.view2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.view2.divs.isWrapContainer
import com.yandex.div.core.view2.divs.widgets.DivFrameLayout
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.core.view2.divs.widgets.DivSliderView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.DivWrapLayout
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.internal.viewpool.ViewPool
import com.yandex.div.internal.widget.tabs.TabsLayout
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContainer.Orientation
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import javax.inject.Inject
import javax.inject.Named

@DivScope
@Mockable
internal class DivViewCreator @Inject constructor(
        @Named(Names.THEMED_CONTEXT) private val context: Context,
        private val viewPool: ViewPool,
        private val validator: DivValidator
) : DivVisitor<View>() {

    init {
        viewPool.register(TAG_TEXT, { DivLineHeightTextView(context) }, 20)
        viewPool.register(TAG_IMAGE, { DivImageView(context) }, 20)
        viewPool.register(TAG_GIF_IMAGE, { DivGifImageView(context) }, 3)
        viewPool.register(TAG_OVERLAP_CONTAINER, { DivFrameLayout(context) }, 8)
        viewPool.register(TAG_LINEAR_CONTAINER, { DivLinearLayout(context) }, 12)
        viewPool.register(TAG_WRAP_CONTAINER, { DivWrapLayout(context) }, 4)
        viewPool.register(TAG_GRID, { DivGridLayout(context) }, 4)
        viewPool.register(TAG_GALLERY, { DivRecyclerView(context) }, 6)
        viewPool.register(TAG_PAGER, { DivPagerView(context) }, 2)
        viewPool.register(TAG_TABS, { TabsLayout(context) }, 2)
        viewPool.register(TAG_STATE, { DivStateLayout(context) }, 4)
        viewPool.register(TAG_CUSTOM, { DivFrameLayout(context) }, 2)
        viewPool.register(TAG_INDICATOR, { DivPagerIndicatorView(context) }, 2)
        viewPool.register(TAG_SLIDER, { DivSliderView(context) }, 2)
        viewPool.register(TAG_INPUT, { DivInputView(context) }, 2)
    }

    fun create(div: Div, resolver: ExpressionResolver): View {
        return if (validator.validate(div, resolver)) {
            visit(div, resolver)
        } else {
            Space(context)
        }
    }

    override fun visit(data: DivText, resolver: ExpressionResolver): View = viewPool.obtain(TAG_TEXT)

    override fun visit(data: DivImage, resolver: ExpressionResolver): View = viewPool.obtain(TAG_IMAGE)

    override fun visit(data: DivGifImage, resolver: ExpressionResolver): View = viewPool.obtain(TAG_GIF_IMAGE)

    override fun visit(data: DivSeparator, resolver: ExpressionResolver): View = DivSeparatorView(context)

    override fun visit(data: DivContainer, resolver: ExpressionResolver): View {
        val orientation = data.orientation.evaluate(resolver)
        val view: ViewGroup = when {
            data.isWrapContainer(resolver) -> viewPool.obtain(TAG_WRAP_CONTAINER)
            orientation == Orientation.OVERLAP -> viewPool.obtain(TAG_OVERLAP_CONTAINER)
            else -> viewPool.obtain(TAG_LINEAR_CONTAINER)
        }
        data.items.forEach { childData ->
            view.addView(create(childData, resolver))
        }
        return view
    }

    override fun visit(data: DivGrid, resolver: ExpressionResolver): View {
        val view: DivGridLayout = viewPool.obtain(TAG_GRID)
        data.items.forEach { childData ->
            view.addView(create(childData, resolver))
        }
        return view
    }

    override fun visit(data: DivGallery, resolver: ExpressionResolver): View = viewPool.obtain(TAG_GALLERY)

    override fun visit(data: DivPager, resolver: ExpressionResolver): View = viewPool.obtain(TAG_PAGER)

    override fun visit(data: DivTabs, resolver: ExpressionResolver): View = viewPool.obtain(TAG_TABS)

    override fun visit(data: DivState, resolver: ExpressionResolver): View = viewPool.obtain(TAG_STATE)

    override fun visit(data: DivCustom, resolver: ExpressionResolver): View = viewPool.obtain(TAG_CUSTOM)

    override fun visit(data: DivIndicator, resolver: ExpressionResolver): View = viewPool.obtain(TAG_INDICATOR)

    override fun visit(data: DivSlider, resolver: ExpressionResolver): View = viewPool.obtain(TAG_SLIDER)

    override fun visit(data: DivInput, resolver: ExpressionResolver): View = viewPool.obtain(TAG_INPUT)

    companion object {

        const val TAG_TEXT = "DIV2.TEXT_VIEW"
        const val TAG_IMAGE = "DIV2.IMAGE_VIEW"
        const val TAG_GIF_IMAGE = "DIV2.IMAGE_GIF_VIEW"
        const val TAG_OVERLAP_CONTAINER = "DIV2.OVERLAP_CONTAINER_VIEW"
        const val TAG_LINEAR_CONTAINER = "DIV2.LINEAR_CONTAINER_VIEW"
        const val TAG_WRAP_CONTAINER = "DIV2.WRAP_CONTAINER_VIEW"
        const val TAG_GRID = "DIV2.GRID_VIEW"
        const val TAG_GALLERY = "DIV2.GALLERY_VIEW"
        const val TAG_PAGER = "DIV2.PAGER_VIEW"
        const val TAG_TABS = "DIV2.TAB_VIEW"
        const val TAG_STATE = "DIV2.STATE"
        const val TAG_CUSTOM = "DIV2.CUSTOM"
        const val TAG_INDICATOR = "DIV2.INDICATOR"
        const val TAG_SLIDER = "DIV2.SLIDER"
        const val TAG_INPUT = "DIV2.INPUT"
    }
}
