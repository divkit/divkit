package com.yandex.div.compose.patch

import com.yandex.div.compose.utils.children
import com.yandex.div2.Div
import com.yandex.div2.DivBase

internal fun Div.copyForPatch(): Div {
    val tooltips = value().tooltips?.map {
        it.copy(div = it.div.copyForPatch(), substrateDiv = it.substrateDiv?.copyForPatch())
    }
    return when (this) {
        is Div.Container -> Div.Container(value.copy(
            items = value.items?.map { it.copyForPatch() },
            tooltips = tooltips
        ))
        is Div.Custom -> Div.Custom(value.copy(items = value.items?.map { it.copyForPatch() }, tooltips = tooltips))
        is Div.Gallery -> Div.Gallery(value.copy(items = value.items?.map { it.copyForPatch() }, tooltips = tooltips))
        is Div.Grid -> Div.Grid(value.copy(items = value.items?.map { it.copyForPatch() }, tooltips = tooltips))
        is Div.Pager -> Div.Pager(value.copy(items = value.items?.map { it.copyForPatch() }, tooltips = tooltips))
        is Div.State -> Div.State(value.copy(
            states = value.states.map { it.copy(div = it.div?.copyForPatch()) },
            tooltips = tooltips
        ))
        is Div.Tabs -> Div.Tabs(value.copy(
            items = value.items.map { it.copy(div = it.div.copyForPatch()) },
            tooltips = tooltips
        ))
        is Div.GifImage -> Div.GifImage(value.copy(tooltips = tooltips))
        is Div.Image -> Div.Image(value.copy(tooltips = tooltips))
        is Div.Indicator -> Div.Indicator(value.copy(tooltips = tooltips))
        is Div.Input -> Div.Input(value.copy(tooltips = tooltips))
        is Div.Select -> Div.Select(value.copy(tooltips = tooltips))
        is Div.Separator -> Div.Separator(value.copy(tooltips = tooltips))
        is Div.Slider -> Div.Slider(value.copy(tooltips = tooltips))
        is Div.Switch -> Div.Switch(value.copy(tooltips = tooltips))
        is Div.Text -> Div.Text(value.copy(tooltips = tooltips))
        is Div.Video -> Div.Video(value.copy(tooltips = tooltips))
    }
}

internal fun Div.collectDivs(result: MutableSet<DivBase>) {
    val base = value()
    if (!result.add(base)) return
    children().forEach { it.collectDivs(result) }
    base.tooltips?.forEach {
        it.div.collectDivs(result)
        it.substrateDiv?.collectDivs(result)
    }
}
