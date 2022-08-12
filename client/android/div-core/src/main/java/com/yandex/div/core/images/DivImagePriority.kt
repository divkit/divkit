package com.yandex.div.core.images

import androidx.annotation.IntDef

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(
    DivImagePriority.IMAGES_PRIORITY_DEFAULT,
    DivImagePriority.IMAGES_PRIORITY_PRELOAD
)
annotation class DivImagePriority {
    companion object {
        const val IMAGES_PRIORITY_PRELOAD = -1
        const val IMAGES_PRIORITY_DEFAULT = 0
    }
}