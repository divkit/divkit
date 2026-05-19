package com.yandex.div.core.view2.divs.gallery

import com.yandex.div2.DivGallery

@Deprecated("Not used in project. Will be removed in major release.")
enum class ScrollPosition {
    DEFAULT, CENTER
}

@Deprecated("Not used in project. Will be removed in major release.")
fun DivGallery.ScrollMode.toScrollPosition(): ScrollPosition {
    return when(this) {
        DivGallery.ScrollMode.DEFAULT -> ScrollPosition.DEFAULT
        DivGallery.ScrollMode.PAGING -> ScrollPosition.CENTER
    }
}
