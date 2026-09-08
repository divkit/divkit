package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivSize
import com.yandex.div2.DivTabs

fun tabs(
    dynamicHeight: Expression<Boolean> = constant(false),
    height: DivSize = wrapContent(),
    id: String? = null,
    items: List<DivTabs.Item>,
    selectedTab: Expression<Long> = constant(0L),
    switchTabsByContentSwipeEnabled: Expression<Boolean> = constant(true),
    width: DivSize = matchParent(),
): Div {
    return Div.Tabs(
        value = DivTabs(
            dynamicHeight = dynamicHeight,
            height = height,
            id = id,
            items = items,
            selectedTab = selectedTab,
            switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled,
            width = width
        )
    )
}

fun tabItem(
    div: Div,
    title: String,
    titleClickAction: DivAction? = null,
) = DivTabs.Item(
    div = div,
    title = constant(title),
    titleClickAction = titleClickAction
)
