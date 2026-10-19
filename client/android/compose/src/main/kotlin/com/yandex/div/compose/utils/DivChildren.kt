package com.yandex.div.compose.utils

import com.yandex.div2.Div

internal fun Div.children(): List<Div> = when (this) {
    is Div.Container -> value.items.orEmpty()
    is Div.Custom -> value.items.orEmpty()
    is Div.Gallery -> value.items.orEmpty()
    is Div.Grid -> value.items.orEmpty()
    is Div.Pager -> value.items.orEmpty()
    is Div.State -> value.states.mapNotNull { it.div }
    is Div.Tabs -> value.items.map { it.div }
    is Div.GifImage, is Div.Image, is Div.Indicator, is Div.Input,
    is Div.Select, is Div.Separator, is Div.Slider, is Div.Switch,
    is Div.Text, is Div.Video -> emptyList()
}
