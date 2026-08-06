package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
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
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import com.yandex.div2.DivVideo

@InternalApi
sealed class DivBlock(
    val div: Div,
    val expressionResolver: ExpressionResolver,
    val path: DivStatePath,
) {
    class Image(divImage: Div.Image, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divImage, expressionResolver, path) {
        val divValue: DivImage = divImage.value
    }
    class GifImage(divGifImage: Div.GifImage, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divGifImage, expressionResolver, path) {
        val divValue: DivGifImage = divGifImage.value
    }
    class Text(divText: Div.Text, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divText, expressionResolver, path) {
        val divValue: DivText = divText.value
    }
    class Separator(divSeparator: Div.Separator, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divSeparator, expressionResolver, path) {
        val divValue: DivSeparator = divSeparator.value
    }
    class Container(divContainer: Div.Container, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divContainer, expressionResolver, path) {
        val divValue: DivContainer = divContainer.value
    }
    class Grid(divGrid: Div.Grid, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divGrid, expressionResolver, path) {
        val divValue: DivGrid = divGrid.value
    }
    class Gallery(divGallery: Div.Gallery, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divGallery, expressionResolver, path) {
        val divValue: DivGallery = divGallery.value
    }
    class Pager(divPager: Div.Pager, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divPager, expressionResolver, path) {
        val divValue: DivPager = divPager.value
    }
    class Tabs(divTabs: Div.Tabs, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divTabs, expressionResolver, path) {
        val divValue: DivTabs = divTabs.value
    }
    class State(divState: Div.State, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divState, expressionResolver, path) {
        val divValue: DivState = divState.value
    }
    class Custom(divCustom: Div.Custom, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divCustom, expressionResolver, path) {
        val divValue: DivCustom = divCustom.value
    }
    class Indicator(divIndicator: Div.Indicator, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divIndicator, expressionResolver, path) {
        val divValue: DivIndicator = divIndicator.value
    }
    class Slider(divSlider: Div.Slider, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divSlider, expressionResolver, path) {
        val divValue: DivSlider = divSlider.value
    }
    class Switch(divSwitch: Div.Switch, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divSwitch, expressionResolver, path) {
        val divValue: DivSwitch = divSwitch.value
    }
    class Input(divInput: Div.Input, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divInput, expressionResolver, path) {
        val divValue: DivInput = divInput.value
    }
    class Select(divSelect: Div.Select, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divSelect, expressionResolver, path) {
        val divValue: DivSelect = divSelect.value
    }
    class Video(divVideo: Div.Video, expressionResolver: ExpressionResolver, path: DivStatePath)
        : DivBlock(divVideo, expressionResolver, path) {
        val divValue: DivVideo = divVideo.value
    }

    companion object {
        fun create(div: Div, expressionResolver: ExpressionResolver, path: DivStatePath): DivBlock {
            return when (div) {
                is Div.Image -> Image(div, expressionResolver, path)
                is Div.GifImage -> GifImage(div, expressionResolver, path)
                is Div.Text -> Text(div, expressionResolver, path)
                is Div.Separator -> Separator(div, expressionResolver, path)
                is Div.Container -> Container(div, expressionResolver, path)
                is Div.Grid -> Grid(div, expressionResolver, path)
                is Div.Gallery -> Gallery(div, expressionResolver, path)
                is Div.Pager -> Pager(div, expressionResolver, path)
                is Div.Tabs -> Tabs(div, expressionResolver, path)
                is Div.State -> State(div, expressionResolver, path)
                is Div.Custom -> Custom(div, expressionResolver, path)
                is Div.Indicator -> Indicator(div, expressionResolver, path)
                is Div.Slider -> Slider(div, expressionResolver, path)
                is Div.Switch -> Switch(div, expressionResolver, path)
                is Div.Input -> Input(div, expressionResolver, path)
                is Div.Select -> Select(div, expressionResolver, path)
                is Div.Video -> Video(div, expressionResolver, path)
            }
        }
    }
}
