package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivExtension
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPageSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivPercentageSize
import com.yandex.div2.DivSize

fun pager(
    defaultItem: Long = 0,
    extensions: List<DivExtension>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    infiniteScroll: Boolean = false,
    itemBuilder: DivCollectionItemBuilder? = null,
    itemCountVariable: String? = null,
    itemSpacing: Expression<Long> = constant(0L),
    items: List<Div>? = emptyList(),
    layoutMode: DivPagerLayoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
    orientation: DivPager.Orientation = DivPager.Orientation.HORIZONTAL,
    restrictParentScroll: Expression<Boolean> = constant(false),
    scrollAxisAlignment: DivPager.ItemAlignment = DivPager.ItemAlignment.CENTER,
    width: DivSize = matchParent(),
): Div.Pager {
    return Div.Pager(
        DivPager(
            defaultItem = constant(defaultItem),
            extensions = extensions,
            height = height,
            id = id,
            infiniteScroll = constant(infiniteScroll),
            itemCountVariable = itemCountVariable,
            itemBuilder = itemBuilder,
            itemSpacing = DivFixedSize(value = itemSpacing),
            items = items,
            width = width,
            layoutMode = layoutMode,
            orientation = constant(orientation),
            restrictParentScroll = restrictParentScroll,
            scrollAxisAlignment = constant(scrollAxisAlignment),
        )
    )
}

fun pageSize(percentage: Double): DivPagerLayoutMode {
    return DivPagerLayoutMode.PageSize(
        DivPageSize(pageWidth = DivPercentageSize(value = constant(percentage)))
    )
}
