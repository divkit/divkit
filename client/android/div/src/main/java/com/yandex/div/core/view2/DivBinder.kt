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
import com.yandex.div.core.view2.divs.DivPagerBinder
import com.yandex.div.core.view2.divs.DivSelectBinder
import com.yandex.div.core.view2.divs.DivSeparatorBinder
import com.yandex.div.core.view2.divs.DivSliderBinder
import com.yandex.div.core.view2.divs.DivStateBinder
import com.yandex.div.core.view2.divs.DivTextBinder
import com.yandex.div.core.view2.divs.DivVideoBinder
import com.yandex.div.core.view2.divs.PagerIndicatorConnector
import com.yandex.div.core.view2.divs.applyMargins
import com.yandex.div.core.view2.divs.gallery.DivGalleryBinder
import com.yandex.div.core.view2.divs.tabs.DivTabsBinder
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
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
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.internal.widget.tabs.TabsLayout
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSelect
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import com.yandex.div2.DivVideo
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
    private val pagerIndicatorConnector: PagerIndicatorConnector
) {
    @MainThread
    fun bind(view: View, div: Div, divView: Div2View, path: DivStatePath) = suppressExpressionErrors {
        if (!validator.validate(div, divView.expressionResolver)) {
            bindLayoutParams(view, div.value(), divView.expressionResolver)
            return
        }

        extensionController.beforeBindView(divView, view, div.value())

        return when (div) {
            is Div.Text -> bindText(view, div.value, divView)
            is Div.Image -> bindImage(view, div.value, divView)
            is Div.GifImage -> bindGifImage(view, div.value, divView)
            is Div.Separator -> bindSeparator(view, div.value, divView)
            is Div.Container -> bindContainer(view, div.value, divView, path)
            is Div.Grid -> bindGrid(view, div.value, divView, path)
            is Div.Gallery -> bindGallery(view, div.value, divView, path)
            is Div.Pager -> bindPager(view, div.value, divView, path)
            is Div.Tabs -> bindTabs(view, div.value, divView, path)
            is Div.State -> bindState(view, div.value, divView, path)
            is Div.Custom -> bindCustom(view, div.value, divView, path)
            is Div.Indicator -> bindIndicator(view, div.value, divView)
            is Div.Slider -> bindSlider(view, div.value, divView)
            is Div.Input -> bindInput(view, div.value, divView)
            is Div.Select -> bindSelect(view, div.value, divView)
            is Div.Video -> bindVideo(view, div.value, divView)
        }.also {
            // extensionController bound new CustomView in DivCustomBinder after replacing in parent
            if (div !is Div.Custom) {
                extensionController.bindView(divView, view, div.value())
            }
        }
    }

    @MainThread
    internal fun attachIndicators() {
        pagerIndicatorConnector.attach()
    }

    private fun bindText(view: View, data: DivText, divView: Div2View) {
        textBinder.bindView(view as DivLineHeightTextView, data, divView)
    }

    private fun bindImage(view: View, data: DivImage, divView: Div2View) {
        imageBinder.bindView(view as DivImageView, data, divView)
    }

    private fun bindGifImage(view: View, data: DivGifImage, divView: Div2View) {
        gifImageBinder.bindView(view as DivGifImageView, data, divView)
    }

    private fun bindSeparator(view: View, data: DivSeparator, divView: Div2View) {
        separatorBinder.bindView(view as DivSeparatorView, data, divView)
    }

    private fun bindContainer(view: View, data: DivContainer, divView: Div2View, path: DivStatePath) {
        containerBinder.bindView(view as ViewGroup, data, divView, path)
    }

    private fun bindGrid(view: View, data: DivGrid, divView: Div2View, path: DivStatePath) {
        gridBinder.bindView(view as DivGridLayout, data, divView, path)
    }

    private fun bindGallery(view: View, data: DivGallery, divView: Div2View, path: DivStatePath) {
        galleryBinder.bindView(view as DivRecyclerView, data, divView, path)
    }

    private fun bindPager(view: View, data: DivPager, divView: Div2View, path: DivStatePath) {
        pagerBinder.bindView(view as DivPagerView, data, divView, path)
    }

    private fun bindTabs(view: View, data: DivTabs, divView: Div2View, path: DivStatePath) {
        tabsBinder.bindView(view as TabsLayout, data, divView, this, path)
    }

    private fun bindState(view: View, data: DivState, divView: Div2View, path: DivStatePath) {
        stateBinder.bindView(view as DivStateLayout, data, divView, path)
    }

    private fun bindCustom(view: View, data: DivCustom, divView: Div2View, path: DivStatePath) {
        customBinder.bindView(view, data, divView, path)
    }

    private fun bindIndicator(view: View, data: DivIndicator, divView: Div2View) {
        indicatorBinder.bindView(view as DivPagerIndicatorView, data, divView)
    }

    private fun bindSlider(view: View, data: DivSlider, divView: Div2View) {
        sliderBinder.bindView(view as DivSliderView, data, divView)
    }

    private fun bindInput(view: View, data: DivInput, divView: Div2View) {
        inputBinder.bindView(view as DivInputView, data, divView)
    }

    private fun bindSelect(view: View, data: DivSelect, divView: Div2View) {
        selectBinder.bindView(view as DivSelectView, data, divView)
    }

    private fun bindVideo(view: View, data: DivVideo, divView: Div2View) {
        videoBinder.bindView(view as DivVideoView, data, divView)
    }

    private fun bindLayoutParams(view: View, data: DivBase, resolver: ExpressionResolver) {
        view.applyMargins(data.margins, resolver)
    }

    fun setDataWithoutBinding(view: View, div: Div) = when (div) {
        is Div.Text -> (view as DivLineHeightTextView).div = div.value
        is Div.Image -> (view as DivImageView).div = div.value
        is Div.GifImage -> (view as DivGifImageView).div = div.value
        is Div.Separator -> (view as DivSeparatorView).div = div.value
        is Div.Container -> setContainerData(view, div.value)
        is Div.Grid -> setGridData(view, div.value)
        is Div.Gallery -> (view as DivRecyclerView).div = div.value
        is Div.Pager -> (view as DivPagerView).div = div.value
        is Div.Tabs -> (view as TabsLayout).div = div.value
        is Div.State -> (view as DivStateLayout).divState = div.value
        is Div.Custom -> setCustomData(view, div.value)
        is Div.Indicator -> (view as DivPagerIndicatorView).div = div.value
        is Div.Slider -> (view as DivSliderView).div = div.value
        is Div.Input -> (view as DivInputView).div = div.value
        is Div.Select -> (view as DivSelectView).div = div.value
        is Div.Video -> (view as DivVideoView).div = div.value
    }

    private fun setContainerData(view: View, data: DivContainer) {
        containerBinder.setDataWithoutBinding(view as ViewGroup, data)
    }

    private fun setGridData(view: View, data: DivGrid) {
        gridBinder.setDataWithoutBinding(view as DivGridLayout, data)
    }

    private fun setCustomData(view: View, data: DivCustom) {
        customBinder.setDataWithoutBinding(view, data)
    }
}
