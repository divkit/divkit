package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPageSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivPercentageSize
import com.yandex.div2.DivSize

fun pager(
    id: String? = null,
    items: List<Div> = emptyList(),
    height: DivSize = wrapContent(),
    width: DivSize = matchParent(),
    defaultItem: Long = 0,
    infiniteScroll: Boolean = false,
    layoutMode: DivPagerLayoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
    orientation: DivPager.Orientation = DivPager.Orientation.HORIZONTAL,
    scrollAxisAlignment: DivPager.ItemAlignment = DivPager.ItemAlignment.CENTER,
): Div.Pager = Div.Pager(DivPager(
    defaultItem = constant(defaultItem),
    infiniteScroll = constant(infiniteScroll),
    id = id,
    items = items,
    height = height,
    width = width,
    layoutMode = layoutMode,
    orientation = constant(orientation),
    scrollAxisAlignment = constant(scrollAxisAlignment),
))

fun pageSize(percentage: Double): DivPagerLayoutMode =
    DivPagerLayoutMode.PageSize(DivPageSize(pageWidth = DivPercentageSize(value = constant(percentage))))
