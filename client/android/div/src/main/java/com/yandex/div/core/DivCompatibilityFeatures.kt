package com.yandex.div.core

/**
 * Opt-in features for hosts whose markup relies on how earlier DivKit versions handled it.
 * All features are disabled by default.
 *
 * @property hideImagesWithoutUrl lays out `image` without `image_url` and `gif` without `gif_url`
 * as `visibility: gone` instead of showing a placeholder. The element is hidden without visibility
 * transitions and visibility actions. Elements with any URL, including `empty://`, are not affected.
 */
class DivCompatibilityFeatures @JvmOverloads constructor(
    val hideImagesWithoutUrl: Boolean = false,
)
