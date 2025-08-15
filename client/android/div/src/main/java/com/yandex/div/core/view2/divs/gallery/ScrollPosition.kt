package com.yandex.div.core.view2.divs.gallery

import com.yandex.div2.DivGallery

enum class ScrollPosition {
    DEFAULT, CENTER
}

fun DivGallery.ScrollMode.toScrollPosition(): ScrollPosition {
    return when(this) {
        DivGallery.ScrollMode.DEFAULT -> ScrollPosition.DEFAULT
        DivGallery.ScrollMode.PAGING -> ScrollPosition.CENTER
    }
}
