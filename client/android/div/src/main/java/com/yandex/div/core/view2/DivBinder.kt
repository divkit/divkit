package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.extension.DivExtensionController
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
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.view.DivImageView
import javax.inject.Inject

@DivScope
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

    fun bind(view: View, divBlock: DivBlock, divView: Div2View) = suppressExpressionErrors {
        divView.currentRebindReusableList?.pop(divBlock.div)?.let {
            return@suppressExpressionErrors
        }

        if (!validator.validate(divBlock.div, divBlock.expressionResolver)) {
            bindLayoutParams(view, divBlock)
            return
        }

        extensionController.beforeBindView(view, divBlock, divView)

        if (divBlock !is DivBlock.Custom) {
            (view as DivHolderView<*>).divBlock?.let { extensionController.unbindView(view, it, divView) }
        }

        return when (divBlock) {
            is DivBlock.Text -> bindText(view, divBlock, divView)
            is DivBlock.Image -> bindImage(view, divBlock, divView)
            is DivBlock.GifImage -> bindGifImage(view, divBlock, divView)
            is DivBlock.Separator -> bindSeparator(view, divBlock, divView)
            is DivBlock.Container -> bindContainer(view, divBlock, divView)
            is DivBlock.Grid -> bindGrid(view, divBlock, divView)
            is DivBlock.Gallery -> bindGallery(view, divBlock, divView)
            is DivBlock.Pager -> bindPager(view, divBlock, divView)
            is DivBlock.Tabs -> bindTabs(view, divBlock, divView)
            is DivBlock.State -> bindState(view, divBlock, divView)
            is DivBlock.Custom -> bindCustom(view, divBlock, divView)
            is DivBlock.Indicator -> bindIndicator(view, divBlock, divView)
            is DivBlock.Slider -> bindSlider(view, divBlock, divView)
            is DivBlock.Input -> bindInput(view, divBlock, divView)
            is DivBlock.Select -> bindSelect(view, divBlock, divView)
            is DivBlock.Video -> bindVideo(view, divBlock, divView)
            is DivBlock.Switch -> bindSwitch(view, divBlock, divView)
        }.also {
            // extensionController bound new CustomView in DivCustomBinder after replacing in parent
            if (divBlock !is DivBlock.Custom) {
                extensionController.bindView(view, divBlock, divView)
            }
        }
    }

    internal fun attachIndicators(divView: Div2View) = divView.runMainThreadAction {
        pagerIndicatorConnector.attach()
    }

    private fun bindText(view: View, divBlock: DivBlock.Text, divView: Div2View) {
        textBinder.bindView(view as DivLineHeightTextView, divBlock, divView)
    }

    private fun bindImage(view: View, divBlock: DivBlock.Image, divView: Div2View) {
        imageBinder.bindView(view as DivImageView, divBlock, divView)
    }

    private fun bindGifImage(view: View, divBlock: DivBlock.GifImage, divView: Div2View) {
        gifImageBinder.bindView(view as DivGifImageView, divBlock, divView)
    }

    private fun bindSeparator(view: View, divBlock: DivBlock.Separator, divView: Div2View) {
        separatorBinder.bindView(view as DivSeparatorView, divBlock, divView)
    }

    private fun bindContainer(view: View, divBlock: DivBlock.Container, divView: Div2View) {
        containerBinder.bindView(view as ViewGroup, divBlock, divView)
    }

    private fun bindGrid(view: View, divBlock: DivBlock.Grid, divView: Div2View) {
        gridBinder.bindView(view as DivGridLayout, divBlock, divView)
    }

    private fun bindGallery(view: View, divBlock: DivBlock.Gallery, divView: Div2View) {
        galleryBinder.bindView(view as DivRecyclerView, divBlock, divView)
    }

    private fun bindPager(view: View, divBlock: DivBlock.Pager, divView: Div2View) {
        pagerBinder.bindView(view as DivPagerView, divBlock, divView)
    }

    private fun bindTabs(view: View, divBlock: DivBlock.Tabs, divView: Div2View) {
        tabsBinder.bindView(view as DivTabsLayout, divBlock, divView)
    }

    private fun bindState(view: View, divBlock: DivBlock.State, divView: Div2View) {
        stateBinder.bindView(view as DivStateLayout, divBlock, divView)
    }

    private fun bindCustom(view: View, divBlock: DivBlock.Custom, divView: Div2View) {
        customBinder.bindView(view as DivCustomWrapper, divBlock, divView)
    }

    private fun bindIndicator(view: View, divBlock: DivBlock.Indicator, divView: Div2View) {
        indicatorBinder.bindView(view as DivPagerIndicatorView, divBlock, divView)
    }

    private fun bindSlider(view: View, divBlock: DivBlock.Slider, divView: Div2View) {
        sliderBinder.bindView(view as DivSliderView, divBlock, divView)
    }

    private fun bindInput(view: View, divBlock: DivBlock.Input, divView: Div2View) {
        inputBinder.bindView(view as DivInputView, divBlock, divView)
    }

    private fun bindSelect(view: View, divBlock: DivBlock.Select, divView: Div2View) {
        selectBinder.bindView(view as DivSelectView, divBlock, divView)
    }

    private fun bindVideo(view: View, divBlock: DivBlock.Video, divView: Div2View) {
        videoBinder.bindView(view as DivVideoView, divBlock, divView)
    }

    private fun bindSwitch(view: View, divBlock: DivBlock.Switch, divView: Div2View) {
        switchBinder.bindView(view as DivSwitchView, divBlock, divView)
    }

    private fun bindLayoutParams(view: View, divBlock: DivBlock) {
        view.applyMargins(divBlock.div.value().margins, divBlock.expressionResolver)
    }
}
