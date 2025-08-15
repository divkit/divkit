package com.yandex.div.core.image

/**
 * Transform image url before loading with provided instance of DivImageLoader.
 */
internal fun interface DivImageUrlModifier {
    /**
     * Modifies image url.
     *
     * @param imageUrl - initial image_url from layout.
     */
    fun modifyImageUrl(imageUrl: String): String
}
