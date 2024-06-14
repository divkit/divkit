package com.yandex.div.core.images

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    DivImagePriority.IMAGES_PRIORITY_DEFAULT,
    DivImagePriority.IMAGES_PRIORITY_PRELOAD
)
public annotation class DivImagePriority {
    public companion object {
        public const val IMAGES_PRIORITY_PRELOAD: Int = -1
        public const val IMAGES_PRIORITY_DEFAULT: Int = 0
    }
}
