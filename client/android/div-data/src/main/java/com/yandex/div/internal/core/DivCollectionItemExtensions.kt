package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div2.Div

@InternalApi
const val INDEX_VARIABLE_NAME = "index"

/**
 * Copies the div hierarchy so that every element built from a collection item prototype has its
 * own identity and the id from the prototype overrides the element id.
 */
@InternalApi
fun Div.copy(id: String? = value().id): Div {
    val tooltips = value().tooltips?.map {
        it.copy(div = it.div.copy(), substrateDiv = it.substrateDiv?.copy())
    }
    return when (this) {
        is Div.Image -> Div.Image(value.copy(id = id, tooltips = tooltips))
        is Div.GifImage -> Div.GifImage(value.copy(id = id, tooltips = tooltips))
        is Div.Text -> Div.Text(value.copy(id = id, tooltips = tooltips))
        is Div.Separator -> Div.Separator(value.copy(id = id, tooltips = tooltips))
        is Div.Container -> Div.Container(value.copy(
            id = id,
            items = value.items?.map { it.copy() },
            tooltips = tooltips,
        ))
        is Div.Grid -> Div.Grid(value.copy(
            id = id,
            items = value.items?.map { it.copy() },
            tooltips = tooltips,
        ))
        is Div.Gallery -> Div.Gallery(value.copy(
            id = id,
            items = value.items?.map { it.copy() },
            tooltips = tooltips,
        ))
        is Div.Pager -> Div.Pager(value.copy(
            id = id,
            items = value.items?.map { it.copy() },
            tooltips = tooltips,
        ))
        is Div.Tabs -> Div.Tabs(value.copy(
            id = id,
            items = value.items.map { it.copy(div = it.div.copy()) },
            tooltips = tooltips,
        ))
        is Div.State -> Div.State(value.copy(
            id = id,
            divId = id,
            states = value.states.map { it.copy(div = it.div?.copy()) },
            tooltips = tooltips,
        ))
        is Div.Custom -> Div.Custom(value.copy(
            id = id,
            items = value.items?.map { it.copy() },
            tooltips = tooltips,
        ))
        is Div.Indicator -> Div.Indicator(value.copy(id = id, tooltips = tooltips))
        is Div.Slider -> Div.Slider(value.copy(id = id, tooltips = tooltips))
        is Div.Input -> Div.Input(value.copy(id = id, tooltips = tooltips))
        is Div.Select -> Div.Select(value.copy(id = id, tooltips = tooltips))
        is Div.Video -> Div.Video(value.copy(id = id, tooltips = tooltips))
        is Div.Switch -> Div.Switch(value.copy(id = id, tooltips = tooltips))
    }
}
