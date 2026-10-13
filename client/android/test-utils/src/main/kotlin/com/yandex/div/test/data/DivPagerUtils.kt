package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivSize

fun pager(
    id: String? = null,
    items: List<Div> = emptyList(),
    height: DivSize = wrapContent(),
    width: DivSize = matchParent(),
): Div.Pager = Div.Pager(DivPager(
    id = id,
    items = items,
    height = height,
    width = width,
    layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
))
