package com.yandex.div.core.util

/**
 * Class to store either of two representations of the image.
 * Bitmap for raster images, picture drawable for vector images.
 */
sealed class ImageRepresentation<out T> {
    data class Bitmap<out Bitmap>(val value: Bitmap?) : ImageRepresentation<Bitmap>()
    data class PictureDrawable<out PictureDrawable>(val value: PictureDrawable?) : ImageRepresentation<PictureDrawable>()
}
