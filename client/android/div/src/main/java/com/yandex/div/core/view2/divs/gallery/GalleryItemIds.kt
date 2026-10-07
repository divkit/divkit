package com.yandex.div.core.view2.divs.gallery

import com.yandex.div.internal.core.DivBlock
import java.util.WeakHashMap

internal class GalleryItemIds {
    private val ids = WeakHashMap<DivBlock, Long>()
    private var nextId = 0L

    fun get(item: DivBlock): Long = ids[item] ?: (nextId++).also { ids[item] = it }
}
