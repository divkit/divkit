package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.DivContainerBinder
import com.yandex.div.core.view2.divs.DivCustomBinder
import com.yandex.div.core.view2.divs.DivGifImageBinder
import com.yandex.div.core.view2.divs.DivGridBinder
import com.yandex.div.core.view2.divs.DivImageBinder
import com.yandex.div.core.view2.divs.DivIndicatorBinder
import com.yandex.div.core.view2.divs.DivInputBinder
import com.yandex.div.core.view2.divs.DivSelectBinder
import com.yandex.div.core.view2.divs.DivSeparatorBinder
import com.yandex.div.core.view2.divs.DivSliderBinder
import com.yandex.div.core.view2.divs.DivStateBinder
import com.yandex.div.core.view2.divs.DivSwitchBinder
import com.yandex.div.core.view2.divs.DivTextBinder
import com.yandex.div.core.view2.divs.DivVideoBinder
import com.yandex.div.core.view2.divs.applyMargins
import com.yandex.div.core.view2.divs.gallery.DivGalleryBinder
import com.yandex.div.core.view2.divs.pager.DivPagerBinder
import com.yandex.div.core.view2.divs.pager.PagerIndicatorConnector
import com.yandex.div.core.view2.divs.tabs.DivTabsBinder
import com.yandex.div.core.view2.divs.widgets.DivCustomWrapper
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivSelectView
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.core.view2.divs.widgets.DivSliderView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.DivSwitchView
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.internal.core.getChildContext
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivScope
@Mockable
internal class DivBinder @Inject constructor(
    private val validator: DivValidator,
    private val textBinder: DivTextBinder,
    private val containerBinder: DivContainerBinder,
    private val separatorBinder: DivSeparatorBinder,
    private val imageBinder: DivImageBinder,
    private val gifImageBinder: DivGifImageBinder,
    private val gridBinder: DivGridBinder,
    private val galleryBinder: DivGalleryBinder,
    private val pagerBinder: DivPagerBinder,
    private val tabsBinder: DivTabsBinder,
    private val stateBinder: DivStateBinder,
    private val customBinder: DivCustomBinder,
    private val indicatorBinder: DivIndicatorBinder,
    private val sliderBinder: DivSliderBinder,
    private val inputBinder: DivInputBinder,
    private val selectBinder: DivSelectBinder,
    private val videoBinder: DivVideoBinder,
    private val extensionController: DivExtensionController,
    private val pagerIndicatorConnector: PagerIndicatorConnector,
    private val switchBinder: DivSwitchBinder
) {
    @MainThread
    fun bind(parentContext: BindingContext, view: View, div: Div, path: DivStatePath) = suppressExpressionErrors {
        val context = parentContext.getChildContext(div, path)
        val divView = context.divView
        val resolver = context.expressionResolver
        divView.currentRebindReusableList?.pop(div)?.let {
            return@suppressExpressionErrors
        }

        if (!validator.validate(div, resolver)) {
            bindLayoutParams(view, div.value(), resolver)
            return
        }

        extensionController.beforeBindView(divView, resolver, view, div.value())

        if (div !is Div.Custom) {
            (view as DivHolderView<*>).div?.let { extensionController.unbindView(divView, resolver, view, it.value()) }
        }

        return when (div) {
            is Div.Text -> bindText(context, view, div)
            is Div.Image -> bindImage(context, view, div)
            is Div.GifImage -> bindGifImage(context, view, div)
            is Div.Separator -> bindSeparator(context, view, div)
            is Div.Container -> bindContainer(context, view, div, path)
            is Div.Grid -> bindGrid(context, view, div, path)
            is Div.Gallery -> bindGallery(context, view, div, path)
            is Div.Pager -> bindPager(context, view, div, path)
            is Div.Tabs -> bindTabs(context, view, div, path)
            is Div.State -> bindState(context, view, div, path)
            is Div.Custom -> bindCustom(context, view, div, path)
            is Div.Indicator -> bindIndicator(context, view, div)
            is Div.Slider -> bindSlider(context, view, div, path)
            is Div.Input -> bindInput(context, view, div, path)
            is Div.Select -> bindSelect(context, view, div, path)
            is Div.Video -> bindVideo(context, view, div, path)
            is Div.Switch -> bindSwitch(context, view, div, path)
        }.also {
            // extensionController bound new CustomView in DivCustomBinder after replacing in parent
            if (div !is Div.Custom) {
                extensionController.bindView(divView, resolver, view, div.value())
            }
        }
    }

    @MainThread
    internal fun attachIndicators() {
        pagerIndicatorConnector.attach()
    }

    private fun bindText(context: BindingContext, view: View, data: Div.Text) {
        textBinder.bindView(context, view as DivLineHeightTextView, data)
    }

    private fun bindImage(context: BindingContext, view: View, data: Div.Image) {
        imageBinder.bindView(context, view as DivImageView, data)
    }

    private fun bindGifImage(context: BindingContext, view: View, data: Div.GifImage) {
        gifImageBinder.bindView(context, view as DivGifImageView, data)
    }

    private fun bindSeparator(context: BindingContext, view: View, data: Div.Separator) {
        separatorBinder.bindView(context, view as DivSeparatorView, data)
    }

    private fun bindContainer(context: BindingContext, view: View, data: Div.Container, path: DivStatePath) {
        containerBinder.bindView(context, view as ViewGroup, data, path)
    }

    private fun bindGrid(context: BindingContext, view: View, data: Div.Grid, path: DivStatePath) {
        gridBinder.bindView(context, view as DivGridLayout, data, path)
    }

    private fun bindGallery(context: BindingContext, view: View, data: Div.Gallery, path: DivStatePath) {
        galleryBinder.bindView(context, view as DivRecyclerView, data, path)
    }

    private fun bindPager(context: BindingContext, view: View, data: Div.Pager, path: DivStatePath) {
        pagerBinder.bindView(context, view as DivPagerView, data, path)
    }

    private fun bindTabs(context: BindingContext, view: View, data: Div.Tabs, path: DivStatePath) {
        tabsBinder.bindView(context, view as DivTabsLayout, data, path)
    }

    private fun bindState(context: BindingContext, view: View, data: Div.State, path: DivStatePath) {
        stateBinder.bindView(context, view as DivStateLayout, data, path)
    }

    private fun bindCustom(context: BindingContext, view: View, data: Div.Custom, path: DivStatePath) {
        customBinder.bindView(context, view as DivCustomWrapper, data, path)
    }

    private fun bindIndicator(context: BindingContext, view: View, data: Div.Indicator) {
        indicatorBinder.bindView(context, view as DivPagerIndicatorView, data)
    }

    private fun bindSlider(context: BindingContext, view: View, data: Div.Slider, path: DivStatePath) {
        sliderBinder.bindView(context, view as DivSliderView, data, path)
    }

    private fun bindInput(context: BindingContext, view: View, data: Div.Input, path: DivStatePath) {
        inputBinder.bindView(context, view as DivInputView, data, path)
    }

    private fun bindSelect(context: BindingContext, view: View, data: Div.Select, path: DivStatePath) {
        selectBinder.bindView(context, view as DivSelectView, data, path)
    }

    private fun bindVideo(context: BindingContext, view: View, data: Div.Video, path: DivStatePath) {
        videoBinder.bindView(context, view as DivVideoView, data, path)
    }

    private fun bindSwitch(context: BindingContext, view: View, data: Div.Switch, path: DivStatePath) {
        switchBinder.bindView(context, view as DivSwitchView, data, path)
    }

    private fun bindLayoutParams(view: View, data: DivBase, resolver: ExpressionResolver) {
        view.applyMargins(data.margins, resolver)
    }

    fun setDataWithoutBinding(context: BindingContext, view: View, div: Div) = when (div) {
        is Div.Text -> (view as DivLineHeightTextView).setDataWithoutBinding(context, div)
        is Div.Image -> (view as DivImageView).setDataWithoutBinding(context, div)
        is Div.GifImage -> (view as DivGifImageView).setDataWithoutBinding(context, div)
        is Div.Separator -> (view as DivSeparatorView).setDataWithoutBinding(context, div)
        is Div.Container -> setContainerData(context, view, div)
        is Div.Grid -> setGridData(context, view, div)
        is Div.Gallery -> (view as DivRecyclerView).setDataWithoutBinding(context, div)
        is Div.Pager -> (view as DivPagerView).setDataWithoutBinding(context, div)
        is Div.Tabs -> (view as DivTabsLayout).setDataWithoutBinding(context, div)
        is Div.State -> (view as DivStateLayout).setDataWithoutBinding(context, div)
        is Div.Custom -> (view as DivCustomWrapper).setDataWithoutBinding(context, div)
        is Div.Indicator -> (view as DivPagerIndicatorView).setDataWithoutBinding(context, div)
        is Div.Slider -> (view as DivSliderView).setDataWithoutBinding(context, div)
        is Div.Input -> (view as DivInputView).setDataWithoutBinding(context, div)
        is Div.Select -> (view as DivSelectView).setDataWithoutBinding(context, div)
        is Div.Video -> (view as DivVideoView).setDataWithoutBinding(context, div)
        is Div.Switch -> (view as DivSwitchView).setDataWithoutBinding(context, div)
    }

    private fun <T: Div> DivHolderView<T>.setDataWithoutBinding(context: BindingContext, newDiv: T) {
        div = newDiv
        bindingContext = context
    }

    private fun setContainerData(context: BindingContext, view: View, data: Div.Container) {
        containerBinder.setDataWithoutBinding(context, view as ViewGroup, data)
    }

    private fun setGridData(context: BindingContext, view: View, data: Div.Grid) {
        gridBinder.setDataWithoutBinding(context, view as DivGridLayout, data)
    }
}
