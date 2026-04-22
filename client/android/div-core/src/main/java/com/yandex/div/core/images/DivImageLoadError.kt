package com.yandex.div.core.images

public class DivImageLoadError(imageUrl: String, t: Throwable) :
    Throwable("Failed to load image with url='$imageUrl'", t) {

    public companion object {
        @JvmStatic
        public fun Throwable?.toDivImageLoadError(imageUrl: String): DivImageLoadError? =
            this?.let { DivImageLoadError(imageUrl, it) }
    }
}
