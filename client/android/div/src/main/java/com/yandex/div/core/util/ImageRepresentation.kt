package com.yandex.div.core.util

/**
 * Class to store either of two representations of the image.
 * Bitmap for raster images, picture drawable for vector images.
 */
sealed interface ImageRepresentation {
    @JvmInline
    value class Bitmap(val value: android.graphics.Bitmap) : ImageRepresentation
    @JvmInline
    value class PictureDrawable(val value: android.graphics.drawable.PictureDrawable) : ImageRepresentation
}
